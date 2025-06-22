package View;

import dao.ClientDAO;
import modele.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Modal pour créer ou modifier un client - Version améliorée
 */
public class ClientFormModal extends JDialog {
    // Palette de couleurs moderne
    private final Color primaryColor = new Color(79, 70, 229);      // Indigo-600
    private final Color primaryLight = new Color(129, 140, 248);    // Indigo-400
    private final Color secondaryColor = new Color(5, 150, 105);    // Emerald-600
    private final Color accentColor = new Color(239, 68, 68);       // Red-500
    private final Color backgroundColor = new Color(248, 250, 252);  // Slate-50
    private final Color cardBackground = Color.WHITE;
    private final Color textPrimary = new Color(15, 23, 42);        // Slate-900
    private final Color textSecondary = new Color(71, 85, 105);     // Slate-600
    private final Color borderColor = new Color(203, 213, 225);     // Slate-300
    private final Color successColor = new Color(34, 197, 94);      // Green-500
    
    private JTextField nomField, prenomField, telephoneField, emailField, adresseField;
    private ClientDAO clientDAO;
    private Client client;
    private boolean clientSaved = false;

    public ClientFormModal(JFrame parent, Client client, ClientDAO clientDAO) {
        super(parent, client == null ? "Nouveau Client" : "Modifier Client", true);
        this.client = client;
        this.clientDAO = clientDAO;
        
        initDialog();
        initComponents();
        
        if (client != null) {
            fillFields();
        }
    }

    private void initDialog() {
        setSize(580, 700);
        setMinimumSize(new Dimension(480, 600));
        
        // POSITIONNEMENT AU CENTRE DE L'ÉCRAN
        setLocationRelativeTo(null); // Centre le modal au milieu de l'écran complet
        
        // Alternative manuelle si besoin :
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int x = (screenSize.width - getWidth()) / 2;
         int y = (screenSize.height - getHeight()) / 2;
         setLocation(x, y);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Icône de la fenêtre
        try {
            setIconImage(createIcon());
        } catch (Exception e) {
            // Icône par défaut si erreur
        }
    }



    private Image createIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(primaryColor);
        g2d.fillRoundRect(4, 4, 24, 24, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2d.drawString("C", 10, 20);
        g2d.dispose();
        return icon;
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(backgroundColor);

        // Header avec gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Contenu principal avec scroll
        JScrollPane scrollPane = createScrollableContent();
        add(scrollPane, BorderLayout.CENTER);

        // Footer avec boutons
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, primaryColor,
                    0, getHeight(), primaryLight
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerPanel.setPreferredSize(new Dimension(0, 120));

        // Icône et titre
        JPanel titleContainer = new JPanel(new BorderLayout(15, 0));
        titleContainer.setOpaque(false);

        // Icône
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        titleContainer.add(iconLabel, BorderLayout.WEST);

        // Textes
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(client == null ? "Nouveau Client" : "Modifier Client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(client == null ? 
            "Créez un nouveau profil client avec toutes les informations nécessaires" : 
            "Modifiez les informations du client sélectionné");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        titleContainer.add(textPanel, BorderLayout.CENTER);
        headerPanel.add(titleContainer, BorderLayout.CENTER);

        return headerPanel;
    }

    private JScrollPane createScrollableContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Card principale du formulaire
        JPanel formCard = createFormCard();
        contentPanel.add(formCard);
        
        // Espace en bas
        contentPanel.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Style de la scrollbar
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = borderColor;
                this.trackColor = backgroundColor;
            }
        });

        return scrollPane;
    }

    private JPanel createFormCard() {
        JPanel formCard = createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(35, 35, 35, 35));

        // En-tête du formulaire
        JPanel formHeader = createFormHeader();
        formCard.add(formHeader);
        formCard.add(Box.createVerticalStrut(25));

        // Grille des champs en 2 colonnes pour optimiser l'espace
        JPanel fieldsGrid = createFieldsGrid();
        formCard.add(fieldsGrid);

        return formCard;
    }

    private JPanel createFormHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Informations Personnelles");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(textPrimary);

        JLabel formSubtitle = new JLabel("Tous les champs marqués d'un astérisque (*) sont obligatoires");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formSubtitle.setForeground(textSecondary);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(formTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(formSubtitle);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createFieldsGrid() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 15); // bottom, right spacing
        
        // Première ligne : Nom et Prénom
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        grid.add(createInputField("Nom *", nomField = new JTextField(), "user"), gbc);
        
        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 20, 0); // left, bottom spacing
        grid.add(createInputField("Prénom *", prenomField = new JTextField(), "user"), gbc);
        
        // Deuxième ligne : Téléphone et Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 15);
        grid.add(createInputField("Téléphone *", telephoneField = new JTextField(), "phone"), gbc);
        
        gbc.gridx = 1; gbc.insets = new Insets(0, 15, 20, 0);
        grid.add(createInputField("Email *", emailField = new JTextField(), "email"), gbc);
        
        // Troisième ligne : Adresse (pleine largeur)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        grid.add(createInputField("Adresse Complète *", adresseField = new JTextField(), "address"), gbc);
        
        return grid;
    }

    private JPanel createInputField(String labelText, JTextField textField, String iconType) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);

        // Label avec icône
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        String icon = getIconForType(iconType);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        JLabel label = new JLabel(" " + labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(textPrimary);
        
        labelPanel.add(iconLabel);
        labelPanel.add(label);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createVerticalStrut(8));

        // Container du champ avec style avancé
        JPanel textFieldContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean focused = textField.hasFocus();
                boolean hasText = !textField.getText().isEmpty();
                
                // Background avec état
                if (focused) {
                    g2d.setColor(Color.WHITE);
                } else if (hasText) {
                    g2d.setColor(new Color(249, 250, 251));
                } else {
                    g2d.setColor(new Color(248, 250, 252));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Border avec état
                if (focused) {
                    g2d.setColor(primaryColor);
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(focused ? 1 : 0, focused ? 1 : 0, 
                                getWidth() - (focused ? 2 : 1), 
                                getHeight() - (focused ? 2 : 1), 10, 10);
                
                g2d.dispose();
            }
        };

        // Configuration du TextField
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(new EmptyBorder(14, 16, 14, 16));
        textField.setForeground(textPrimary);
        textField.setOpaque(false);
        textField.setCaretColor(primaryColor);
        
        // Placeholder effect
        addPlaceholderEffect(textField, getPlaceholderForType(iconType));
        
        // Focus listeners pour les effets visuels
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textFieldContainer.repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textFieldContainer.repaint();
            }
        });

        textFieldContainer.setOpaque(false);
        textFieldContainer.setPreferredSize(new Dimension(0, 48));
        textFieldContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        textFieldContainer.add(textField, BorderLayout.CENTER);

        fieldPanel.add(textFieldContainer);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        return fieldPanel;
    }

    private String getIconForType(String type) {
        switch (type) {
            case "user": return "👤";
            case "phone": return "📞";
            case "email": return "📧";
            case "address": return "🏠";
            default: return "📝";
        }
    }

    private String getPlaceholderForType(String type) {
        switch (type) {
            case "user": return "Entrez le nom/prénom";
            case "phone": return "Ex: +78 755 02 90";
            case "email": return "exemple@email.com";
            case "address": return "123 Rue de la Médina, 75001 DAKAR";
            default: return "";
        }
    }

    private void addPlaceholderEffect(JTextField textField, String placeholder) {
        textField.putClientProperty("placeholder", placeholder);
        
        // Ajouter un listener pour dessiner le placeholder
        textField.addPropertyChangeListener("text", e -> textField.repaint());
        
        // Override paint pour dessiner le placeholder
        textField.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics g) {
                super.paintSafely(g);
                
                if (textField.getText().isEmpty() && !textField.hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(156, 163, 175)); // Gray-400
                    g2d.setFont(textField.getFont());
                    
                    Insets insets = textField.getInsets();
                    FontMetrics fm = g2d.getFontMetrics();
                    int y = (textField.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2d.drawString(placeholder, insets.left, y);
                    g2d.dispose();
                }
            }
        });
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Ligne de séparation
                g2d.setColor(borderColor);
                g2d.drawLine(0, 0, getWidth(), 0);
                g2d.dispose();
            }
        };
        
        footerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setBorder(new EmptyBorder(20, 30, 25, 30));

        JButton cancelBtn = createStyledButton("Annuler", new Color(107, 114, 128), false);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createStyledButton(
            client == null ? "Créer le Client" : "Enregistrer", 
            successColor, 
            true
        );
        saveBtn.addActionListener(e -> saveClient());

        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);

        return footerPanel;
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 20, 20);
                
                // Background principal
                g2d.setColor(cardBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Border subtile
                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JButton createStyledButton(String text, Color color, boolean primary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean pressed = getModel().isPressed();
                boolean hovered = getModel().isRollover();
                
                Color bgColor;
                if (pressed) {
                    bgColor = color.darker();
                } else if (hovered) {
                    bgColor = primary ? color.brighter() : color;
                } else {
                    bgColor = color;
                }
                
                if (!primary && !pressed && !hovered) {
                    // Bouton secondaire avec border
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    g2d.setColor(bgColor);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                } else {
                    g2d.setColor(bgColor);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(primary ? Color.WHITE : color);
        button.setPreferredSize(new Dimension(primary ? 140 : 100, 48));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Effet hover sur la couleur du texte pour bouton secondaire
        if (!primary) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setForeground(Color.WHITE);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setForeground(color);
                }
            });
        }

        return button;
    }

    private void fillFields() {
        if (client != null) {
            nomField.setText(client.getNom());
            prenomField.setText(client.getPrenom());
            telephoneField.setText(client.getTelephone());
            emailField.setText(client.getEmail());
            adresseField.setText(client.getAdresse());
        }
    }

    private void saveClient() {
        if (!validateFields()) {
            return;
        }

        // Animation de chargement sur le bouton
        JButton saveBtn = null;
        for (Component comp : ((JPanel)getContentPane().getComponent(2)).getComponents()) {
            if (comp instanceof JButton && ((JButton)comp).getText().contains("Créer") || 
                ((JButton)comp).getText().contains("Enregistrer")) {
                saveBtn = (JButton)comp;
                break;
            }
        }
        
        if (saveBtn != null) {
            saveBtn.setText("⏳ Traitement...");
            saveBtn.setEnabled(false);
        }

        try {
            if (client == null) {
                // Création d'un nouveau client
                Client newClient = new Client(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    telephoneField.getText().trim(),
                    emailField.getText().trim(),
                    adresseField.getText().trim()
                );

                if (clientDAO.emailExiste(newClient.getEmail())) {
                    showStyledMessage("Un client avec cet email existe déjà!", "Email déjà utilisé", 
                                    JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = clientDAO.ajouterClient(newClient);
                if (success) {
                    clientSaved = true;
                    showStyledMessage("Client créé avec succès!", "Succès", 
                                    JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    showStyledMessage("Erreur lors de la création du client!", "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modification du client existant
                client.setNom(nomField.getText().trim());
                client.setPrenom(prenomField.getText().trim());
                client.setTelephone(telephoneField.getText().trim());
                client.setEmail(emailField.getText().trim());
                client.setAdresse(adresseField.getText().trim());

                boolean success = clientDAO.modifierClient(client);
                if (success) {
                    clientSaved = true;
                    showStyledMessage("Client modifié avec succès!", "Succès", 
                                    JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    showStyledMessage("Erreur lors de la modification du client!", "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            showStyledMessage("Erreur inattendue: " + ex.getMessage(), "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
        } finally {
            // Restaurer le bouton
            if (saveBtn != null) {
                saveBtn.setText(client == null ? "Créer le Client" : "Enregistrer");
                saveBtn.setEnabled(true);
            }
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("• Le nom est obligatoire\n");
            highlightField(nomField, true);
        } else {
            highlightField(nomField, false);
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            errors.append("• Le prénom est obligatoire\n");
            highlightField(prenomField, true);
        } else {
            highlightField(prenomField, false);
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            errors.append("• Le téléphone est obligatoire\n");
            highlightField(telephoneField, true);
        } else {
            highlightField(telephoneField, false);
        }
        
        if (emailField.getText().trim().isEmpty()) {
            errors.append("• L'email est obligatoire\n");
            highlightField(emailField, true);
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("• L'email n'est pas valide\n");
            highlightField(emailField, true);
        } else {
            highlightField(emailField, false);
        }
        
        if (adresseField.getText().trim().isEmpty()) {
            errors.append("• L'adresse est obligatoire\n");
            highlightField(adresseField, true);
        } else {
            highlightField(adresseField, false);
        }

        if (errors.length() > 0) {
            showStyledMessage("Veuillez corriger les erreurs suivantes:\n\n" + errors.toString(),
                            "Champs obligatoires", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void highlightField(JTextField field, boolean error) {
        if (error) {
            field.putClientProperty("error", true);
        } else {
            field.putClientProperty("error", null);
        }
        field.getParent().repaint();
    }

    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", cardBackground);
        UIManager.put("Panel.background", cardBackground);
        UIManager.put("OptionPane.messageForeground", textPrimary);
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    public boolean isClientSaved() {
        return clientSaved;
    }
}