package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import dao.CommandeDAO;
import dao.CommandeDAO.Client;
import modele.Commande;

public class NouvelleCommandePanel extends JDialog {

    private final Connection dbConnection;
    private CommandeDAO commandeDAO;
    private CommandeView parentView;
    
    // Couleurs modernes identiques à CommandeView
    private final Color primaryGreen = new Color(34, 197, 94);
    private final Color lightGray = new Color(249, 250, 251);
    private final Color darkText = new Color(31, 41, 55);
    private final Color lightText = new Color(107, 114, 128);
    private final Color borderColor = new Color(229, 231, 235);
    private final Color hoverColor = new Color(243, 244, 246);
    
    // Composants du formulaire Client
    private JTextField prenomField;
    private JTextField nomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextArea adresseArea;
    
    // Composants du formulaire Commande
    private JTextField articleField;
    private JTextField totalField;
    private JComboBox<String> statutCombo;
    
    // Composants du formulaire Planification - UTILISANT DES JTEXTFIELD SIMPLES POUR LES DATES
    private JTextField dateRecuperationField;
    private JTextField dateLivraisonField;
    private JComboBox<String> prioriteCombo;
    
    // Variables de validation
    private boolean clientValide = false;
    private boolean commandeValide = false;
    private boolean planificationValide = false;

    public NouvelleCommandePanel(Frame parent, Connection connection, CommandeView commandeView) {
        super(parent, "Nouvelle Commande", true);
        this.dbConnection = connection;
        this.parentView = commandeView;
        this.commandeDAO = new CommandeDAO();
        
        initComponents();
        setupValidation();
        setupDialog();
    }
    
    private void initComponents() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        // Panel principal avec ombre optimisée
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée plus sophistiquée
                g2d.setColor(new Color(0, 0, 0, 50));
                for (int i = 0; i < 25; i++) {
                    int alpha = Math.max(0, 20 - i * 2);
                    g2d.setColor(new Color(255, 222, 222, alpha));
                    g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 35 - i, 35 - i);
                }
                
                // Fond principal avec légère teinte
                g2d.setColor(new Color(255, 255, 255, 255));
                g2d.fillRoundRect(25, 25, getWidth() - 50, getHeight() - 50, 32, 32);
                
                // Pas de bordure pour un look plus propre
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding réduit
        
        // Panel de contenu avec dimensions réduites
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25)); // Padding réduit
        contentPanel.setOpaque(false);
        
        // En-tête
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Formulaire avec scroll
        JScrollPane formScrollPane = createFormPanel();
        contentPanel.add(formScrollPane, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient sophistiqué
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(34, 197, 94, 12),
                    getWidth(), 0, new Color(59, 130, 246, 8)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                
                // Bordure gauche accentuée avec gradient
                GradientPaint accentGradient = new GradientPaint(
                    0, 0, primaryGreen,
                    0, getHeight(), new Color(59, 130, 246)
                );
                g2d.setPaint(accentGradient);
                g2d.fillRoundRect(0, 0, 7, getHeight(), 22, 22);
                
                // Accent supérieur fin
                g2d.setPaint(new GradientPaint(
                    0, 0, new Color(255, 255, 255, 80),
                    getWidth(), 0, new Color(255, 255, 255, 40)
                ));
                g2d.fillRoundRect(0, 0, getWidth(), 3, 22, 22);
                
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24)); // Padding réduit
        
        // Section titre améliorée
        JPanel titleSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleSection.setOpaque(false);
        
        // Icône avec ombre
        JLabel iconLabel = new JLabel("➕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre de l'icône
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 2, getHeight() / 2 + 7);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Nouvelle Commande");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 26));
        titleLabel.setForeground(darkText);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Créer une nouvelle commande en 3 étapes");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        subtitleLabel.setForeground(lightText);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        titleSection.add(iconLabel);
        titleSection.add(textPanel);
        
        headerPanel.add(titleSection, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JScrollPane createFormPanel() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        // Section 1: Informations Client
        JPanel clientSection = createClientSection();
        formContainer.add(clientSection);
        formContainer.add(Box.createVerticalStrut(20)); // Espacement réduit
        
        // Section 2: Informations Commande
        JPanel commandeSection = createCommandeSection();
        formContainer.add(commandeSection);
        formContainer.add(Box.createVerticalStrut(20)); // Espacement réduit
        
        // Section 3: Planification
        JPanel planificationSection = createPlanificationSection();
        formContainer.add(planificationSection);
        formContainer.add(Box.createVerticalStrut(15)); // Espacement réduit
        
        // Scroll pane avec dimensions réduites
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(600, 320)); // Dimensions réduites
        
        return scrollPane;
    }
    
    private JPanel createClientSection() {
        JPanel section = createFormSection("👤 Informations Client", 
            "Renseignez les coordonnées du client", new Color(59, 130, 246));
        
        // Grille pour prénom et nom - espacement réduit
        JPanel nameGrid = new JPanel(new GridLayout(1, 2, 20, 0)); // Espacement réduit
        nameGrid.setBackground(Color.WHITE);
        
        prenomField = createEnhancedTextField("", "Ex: Mulho");
        nomField = createEnhancedTextField("", "Ex: Ibrahim");
        
        nameGrid.add(createFieldRow("Prénom *", "Prénom du client", prenomField));
        nameGrid.add(createFieldRow("Nom *", "Nom de famille", nomField));
        
        section.add(nameGrid);
        section.add(Box.createVerticalStrut(12)); // Espacement réduit
        
        // Email et téléphone - espacement réduit
        JPanel contactGrid = new JPanel(new GridLayout(1, 2, 20, 0)); // Espacement réduit
        contactGrid.setBackground(Color.WHITE);
        
        emailField = createEnhancedTextField("", "exemple@email.com");
        telephoneField = createEnhancedTextField("", "+221787550290");
        
        contactGrid.add(createFieldRow("Email *", "Adresse email du client", emailField));
        contactGrid.add(createFieldRow("Téléphone *", "Numéro de téléphone", telephoneField));
        
        section.add(contactGrid);
        section.add(Box.createVerticalStrut(12)); // Espacement réduit
        
        // Adresse avec dimensions réduites
        adresseArea = createEnhancedTextArea("", "Adresse complète du client");
        JScrollPane adresseScroll = new JScrollPane(adresseArea);
        adresseScroll.setBorder(null);
        adresseScroll.setOpaque(false);
        adresseScroll.getViewport().setOpaque(false);
        adresseScroll.setPreferredSize(new Dimension(0, 70)); // Hauteur réduite
        
        section.add(createFieldRow("Adresse *", "Adresse complète", adresseScroll));
        
        return section;
    }
    
    private JPanel createCommandeSection() {
        JPanel section = createFormSection("📦 Détails de la Commande", 
            "Informations sur les articles et le montant", primaryGreen);
        
        // Article avec espacement réduit
        articleField = createEnhancedTextField("", "Ex: Nettoyage costume 3 pièces");
        section.add(createFieldRow("Article *", "Description détaillée de l'article", articleField));
        section.add(Box.createVerticalStrut(12)); // Espacement réduit
        
        // Montant et statut - espacement réduit
        JPanel commandeGrid = new JPanel(new GridLayout(1, 2, 20, 0)); // Espacement réduit
        commandeGrid.setBackground(Color.WHITE);
        
        totalField = createEnhancedTextField("", "0.00");
        totalField.setFont(new Font("Inter", Font.BOLD, 15));
        totalField.setForeground(primaryGreen);
        
        String[] statutOptions = {"en cours", "en attente", "terminé"};
        statutCombo = createEnhancedComboBox();
        for (String option : statutOptions) {
            statutCombo.addItem(option);
        }
        statutCombo.setSelectedItem("en cours");
        
        commandeGrid.add(createFieldRow("Montant (€) *", "Prix total de la commande", totalField));
        commandeGrid.add(createFieldRow("Statut", "État initial de la commande", statutCombo));
        
        section.add(commandeGrid);
        
        return section;
    }
    
    private JPanel createPlanificationSection() {
        JPanel section = createFormSection("📅 Planification", 
            "Dates importantes et priorité", new Color(139, 92, 246));
        
        // Dates avec JTextField et boutons
        JPanel dateGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        dateGrid.setBackground(Color.WHITE);
        
        // Date de récupération avec date du jour par défaut
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayStr = dateFormat.format(new Date());
        JPanel dateRecPanel = createDateFieldWithButton(todayStr, "dateRecuperation");
        dateRecuperationField = (JTextField) ((JPanel) dateRecPanel.getComponent(0)).getComponent(0);
        
        // Date de livraison par défaut : 3 jours après récupération
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        String defaultLivraisonStr = dateFormat.format(cal.getTime());
        JPanel dateLivPanel = createDateFieldWithButton(defaultLivraisonStr, "dateLivraison");
        dateLivraisonField = (JTextField) ((JPanel) dateLivPanel.getComponent(0)).getComponent(0);
        
        dateGrid.add(createFieldRow("Date de Récupération *", "Cliquez sur le calendrier pour sélectionner", dateRecPanel));
        dateGrid.add(createFieldRow("Date de Livraison *", "Cliquez sur le calendrier pour sélectionner", dateLivPanel));
        
        section.add(dateGrid);
        section.add(Box.createVerticalStrut(12));
        
        // Priorité
        JPanel prioriteContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        prioriteContainer.setBackground(Color.WHITE);
        prioriteContainer.setOpaque(false);
        
        String[] prioriteOptions = {"normale", "urgente"};
        prioriteCombo = createEnhancedComboBox();
        for (String option : prioriteOptions) {
            prioriteCombo.addItem(option);
        }
        prioriteCombo.setSelectedItem("normale");
        prioriteCombo.setPreferredSize(new Dimension(200, 52));
        
        JPanel prioriteFieldContainer = createFieldRow("Priorité", "Niveau d'urgence de la commande", prioriteCombo);
        prioriteContainer.add(prioriteFieldContainer);
        
        section.add(prioriteContainer);
        
        return section;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(18, 0, 0, 0)); // Padding réduit
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createEnhancedButton("Annuler", new Color(107, 114, 128), Color.WHITE, 150, 48);
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = createEnhancedButton("💾 Créer la Commande", primaryGreen, Color.WHITE, 220, 48);
        saveButton.addActionListener(e -> creerCommande());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    private void setupValidation() {
        // Validation client en temps réel
        DocumentListener clientValidator = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateClient(); }
            public void removeUpdate(DocumentEvent e) { validateClient(); }
            public void changedUpdate(DocumentEvent e) { validateClient(); }
        };
        
        prenomField.getDocument().addDocumentListener(clientValidator);
        nomField.getDocument().addDocumentListener(clientValidator);
        emailField.getDocument().addDocumentListener(clientValidator);
        telephoneField.getDocument().addDocumentListener(clientValidator);
        adresseArea.getDocument().addDocumentListener(clientValidator);
        
        // Validation commande
        DocumentListener commandeValidator = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateCommande(); }
            public void removeUpdate(DocumentEvent e) { validateCommande(); }
            public void changedUpdate(DocumentEvent e) { validateCommande(); }
        };
        
        articleField.getDocument().addDocumentListener(commandeValidator);
        totalField.getDocument().addDocumentListener(commandeValidator);
        
        // Validation planification
        DocumentListener planificationValidator = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePlanification(); }
            public void removeUpdate(DocumentEvent e) { validatePlanification(); }
            public void changedUpdate(DocumentEvent e) { validatePlanification(); }
        };
        
        dateRecuperationField.getDocument().addDocumentListener(planificationValidator);
        dateLivraisonField.getDocument().addDocumentListener(planificationValidator);
    }
    
    private void validateClient() {
        boolean valide = !prenomField.getText().trim().isEmpty() &&
                        !nomField.getText().trim().isEmpty() &&
                        !emailField.getText().trim().isEmpty() &&
                        !telephoneField.getText().trim().isEmpty() &&
                        !adresseArea.getText().trim().isEmpty() &&
                        isValidEmail(emailField.getText().trim());
        
        clientValide = valide;
        updateFieldValidation(prenomField, !prenomField.getText().trim().isEmpty());
        updateFieldValidation(nomField, !nomField.getText().trim().isEmpty());
        updateFieldValidation(emailField, isValidEmail(emailField.getText().trim()));
        updateFieldValidation(telephoneField, !telephoneField.getText().trim().isEmpty());
        updateFieldValidation(adresseArea, !adresseArea.getText().trim().isEmpty());
    }
    
    private void validateCommande() {
        boolean articleOk = !articleField.getText().trim().isEmpty();
        boolean totalOk = isValidNumber(totalField.getText().trim());
        
        commandeValide = articleOk && totalOk;
        updateFieldValidation(articleField, articleOk);
        updateFieldValidation(totalField, totalOk);
    }
    
    private void validatePlanification() {
        Date dateRec = parseDate(dateRecuperationField.getText().trim());
        Date dateLiv = parseDate(dateLivraisonField.getText().trim());
        
        boolean dateRecOk = dateRec != null;
        boolean dateLivOk = dateLiv != null;
        boolean datesOk = dateRecOk && dateLivOk && dateLiv.after(dateRec);
        
        planificationValide = datesOk;
        
        updateFieldValidation(dateRecuperationField, dateRecOk);
        updateFieldValidation(dateLivraisonField, dateLivOk);
        
        // Message d'erreur si les dates sont incorrectes
        if (dateRecOk && dateLivOk && dateLiv.before(dateRec)) {
            showStyledMessage("⚠️ Attention", 
                "La date de livraison doit être postérieure à la date de récupération.", false);
        }
    }
    
    private void updateFieldValidation(JComponent field, boolean isValid) {
        if (isValid) {
            if (field == dateRecuperationField || field == dateLivraisonField) {
                // Pour les champs de date, utiliser la bordure violette normale
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(139, 92, 246), 2),
                    new EmptyBorder(12, 16, 12, 16)
                ));
            } else {
                // Pour les autres champs
                field.setBorder(new EmptyBorder(16, 20, 16, 20));
            }
        } else {
            // Bordure rouge pour les erreurs
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 38, 38), 2),
                new EmptyBorder(12, 16, 12, 16)
            ));
        }
        field.repaint();
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    private boolean isValidNumber(String text) {
        try {
            double value = Double.parseDouble(text);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            format.setLenient(false);
            return format.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    private void creerCommande() {
        // Valider tous les champs
        validateClient();
        validateCommande();
        validatePlanification();
        
        if (!clientValide) {
            showStyledMessage("❌ Validation Client", 
                "Veuillez remplir correctement tous les champs client requis.", false);
            return;
        }
        
        if (!commandeValide) {
            showStyledMessage("❌ Validation Commande", 
                "Veuillez remplir correctement les informations de commande.", false);
            return;
        }
        
        if (!planificationValide) {
            showStyledMessage("❌ Validation Planification", 
                "Veuillez vérifier les dates de planification (format: dd/mm/aaaa).", false);
            return;
        }
        
        try {
            // Créer ou récupérer le client
            // ✅ Ligne correcte
            Client client = new Client(
                
                prenomField.getText().trim(),
                nomField.getText().trim(), 
                telephoneField.getText().trim(),
                emailField.getText().trim(),
                adresseArea.getText().trim()
            );
            
            // Vérifier si le client existe déjà
            Client existingClient = commandeDAO.getClientByEmail(client.getEmail());
            if (existingClient != null) {
                int response = JOptionPane.showConfirmDialog(this,
                    "Un client avec cet email existe déjà.\nVoulez-vous utiliser ses informations ?",
                    "Client existant", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    client = existingClient;
                } else {
                    return;
                }
            } else {
                // Créer un nouveau client
                int clientId = commandeDAO.createClient(client);
                client.setId(clientId);
            }

            
            // Créer la commande
            Commande commande = new Commande(
                client.getId(),
                parseDate(dateRecuperationField.getText().trim()),
                parseDate(dateLivraisonField.getText().trim()),
                (String) statutCombo.getSelectedItem(),
                articleField.getText().trim(),
                Double.parseDouble(totalField.getText().trim()),
                (String) prioriteCombo.getSelectedItem()
            );


            
            int generatedId = commandeDAO.createCommande(commande);
            boolean success = generatedId > 0;

            if (success) {
                dispose();
                if (parentView != null) {
                    parentView.loadCommandeData();
                }
                showStyledMessage("✔ Succès", "Nouvelle commande créée avec succès !", true);
            } else {
                showStyledMessage("✘ Erreur", "Erreur lors de la création de la commande.", false);
            }
            
        } catch (Exception e) {
            Logger.getLogger(NouvelleCommandePanel.class.getName())
                    .log(Level.SEVERE, "Erreur lors de la création de commande", e);
            showStyledMessage("❌ Erreur", 
                "Erreur lors de la création: " + e.getMessage(), false);
        }
    }
    
    private void setupDialog() {
        setSize(720, 700); // Dimensions réduites
        setResizable(false);
        
        // Centrage au milieu de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }
    
    // MÉTHODES UTILITAIRES POUR LES COMPOSANTS AVEC DESIGN OPTIMISÉ
    
    private JPanel createFormSection(String title, String description, Color accentColor) {
        JPanel section = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient subtil
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(248, 250, 252),
                    0, getHeight(), new Color(243, 246, 250)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Bordure colorée plus épaisse
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Accent en haut plus prononcé
                GradientPaint accentGradient = new GradientPaint(
                    0, 0, accentColor,
                    getWidth(), 0, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 180)
                );
                g2d.setPaint(accentGradient);
                g2d.fillRoundRect(0, 0, getWidth(), 8, 20, 20);
                
                g2d.dispose();
            }
        };
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(20, 24, 20, 24)); // Padding réduit
        
        // En-tête de section optimisé
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 19));
        titleLabel.setForeground(new Color(51, 65, 85));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(6, 0, 22, 0));
        
        section.add(titleLabel);
        section.add(descLabel);
        
        return section;
    }
    
    private JTextField createEnhancedTextField(String text, String placeholder) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec léger gradient
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255),
                    0, getHeight(), new Color(252, 254, 255)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure interactive améliorée
                if (hasFocus()) {
                    // Effet de lueur
                    g2d.setColor(new Color(59, 130, 246, 40));
                    g2d.setStroke(new BasicStroke(6f));
                    g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                    
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(209, 213, 219));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(new Font("Inter", Font.PLAIN, 15));
        field.setBorder(new EmptyBorder(16, 20, 16, 20));
        field.setPreferredSize(new Dimension(0, 52)); // Hauteur optimisée
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        field.setOpaque(false);
        
        // Placeholder optimisé
        if (text.isEmpty()) {
            field.setForeground(lightText);
            field.setText(placeholder);
            field.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(darkText);
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (field.getText().trim().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(lightText);
                    }
                }
            });
        }
        
        return field;
    }
    
    private JPanel createDateFieldWithButton(String defaultValue, String fieldType) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setOpaque(false);
        
        // Panel pour le TextField avec padding
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setOpaque(false);
        
        JTextField dateField = new JTextField(defaultValue);
        dateField.setFont(new Font("Inter", Font.PLAIN, 15));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 92, 246), 2),
            new EmptyBorder(12, 16, 12, 12)
        ));
        dateField.setPreferredSize(new Dimension(0, 48));
        dateField.setBackground(Color.WHITE);
        dateField.setForeground(new Color(139, 92, 246));
        dateField.setEditable(false); // En lecture seule, modification par le calendrier
        
        fieldPanel.add(dateField, BorderLayout.CENTER);
        
        // Bouton calendrier
        JButton calendarBtn = new JButton("📅") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = new Color(139, 92, 246);
                if (getModel().isPressed()) {
                    bgColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = new Color(159, 112, 255);
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        calendarBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        calendarBtn.setForeground(Color.WHITE);
        calendarBtn.setPreferredSize(new Dimension(48, 48));
        calendarBtn.setContentAreaFilled(false);
        calendarBtn.setBorderPainted(false);
        calendarBtn.setFocusPainted(false);
        calendarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendarBtn.setToolTipText("Ouvrir le calendrier");
        
        // Action du bouton calendrier
        calendarBtn.addActionListener(e -> {
            Date currentDate = parseDate(dateField.getText());
            if (currentDate == null) {
                currentDate = new Date();
            }
            
            Date selectedDate = showDatePicker(currentDate, fieldType);
            if (selectedDate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dateField.setText(formatter.format(selectedDate));
                validatePlanification(); // Revalider après changement
            }
        });
        
        container.add(fieldPanel, BorderLayout.CENTER);
        container.add(calendarBtn, BorderLayout.EAST);
        
        return container;
    }
    
    private Date showDatePicker(Date initialDate, String fieldType) {
        JDialog dateDialog = new JDialog(this, "Sélectionner une date", true);
        dateDialog.setUndecorated(true);
        dateDialog.setBackground(new Color(0, 0, 0, 0));
        
        // Panel principal avec ombre
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 40));
                for (int i = 0; i < 10; i++) {
                    g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 20 - i, 20 - i);
                }
                
                // Fond principal
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 16, 16);
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel de contenu
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 15, 20));
        contentPanel.setOpaque(false);
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("📅 " + (fieldType.equals("dateRecuperation") ? "Date de Récupération" : "Date de Livraison"));
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(139, 92, 246));
        
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Inter", Font.BOLD, 14));
        closeBtn.setForeground(new Color(107, 114, 128));
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dateDialog.dispose());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeBtn, BorderLayout.EAST);
        
        // Sélecteur de date personnalisé
        CustomDatePicker datePicker = new CustomDatePicker(initialDate);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelBtn = createEnhancedButton("Annuler", new Color(107, 114, 128), Color.WHITE, 80, 35);
        cancelBtn.addActionListener(e -> dateDialog.dispose());
        
        JButton okBtn = createEnhancedButton("OK", new Color(139, 92, 246), Color.WHITE, 80, 35);
        final Date[] result = {null};
        okBtn.addActionListener(e -> {
            result[0] = datePicker.getSelectedDate();
            dateDialog.dispose();
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(okBtn);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(datePicker, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dateDialog.add(mainPanel);
        
        dateDialog.setSize(320, 380);
        
        // Centrage par rapport au parent
        Point parentLocation = this.getLocationOnScreen();
        int x = parentLocation.x + (this.getWidth() - dateDialog.getWidth()) / 2;
        int y = parentLocation.y + (this.getHeight() - dateDialog.getHeight()) / 2;
        dateDialog.setLocation(x, y);
        
        dateDialog.setVisible(true);
        
        return result[0];
    }
    
    // Classe pour le sélecteur de date personnalisé
    private class CustomDatePicker extends JPanel {
        private Calendar calendar;
        private JLabel monthYearLabel;
        private JPanel daysPanel;
        private Date selectedDate;
        private JButton selectedButton;
        
        public CustomDatePicker(Date initialDate) {
            this.calendar = Calendar.getInstance();
            this.selectedDate = initialDate;
            this.calendar.setTime(initialDate);
            
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            createHeader();
            createDaysPanel();
            updateCalendar();
        }
        
        private void createHeader() {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(new EmptyBorder(10, 0, 15, 0));
            
            JButton prevBtn = createNavButton("◀");
            prevBtn.addActionListener(e -> {
                calendar.add(Calendar.MONTH, -1);
                updateCalendar();
            });
            
            monthYearLabel = new JLabel();
            monthYearLabel.setFont(new Font("Inter", Font.BOLD, 16));
            monthYearLabel.setForeground(new Color(139, 92, 246));
            monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton nextBtn = createNavButton("▶");
            nextBtn.addActionListener(e -> {
                calendar.add(Calendar.MONTH, 1);
                updateCalendar();
            });
            
            headerPanel.add(prevBtn, BorderLayout.WEST);
            headerPanel.add(monthYearLabel, BorderLayout.CENTER);
            headerPanel.add(nextBtn, BorderLayout.EAST);
            
            add(headerPanel, BorderLayout.NORTH);
        }
        
        private JButton createNavButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Inter", Font.BOLD, 14));
            btn.setForeground(new Color(139, 92, 246));
            btn.setPreferredSize(new Dimension(40, 30));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }
        
        private void createDaysPanel() {
            daysPanel = new JPanel(new GridLayout(7, 7, 2, 2));
            daysPanel.setBackground(Color.WHITE);
            
            // En-têtes des jours
            String[] dayHeaders = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
            for (String day : dayHeaders) {
                JLabel label = new JLabel(day);
                label.setFont(new Font("Inter", Font.BOLD, 12));
                label.setForeground(new Color(107, 114, 128));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                daysPanel.add(label);
            }
            
            add(daysPanel, BorderLayout.CENTER);
        }
        
        private void updateCalendar() {
            // Supprimer les anciens boutons de jours (garder les en-têtes)
            while (daysPanel.getComponentCount() > 7) {
                daysPanel.remove(daysPanel.getComponentCount() - 1);
            }
            
            // Mettre à jour le label mois/année
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
            monthYearLabel.setText(formatter.format(calendar.getTime()));
            
            // Premier jour du mois
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Dimanche
            
            // Jours du mois précédent (cases vides)
            for (int i = 0; i < firstDayOfWeek; i++) {
                daysPanel.add(new JLabel(""));
            }
            
            // Jours du mois actuel
            int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar today = Calendar.getInstance();
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);
            
            for (int day = 1; day <= daysInMonth; day++) {
                JButton dayBtn = createDayButton(day);
                
                tempCal.set(Calendar.DAY_OF_MONTH, day);
                
                // Marquer le jour sélectionné
                if (tempCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                    tempCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    tempCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)) {
                    dayBtn.setBackground(new Color(139, 92, 246));
                    dayBtn.setForeground(Color.WHITE);
                    selectedButton = dayBtn;
                }
                
                // Marquer aujourd'hui
                if (tempCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    tempCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                    dayBtn.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246), 2));
                }
                
                daysPanel.add(dayBtn);
            }
            
            daysPanel.revalidate();
            daysPanel.repaint();
        }
        
        private JButton createDayButton(int day) {
            JButton btn = new JButton(String.valueOf(day));
            btn.setFont(new Font("Inter", Font.PLAIN, 14));
            btn.setForeground(new Color(31, 41, 55));
            btn.setPreferredSize(new Dimension(35, 35));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            btn.addActionListener(e -> {
                // Désélectionner l'ancien bouton
                if (selectedButton != null) {
                    selectedButton.setBackground(null);
                    selectedButton.setForeground(new Color(31, 41, 55));
                    selectedButton.setOpaque(false);
                }
                
                // Sélectionner le nouveau bouton
                btn.setBackground(new Color(139, 92, 246));
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
                selectedButton = btn;
                
                // Mettre à jour la date sélectionnée
                calendar.set(Calendar.DAY_OF_MONTH, day);
                selectedDate = calendar.getTime();
            });
            
            return btn;
        }
        
        public Date getSelectedDate() {
            return selectedDate;
        }
    }
    
    private JTextArea createEnhancedTextArea(String text, String placeholder) {
        JTextArea area = new JTextArea(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec léger gradient
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255),
                    0, getHeight(), new Color(252, 254, 255)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure interactive
                if (hasFocus()) {
                    g2d.setColor(new Color(59, 130, 246, 40));
                    g2d.setStroke(new BasicStroke(6f));
                    g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                    
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(209, 213, 219));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        area.setFont(new Font("Inter", Font.PLAIN, 15));
        area.setBorder(new EmptyBorder(16, 20, 16, 20));
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(3);
        
        // Placeholder
        if (text.isEmpty()) {
            area.setForeground(lightText);
            area.setText(placeholder);
            area.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (area.getText().equals(placeholder)) {
                        area.setText("");
                        area.setForeground(darkText);
                    }
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (area.getText().trim().isEmpty()) {
                        area.setText(placeholder);
                        area.setForeground(lightText);
                    }
                }
            });
        }
        
        return area;
    }
    
    private <T> JComboBox<T> createEnhancedComboBox() {
        JComboBox<T> combo = new JComboBox<T>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255),
                    0, getHeight(), new Color(252, 254, 255)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure interactive
                if (hasFocus()) {
                    g2d.setColor(new Color(59, 130, 246, 40));
                    g2d.setStroke(new BasicStroke(6f));
                    g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                    
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(209, 213, 219));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        combo.setFont(new Font("Inter", Font.PLAIN, 15));
        combo.setPreferredSize(new Dimension(0, 52));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        combo.setOpaque(false);
        
        return combo;
    }
    
    private JPanel createFieldRow(String label, String description, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(Color.WHITE);
        row.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Inter", Font.BOLD, 15));
        labelComponent.setForeground(new Color(55, 65, 81));
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descComponent = new JLabel(description);
        descComponent.setFont(new Font("Inter", Font.PLAIN, 13));
        descComponent.setForeground(new Color(107, 114, 128));
        descComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        descComponent.setBorder(new EmptyBorder(3, 0, 10, 0));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        row.add(labelComponent);
        row.add(descComponent);
        row.add(field);
        
        return row;
    }
    
    private JButton createEnhancedButton(String text, Color bgColor, Color textColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentBg = bgColor;
                if (getModel().isPressed()) {
                    currentBg = bgColor.darker();
                    // Ombre interne
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);
                } else if (getModel().isRollover()) {
                    currentBg = new Color(
                        Math.min(255, bgColor.getRed() + 30),
                        Math.min(255, bgColor.getGreen() + 30),
                        Math.min(255, bgColor.getBlue() + 30)
                    );
                    
                    // Effet de lueur au survol plus prononcé
                    g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 60));
                    g2d.fillRoundRect(-3, -3, getWidth() + 6, getHeight() + 6, 22, 22);
                }
                
                // Gradient de fond pour plus de profondeur
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentBg,
                    0, getHeight(), new Color(
                        Math.max(0, currentBg.getRed() - 20),
                        Math.max(0, currentBg.getGreen() - 20),
                        Math.max(0, currentBg.getBlue() - 20)
                    )
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Inter", Font.BOLD, 15));
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void showStyledMessage(String title, String message, boolean isSuccess) {
        JDialog messageDialog = new JDialog(this, title, true);
        messageDialog.setUndecorated(true);
        messageDialog.setBackground(Color.WHITE);
        
        JPanel messagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, Color.WHITE,
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                g2d.dispose();
            }
        };
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isSuccess ? primaryGreen : new Color(220, 38, 38), 3),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 17));
        titleLabel.setForeground(darkText);
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + 
                                       message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        messageLabel.setForeground(lightText);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton okButton = createEnhancedButton("OK", 
            isSuccess ? primaryGreen : new Color(220, 38, 38), 
            Color.WHITE, 100, 40);
        okButton.addActionListener(e -> messageDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(messageLabel);
        
        messagePanel.add(contentPanel, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        messageDialog.add(messagePanel);
        messageDialog.pack();
        
        // Centrage
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - messageDialog.getWidth()) / 2;
        int y = (screenSize.height - messageDialog.getHeight()) / 2;
        messageDialog.setLocation(x, y);
        
        messageDialog.setVisible(true);
    }
    
    // ScrollBar UI moderne optimisée
    class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(180, 190, 200);
            this.trackColor = new Color(248, 250, 252);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }
        
        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Gradient pour le thumb
            GradientPaint thumbGradient = new GradientPaint(
                thumbBounds.x, thumbBounds.y, new Color(180, 190, 200),
                thumbBounds.x, thumbBounds.y + thumbBounds.height, new Color(160, 170, 180)
            );
            g2d.setPaint(thumbGradient);
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                             thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            
            g2d.dispose();
        }
    }
}