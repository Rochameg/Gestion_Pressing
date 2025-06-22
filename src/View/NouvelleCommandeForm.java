
package View;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.JTextComponent;

public class NouvelleCommandeForm extends JFrame {
    
    // Couleurs du thème
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Bleu
    private static final Color SECONDARY_COLOR = new Color(139, 92, 246); // Violet
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Vert
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(51, 65, 85);
    private static final Color PLACEHOLDER_COLOR = new Color(148, 163, 184);
    
    // Composants du formulaire
    private JTextField nomClientField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JLabel articlesLabel;
    private JFormattedTextField dateRecuperationField;
    private JFormattedTextField dateLivraisonField;
    private ButtonGroup prioriteGroup;
    private JTextArea notesArea;
    
    public NouvelleCommandeForm() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("Nouvelle Commande");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void initializeComponents() {
        // Configuration de la fenêtre principale
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Initialisation des champs
        nomClientField = createStyledTextField("Nom complet");
        telephoneField = createStyledTextField("06 XX XX XX XX");
        emailField = createStyledTextField("email@example.com");
        
        articlesLabel = new JLabel("Aucun article ajouté");
        articlesLabel.setForeground(PLACEHOLDER_COLOR);
        articlesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        articlesLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        // Champs de date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateRecuperationField = new JFormattedTextField(dateFormat);
        dateLivraisonField = new JFormattedTextField(dateFormat);
        styleFormattedTextField(dateRecuperationField, "jj/mm/aaaa");
        styleFormattedTextField(dateLivraisonField, "jj/mm/aaaa");
        
        // Zone de notes
        notesArea = new JTextArea(4, 30);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        notesArea.setBackground(Color.WHITE);
        addPlaceholder(notesArea, "Notes générales...");
        
        // Groupe de boutons radio pour la priorité
        prioriteGroup = new ButtonGroup();
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        addPlaceholder(field, placeholder);
        return field;
    }
    
    private void styleFormattedTextField(JFormattedTextField field, String placeholder) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        addPlaceholder(field, placeholder);
    }
    
    private void addPlaceholder(JTextComponent component, String placeholder) {
        component.setForeground(PLACEHOLDER_COLOR);
        component.setText(placeholder);
        
        component.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (component.getText().equals(placeholder)) {
                    component.setText("");
                    component.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (component.getText().isEmpty()) {
                    component.setForeground(PLACEHOLDER_COLOR);
                    component.setText(placeholder);
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal avec marges
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Titre
        JLabel titleLabel = new JLabel("Nouvelle Commande");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Ajout des sections
        contentPanel.add(createClientInfoSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createArticlesSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createPlanificationSection());
        
        // Panel des boutons d'action
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createClientInfoSection() {
        JPanel section = createSectionPanel("Informations client", "👤");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nom du client
        gbc.gridx = 0; gbc.gridy = 0;
        section.add(createFieldLabel("Nom du client *", true), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        section.add(nomClientField, gbc);
        
        // Téléphone
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        section.add(createFieldLabel("Téléphone *", true), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        section.add(telephoneField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        section.add(createFieldLabel("Email", false), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        section.add(emailField, gbc);
        
        return section;
    }
    
    private JPanel createArticlesSection() {
        JPanel section = createSectionPanel("Articles à traiter", "📦");
        
        // Panel central pour les articles
        JPanel articlesPanel = new JPanel(new BorderLayout());
        articlesPanel.setBackground(new Color(249, 250, 251));
        articlesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(40, 20, 40, 20)
        ));
        
        // Icône de boîte (simulée avec du texte)
        JLabel boxIcon = new JLabel("📦");
        boxIcon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        boxIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        articlesPanel.add(boxIcon, BorderLayout.NORTH);
        articlesPanel.add(articlesLabel, BorderLayout.CENTER);
        
        // Bouton d'ajout
        JButton ajouterButton = createStyledButton("Ajouter un article", PRIMARY_COLOR, false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(ajouterButton);
        
        // Utilisation de GridBagConstraints pour ajouter au GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        section.add(articlesPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        section.add(buttonPanel, gbc);
        
        return section;
    }
    
    private JPanel createPlanificationSection() {
        JPanel section = createSectionPanel("Planification", "📅");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Dates
        gbc.gridx = 0; gbc.gridy = 0;
        section.add(createFieldLabel("Date de récupération", false), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        section.add(dateRecuperationField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        section.add(createFieldLabel("Date de livraison", false), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        section.add(dateLivraisonField, gbc);
        
        // Priorité
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        section.add(createPrioritePanel(), gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        section.add(createFieldLabel("Notes générales", false), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        section.add(scrollPane, gbc);
        
        return section;
    }
    
    private JPanel createPrioritePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(PANEL_COLOR);
        
        JLabel label = createFieldLabel("Priorité :", false);
        panel.add(label);
        
        String[] priorites = {"Normale", "Urgente", "Très urgente"};
        Color[] couleurs = {SUCCESS_COLOR, new Color(249, 115, 22), new Color(239, 68, 68)};
        
        for (int i = 0; i < priorites.length; i++) {
            JRadioButton radio = new JRadioButton(priorites[i]);
            radio.setBackground(PANEL_COLOR);
            radio.setForeground(couleurs[i]);
            radio.setFont(new Font("SansSerif", Font.PLAIN, 13));
            if (i == 0) radio.setSelected(true); // Normale par défaut
            prioriteGroup.add(radio);
            panel.add(radio);
        }
        
        return panel;
    }
    
    private JPanel createSectionPanel(String title, String icon) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Titre de section
        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titleLabel, gbc);
        
        return panel;
    }
    
    private JLabel createFieldLabel(String text, boolean required) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT_COLOR);
        if (required) {
            label.setText(text + " *");
            label.setForeground(new Color(239, 68, 68));
        }
        return label;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton annulerButton = createStyledButton("Annuler", new Color(107, 114, 128), true);
        JButton creerButton = createStyledButton("Créer la commande", PRIMARY_COLOR, false);
        
        panel.add(annulerButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(creerButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor, boolean isSecondary) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        if (isSecondary) {
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
        }
        
        // Coins arrondis simulés avec une bordure
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor, 1),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Gestionnaires d'événements pour les boutons
        // À implémenter selon les besoins
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NouvelleCommandeForm().setVisible(true);
        });
    }
}