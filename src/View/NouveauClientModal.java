package View;

import modele.Client;
import dao.ClientDAO;
import Utils.DatabaseConnection; // Assurez-vous que cette classe gère correctement la connexion à la BDD

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class NouveauClientModal extends JDialog {

    private JTextField nomField, prenomField, telephoneField, emailField, adresseField;
    private JCheckBox telephoneCheckBox, emailCheckBox;
    private boolean clientCree = false;
    private ClientDAO clientDAO;

    // Palette de couleurs moderne et sophistiquée
    private final Color primaryColor = new Color(99, 102, 241); // Indigo vibrant
    private final Color accentColor = new Color(168, 85, 247); // Violet
    private final Color successColor = new Color(34, 197, 94); // Vert moderne
    private final Color errorColor = new Color(248, 113, 113); // Rouge doux
    private final Color backgroundColor = new Color(15, 23, 42); // Bleu nuit
    private final Color cardColor = new Color(30, 41, 59); // Gris bleuté
    private final Color surfaceColor = new Color(51, 65, 85); // Surface
    private final Color textColor = new Color(248, 250, 252); // Blanc cassé
    private final Color subtleText = new Color(148, 163, 184); // Gris subtil

    // Constantes pour la réutilisabilité et la maintenabilité
    private static final int BORDER_RADIUS_MODAL = 24;
    private static final int BORDER_RADIUS_COMPONENT = 12;
    private static final int FIELD_HEIGHT = 48;
    private static final int CHECKBOX_SIZE = 20;
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public NouveauClientModal(JFrame parent) {
        super(parent, "Nouveau Client", true);

        // Initialisation de ClientDAO (assurez-vous que
        // DatabaseConnection.getConnection() est fonctionnel)
        try {
            this.clientDAO = new ClientDAO();
        } catch (Exception e) {
            Logger.getLogger(NouveauClientModal.class.getName()).log(Level.SEVERE,
                    "Impossible d'établir la connexion à la base de données pour ClientDAO.", e);
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données. Veuillez contacter l'administrateur.", "Erreur Fatale",
                    JOptionPane.ERROR_MESSAGE);
            // Si la connexion échoue ici, il est préférable de ne pas continuer
            // et de fermer le modal ou de désactiver des fonctionnalités.
            dispose();
            return; // Sortir du constructeur
        }

        setTitle("Nouveau Client");
        setModal(true);
        setLayout(new BorderLayout());
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE); // Pas de décoration par défaut

        initComponents();
        setupLayout();
        setupEvents();

        setSize(520, 640);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Forme arrondie avec des coins plus prononcés
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), BORDER_RADIUS_MODAL, BORDER_RADIUS_MODAL));
    }

    private void initComponents() {
        prenomField = createModernTextField("Prénom");
        nomField = createModernTextField("Nom");
        emailField = createModernTextField("mulho@email.com");
        telephoneField = createModernTextField("+33 6 12 34 56 78");
        adresseField = createModernTextField("Rue 27X24 Medina, Dakar");

        emailCheckBox = createModernCheckBox();
        telephoneCheckBox = createModernCheckBox();
    }

    private boolean clientAdded = false;

    public boolean isClientAdded() {
        return clientAdded;
    }

    // Classe interne pour les JTextField personnalisés avec placeholder et hover
    private class ModernTextField extends JTextField {
        private final String placeholder;
        private boolean isHovered = false;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setText(placeholder);
            setForeground(subtleText);

            setPreferredSize(new Dimension(220, FIELD_HEIGHT));
            setFont(FIELD_FONT);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            setBackground(new Color(0, 0, 0, 0)); // Transparent
            setOpaque(false);
            setCaretColor(primaryColor);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent evt) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(textColor);
                    }
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent evt) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(subtleText);
                    }
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHovered(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setHovered(false);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background avec glassmorphisme
            if (hasFocus()) {
                // Effet de glow étendu
                for (int i = 8; i >= 0; i--) {
                    g2.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(),
                            15 - i));
                    g2.fillRoundRect(-i, -i, getWidth() + 2 * i, getHeight() + 2 * i, BORDER_RADIUS_COMPONENT + i,
                            BORDER_RADIUS_COMPONENT + i);
                }
            }

            // Background principal
            GradientPaint bgGradient = new GradientPaint(
                    0, 0, isHovered ? surfaceColor.brighter() : surfaceColor,
                    0, getHeight(), cardColor);
            g2.setPaint(bgGradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS_COMPONENT, BORDER_RADIUS_COMPONENT);

            // Bordure avec dégradé
            if (hasFocus()) {
                GradientPaint borderGradient = new GradientPaint(
                        0, 0, primaryColor,
                        getWidth(), getHeight(), accentColor);
                g2.setPaint(borderGradient);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, BORDER_RADIUS_COMPONENT, BORDER_RADIUS_COMPONENT);

            g2.dispose();
            super.paintComponent(g);
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint();
        }
    }

    private ModernTextField createModernTextField(String placeholder) {
        return new ModernTextField(placeholder);
    }

    // Classe interne pour les JCheckBox personnalisés
    private class ModernCheckBox extends JCheckBox {
        private boolean isHovered = false;

        public ModernCheckBox() {
            setPreferredSize(new Dimension(CHECKBOX_SIZE + 8, CHECKBOX_SIZE + 8)); // Légèrement plus grand pour le clic
            setBackground(new Color(0, 0, 0, 0));
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHovered(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setHovered(false);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = CHECKBOX_SIZE;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Effet de glow si sélectionné
            if (isSelected()) {
                for (int i = 3; i >= 0; i--) {
                    g2.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(),
                            40 - i * 10));
                    g2.fillRoundRect(x - i, y - i, size + 2 * i, size + 2 * i, 8 + i, 8 + i);
                }
            }

            // Background
            if (isSelected()) {
                GradientPaint gradient = new GradientPaint(
                        x, y, primaryColor,
                        x + size, y + size, accentColor);
                g2.setPaint(gradient);
            } else {
                g2.setColor(isHovered ? surfaceColor.brighter() : surfaceColor);
            }
            g2.fillRoundRect(x, y, size, size, 6, 6);

            // Bordure
            g2.setColor(isSelected() ? primaryColor.brighter() : new Color(71, 85, 105));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, size, size, 6, 6);

            // Checkmark animé
            if (isSelected()) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 5, y + 10, x + 8, y + 13);
                g2.drawLine(x + 8, y + 13, x + 15, y + 6);
            }

            g2.dispose();
            // super.paintComponent(g); // Pas besoin d'appeler super.paintComponent ici car
            // nous dessinons tout
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint();
        }
    }

    private ModernCheckBox createModernCheckBox() {
        return new ModernCheckBox();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Panel principal avec effet glassmorphisme
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Ombre multiple pour plus de profondeur
                for (int i = 12; i >= 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 8 - i / 2));
                    g2.fillRoundRect(6 + i, 6 + i, getWidth() - 12 - 2 * i, getHeight() - 12 - 2 * i,
                            BORDER_RADIUS_MODAL - i, BORDER_RADIUS_MODAL - i);
                }

                // Background principal avec dégradé sophistiqué
                GradientPaint mainGradient = new GradientPaint(
                        0, 0, cardColor,
                        0, getHeight(), backgroundColor.brighter());
                g2.setPaint(mainGradient);
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, BORDER_RADIUS_MODAL - 4,
                        BORDER_RADIUS_MODAL - 4); // Coins légèrement moins prononcés pour le fond

                // Bordure subtile
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(6, 6, getWidth() - 12, getHeight() - 12, BORDER_RADIUS_MODAL - 4,
                        BORDER_RADIUS_MODAL - 4);

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = createHeaderPanel();
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé diagonal sophistiqué
                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryColor,
                        getWidth(), getHeight(), accentColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS_COMPONENT + 4,
                        BORDER_RADIUS_COMPONENT + 4);

                // Overlay subtil pour plus de profondeur
                g2.setColor(new Color(255, 255, 255, 15)); // Légèrement augmenté l'opacité
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, BORDER_RADIUS_COMPONENT + 4,
                        BORDER_RADIUS_COMPONENT + 4);

                g2.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        // Titre avec icône stylée
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        titlePanel.setOpaque(false);

        // Icône personnalisée
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Cercle avec dégradé
                GradientPaint iconGradient = new GradientPaint(
                        0, 0, Color.WHITE,
                        getWidth(), getHeight(), new Color(255, 255, 255, 180));
                g2.setPaint(iconGradient);
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);

                // Icône utilisateur stylisée
                g2.setColor(primaryColor.darker()); // Couleur plus foncée pour le contraste
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Tête
                g2.drawOval(getWidth() / 2 - 4, 8, 8, 8);
                // Corps
                g2.drawArc(getWidth() / 2 - 8, 16, 16, 12, 0, 180);

                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(28, 28));
        iconPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Nouveau Client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(iconPanel);
        titlePanel.add(titleLabel);

        // Bouton fermer moderne avec effet hover
        JButton closeButton = new JButton() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int margin = 8;
                g2.drawLine(margin, margin, getWidth() - margin, getHeight() - margin);
                g2.drawLine(getWidth() - margin, margin, margin, getHeight() - margin);

                g2.dispose();
            }

            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }
        };
        closeButton.setPreferredSize(new Dimension(32, 32));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0, 0, 0, 0));
        formPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Prénom et Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        formPanel.add(createStyledLabel("Prénom *", true), gbc); // Champ obligatoire
        gbc.gridx = 1;
        formPanel.add(createStyledLabel("Nom *", true), gbc); // Champ obligatoire

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(prenomField, gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        // Email
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel emailPanel = createFieldPanel(emailCheckBox, "Email", false); // Champ optionnel
        formPanel.add(emailPanel, gbc);

        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        // Téléphone
        gbc.gridy = 4;
        JPanel telPanel = createFieldPanel(telephoneCheckBox, "Téléphone", false); // Champ optionnel
        formPanel.add(telPanel, gbc);

        gbc.gridy = 5;
        formPanel.add(telephoneField, gbc);

        // Adresse
        gbc.gridy = 6;
        formPanel.add(createStyledLabel("Adresse *", true), gbc); // Champ obligatoire

        gbc.gridy = 7;
        formPanel.add(adresseField, gbc);

        return formPanel;
    }

    private JPanel createFieldPanel(JCheckBox checkBox, String labelText, boolean required) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.add(checkBox);

        JLabel label = createStyledLabel(labelText, required);
        panel.add(label);

        if (!required) {
            JLabel optLabel = new JLabel("(optionnel)");
            optLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            optLabel.setForeground(subtleText);
            panel.add(optLabel);
        }

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));
        buttonPanel.setBorder(new EmptyBorder(25, 0, 20, 0));

        JButton cancelButton = createModernButton("Annuler", errorColor, false);
        JButton saveButton = createModernButton("Créer Client", successColor, true);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> enregistrerClient());

        return buttonPanel;
    }

    // Classe interne pour les JButton personnalisés avec effets
    private class ModernButton extends JButton {
        private final Color bgColor;
        private final boolean isPrimary;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public ModernButton(String text, Color bgColor, boolean isPrimary) {
            super(text);
            this.bgColor = bgColor;
            this.isPrimary = isPrimary;

            setPreferredSize(new Dimension(isPrimary ? 130 : 100, 42));
            setFont(BUTTON_FONT);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentBg = isPressed ? bgColor.darker() : isHovered ? bgColor.brighter() : bgColor;

            // Effet de glow pour le bouton principal
            if (isPrimary && (isHovered || isPressed)) {
                for (int i = 6; i >= 0; i--) {
                    g2.setColor(
                            new Color(currentBg.getRed(), currentBg.getGreen(), currentBg.getBlue(), 20 - i * 3));
                    g2.fillRoundRect(-i, -i, getWidth() + 2 * i, getHeight() + 2 * i, BORDER_RADIUS_COMPONENT + i,
                            BORDER_RADIUS_COMPONENT + i);
                }
            }

            // Dégradé sophistiqué
            if (isPrimary) {
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentBg.brighter(),
                        0, getHeight(), currentBg.darker());
                g2.setPaint(gradient);
            } else {
                g2.setColor(currentBg);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS_COMPONENT, BORDER_RADIUS_COMPONENT);

            // Highlight subtil en haut
            if (isPrimary) {
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 3, BORDER_RADIUS_COMPONENT, BORDER_RADIUS_COMPONENT);
            }

            // Texte avec ombre
            g2.setColor(new Color(0, 0, 0, 30));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), textX + 1, textY + 1);

            g2.setColor(Color.WHITE);
            g2.drawString(getText(), textX, textY);

            g2.dispose();
            // super.paintComponent(g); // Pas besoin d'appeler super.paintComponent ici
        }
    }

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        return new ModernButton(text, bgColor, isPrimary);
    }

    private JLabel createStyledLabel(String text, boolean required) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(required ? textColor.brighter() : textColor); // Légèrement plus lumineux si obligatoire
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    private void setupEvents() {
        // La gestion du hover est maintenant intégrée dans les classes ModernTextField
        // et ModernCheckBox
    }

    private void enregistrerClient() {
        String prenom = prenomField.getText().trim();
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();

        // Validation des champs obligatoires
        if (isFieldEmpty(prenomField, "Prénom") ||
                isFieldEmpty(nomField, "Nom") ||
                isFieldEmpty(adresseField, "Adresse")) {

            showModernDialog(
                    "Champs manquants",
                    "Veuillez remplir tous les champs obligatoires.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validation conditionnelle de l'email
        if (emailCheckBox.isSelected() || (!email.isEmpty() && !email.equals("mulho@email.com"))) {
            if (!isValidEmail(email)) {
                showModernDialog(
                        "Email invalide",
                        "Veuillez saisir une adresse email valide.",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            email = null; // non requis
        }

        // Validation conditionnelle du téléphone
        if (telephoneCheckBox.isSelected() || (!telephone.isEmpty() && !telephone.equals("+33 6 12 34 56 78"))) {
            if (telephone.equals("+33 6 12 34 56 78")) {
                showModernDialog(
                        "Numéro de téléphone invalide",
                        "Veuillez saisir un numéro de téléphone correct si la case est cochée.",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            telephone = null;
        }

        // Création de l'objet client
        Client nouveauClient = new Client(0, prenom, nom, telephone, email, adresse);

        try {
            ClientDAO clientDAO = new ClientDAO();
            boolean success = clientDAO.ajouterClient(nouveauClient);

            if (success) {
                clientCree = true; // Indicateur que le client a été ajouté

                showModernDialog(
                        "Succès",
                        "✨ Client créé avec succès !\n\n" +
                                "👤 " + prenom + " " + nom + "\n" +
                                "📧 " + (email != null ? email : "Non renseigné") + "\n" +
                                "📱 " + (telephone != null ? telephone : "Non renseigné") + "\n" +
                                "🏠 " + adresse,
                        JOptionPane.INFORMATION_MESSAGE);

                dispose(); // Fermer la fenêtre après enregistrement
            } else {
                showModernDialog(
                        "Échec de l'ajout",
                        "Le client n'a pas pu être ajouté à la base de données.",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            Logger.getLogger(NouveauClientModal.class.getName()).log(Level.SEVERE,
                    "Erreur lors de l'enregistrement du client", e);
            showModernDialog(
                    "Erreur de base de données",
                    "Une erreur est survenue lors de l'ajout du client :\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showModernDialog(String title, String message, int messageType) {
        // Applique le look and feel système pour les JOptionPane pour une meilleure
        // intégration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            Logger.getLogger(NouveauClientModal.class.getName()).log(Level.WARNING,
                    "Erreur lors de la définition du L&F système pour le dialogue.", e);
        }

        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setVisible(true);

        // Rétablit le look and feel original si nécessaire après le dialogue
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Ou votre propre L&F si défini
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            Logger.getLogger(NouveauClientModal.class.getName()).log(Level.WARNING,
                    "Erreur lors du rétablissement du L&F par défaut.", e);
        }
    }

    private boolean isFieldEmpty(JTextField field, String placeholder) {
        return field.getText().trim().isEmpty() || field.getText().equals(placeholder);
    }

    private boolean isValidEmail(String email) {
        // Un regex plus robuste pour la validation d'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public boolean isClientCree() {
        return clientCree;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Utilisation du look and feel système pour une meilleure intégration
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
            } catch (Exception e) {
                Logger.getLogger(NouveauClientModal.class.getName()).log(Level.SEVERE,
                        "Erreur lors de l'initialisation du L&F dans main.", e);
            }

            JFrame parentFrame = new JFrame();
            parentFrame.setUndecorated(true);
            parentFrame.setSize(0, 0); // Rend le cadre parent invisible
            parentFrame.setVisible(true); // Doit être visible pour que setLocationRelativeTo fonctionne bien

            NouveauClientModal nouveauClientModal = new NouveauClientModal(parentFrame);
            nouveauClientModal.setVisible(true);

            parentFrame.dispose(); // Ferme le cadre parent une fois le modal géré
            System.exit(0); // Quitte l'application une fois le modal fermé
        });
    }
}