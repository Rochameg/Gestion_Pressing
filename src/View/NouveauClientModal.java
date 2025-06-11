package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;

public class NouveauClientModal extends JDialog {

    private JTextField prenomField;
    private JTextField nomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField adresseField;
    private JCheckBox emailCheckBox;
    private JCheckBox telephoneCheckBox;

    private boolean clientCree = false;
    
    // Palette de couleurs moderne et sophistiquée
    private Color primaryColor = new Color(99, 102, 241);      // Indigo vibrant
    private Color accentColor = new Color(168, 85, 247);       // Violet
    private Color successColor = new Color(34, 197, 94);       // Vert moderne
    private Color errorColor = new Color(248, 113, 113);       // Rouge doux
    private Color backgroundColor = new Color(15, 23, 42);     // Bleu nuit
    private Color cardColor = new Color(30, 41, 59);           // Gris bleuté
    private Color surfaceColor = new Color(51, 65, 85);        // Surface
    private Color textColor = new Color(248, 250, 252);        // Blanc cassé
    private Color subtleText = new Color(148, 163, 184);       // Gris subtil

    public NouveauClientModal(JFrame parent) {
        super(parent, "Nouveau Client", true);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        initComponents();
        setupLayout();
        setupEvents();

        setSize(520, 640);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Forme arrondie avec des coins plus prononcés
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
    }

    private void initComponents() {
        prenomField = createModernTextField("Prénom");
        nomField = createModernTextField("Nom");
        emailField = createModernTextField("votre@email.com");
        telephoneField = createModernTextField("+33 6 12 34 56 78");
        adresseField = createModernTextField("123 Rue de la Paix, Paris");

        emailCheckBox = createModernCheckBox();
        telephoneCheckBox = createModernCheckBox();
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background avec glassmorphisme
                if (hasFocus()) {
                    // Effet de glow étendu
                    for (int i = 8; i >= 0; i--) {
                        g2.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 15 - i));
                        g2.fillRoundRect(-i, -i, getWidth() + 2*i, getHeight() + 2*i, 12 + i, 12 + i);
                    }
                }

                // Background principal
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, isHovered ? surfaceColor.brighter() : surfaceColor,
                    0, getHeight(), cardColor
                );
                g2.setPaint(bgGradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Bordure avec dégradé
                if (hasFocus()) {
                    GradientPaint borderGradient = new GradientPaint(
                        0, 0, primaryColor,
                        getWidth(), getHeight(), accentColor
                    );
                    g2.setPaint(borderGradient);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(new Color(71, 85, 105));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
            
            public void setHovered(boolean hovered) {
                this.isHovered = hovered;
                repaint();
            }
        };

        field.setPreferredSize(new Dimension(220, 48));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        field.setBackground(new Color(0, 0, 0, 0)); // Transparent
        field.setForeground(subtleText);
        field.setText(placeholder);
        field.setOpaque(false);
        field.setCaretColor(primaryColor);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(textColor);
                }
                field.repaint();
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(subtleText);
                    field.setText(placeholder);
                }
                field.repaint();
            }
        });

        return field;
    }

    private JCheckBox createModernCheckBox() {
        JCheckBox checkBox = new JCheckBox() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Effet de glow si sélectionné
                if (isSelected()) {
                    for (int i = 3; i >= 0; i--) {
                        g2.setColor(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 40 - i*10));
                        g2.fillRoundRect(x - i, y - i, size + 2*i, size + 2*i, 8 + i, 8 + i);
                    }
                }

                // Background
                if (isSelected()) {
                    GradientPaint gradient = new GradientPaint(
                        x, y, primaryColor,
                        x + size, y + size, accentColor
                    );
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
            }
            
            public void setHovered(boolean hovered) {
                this.isHovered = hovered;
                repaint();
            }
        };

        checkBox.setPreferredSize(new Dimension(28, 28));
        checkBox.setBackground(new Color(0, 0, 0, 0));
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setOpaque(false);

        return checkBox;
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
                    g2.setColor(new Color(0, 0, 0, 8 - i/2));
                    g2.fillRoundRect(6 + i, 6 + i, getWidth() - 12 - 2*i, getHeight() - 12 - 2*i, 24 - i, 24 - i);
                }

                // Background principal avec dégradé sophistiqué
                GradientPaint mainGradient = new GradientPaint(
                    0, 0, cardColor,
                    0, getHeight(), backgroundColor.brighter()
                );
                g2.setPaint(mainGradient);
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 20, 20);

                // Bordure subtile
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 20, 20);

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
                    getWidth(), getHeight(), accentColor
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Overlay subtil pour plus de profondeur
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 16, 16);

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
                    getWidth(), getHeight(), new Color(255, 255, 255, 180)
                );
                g2.setPaint(iconGradient);
                g2.fillOval(2, 2, getWidth()-4, getHeight()-4);
                
                // Icône utilisateur stylisée
                g2.setColor(primaryColor);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Tête
                g2.drawOval(getWidth()/2 - 4, 8, 8, 8);
                // Corps
                g2.drawArc(getWidth()/2 - 8, 16, 16, 12, 0, 180);
                
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
                g2.drawLine(margin, margin, getWidth()-margin, getHeight()-margin);
                g2.drawLine(getWidth()-margin, margin, margin, getHeight()-margin);
                
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

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Prénom et Nom
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        formPanel.add(createStyledLabel("Prénom *", labelFont), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledLabel("Nom *", labelFont), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(prenomField, gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        // Email
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel emailPanel = createFieldPanel(emailCheckBox, "Email", labelFont);
        formPanel.add(emailPanel, gbc);

        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        // Téléphone
        gbc.gridy = 4;
        JPanel telPanel = createFieldPanel(telephoneCheckBox, "Téléphone", labelFont);
        formPanel.add(telPanel, gbc);

        gbc.gridy = 5;
        formPanel.add(telephoneField, gbc);

        // Adresse
        gbc.gridy = 6;
        formPanel.add(createStyledLabel("Adresse *", labelFont), gbc);

        gbc.gridy = 7;
        formPanel.add(adresseField, gbc);

        return formPanel;
    }

    private JPanel createFieldPanel(JCheckBox checkBox, String labelText, Font font) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.add(checkBox);
        
        JLabel label = createStyledLabel(labelText, font);
        panel.add(label);
        
        JLabel optLabel = new JLabel("(optionnel)");
        optLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        optLabel.setForeground(subtleText);
        panel.add(optLabel);
        
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

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton() {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = isPressed ? bgColor.darker() :
                                 isHovered ? bgColor.brighter() : bgColor;

                // Effet de glow pour le bouton principal
                if (isPrimary && (isHovered || isPressed)) {
                    for (int i = 6; i >= 0; i--) {
                        g2.setColor(new Color(currentBg.getRed(), currentBg.getGreen(), currentBg.getBlue(), 20 - i*3));
                        g2.fillRoundRect(-i, -i, getWidth() + 2*i, getHeight() + 2*i, 12 + i, 12 + i);
                    }
                }

                // Dégradé sophistiqué
                if (isPrimary) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, currentBg.brighter(),
                        0, getHeight(), currentBg.darker()
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(currentBg);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Highlight subtil en haut
                if (isPrimary) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight()/3, 12, 12);
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
        };

        button.setText(text);
        button.setPreferredSize(new Dimension(isPrimary ? 130 : 100, 42));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(textColor);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    private void setupEvents() {
        // Effets hover sophistiqués
        for (JTextField field : new JTextField[]{prenomField, nomField, emailField, telephoneField, adresseField}) {
            field.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (field instanceof JTextField) {
                        try {
                            field.getClass().getMethod("setHovered", boolean.class).invoke(field, true);
                        } catch (Exception ex) {
                            // Fallback silencieux
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (field instanceof JTextField) {
                        try {
                            field.getClass().getMethod("setHovered", boolean.class).invoke(field, false);
                        } catch (Exception ex) {
                            // Fallback silencieux
                        }
                    }
                }
            });
        }

        // Effets hover pour les checkboxes
        for (JCheckBox checkBox : new JCheckBox[]{emailCheckBox, telephoneCheckBox}) {
            checkBox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    try {
                        checkBox.getClass().getMethod("setHovered", boolean.class).invoke(checkBox, true);
                    } catch (Exception ex) {
                        // Fallback silencieux
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    try {
                        checkBox.getClass().getMethod("setHovered", boolean.class).invoke(checkBox, false);
                    } catch (Exception ex) {
                        // Fallback silencieux
                    }
                }
            });
        }
    }

    private void enregistrerClient() {
        if (isFieldEmpty(prenomField, "Prénom") ||
            isFieldEmpty(nomField, "Nom") ||
            isFieldEmpty(emailField, "votre@email.com") ||
            isFieldEmpty(telephoneField, "+33 6 12 34 56 78") ||
            isFieldEmpty(adresseField, "123 Rue de la Paix, Paris")) {

            showModernDialog(
                "Champs manquants",
                "Veuillez remplir tous les champs requis.",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String email = emailField.getText().trim();
        if (!isValidEmail(email)) {
            showModernDialog(
                "Email invalide",
                "Veuillez saisir une adresse email valide.",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        clientCree = true;

        showModernDialog(
            "Succès",
            "✨ Client créé avec succès !\n\n" +
            "👤 " + prenomField.getText() + " " + nomField.getText() + "\n" +
            "📧 " + emailField.getText() + "\n" +
            "📱 " + telephoneField.getText() + "\n" +
            "🏠 " + adresseField.getText(),
            JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    private void showModernDialog(String title, String message, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setVisible(true);
    }

    private boolean isFieldEmpty(JTextField field, String placeholder) {
        return field.getText().trim().isEmpty() || field.getText().equals(placeholder);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean isClientCree() {
        return clientCree;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame parentFrame = new JFrame();
            parentFrame.setUndecorated(true);
            parentFrame.setSize(0, 0);
            
            NouveauClientModal modal = new NouveauClientModal(parentFrame);
            modal.setVisible(true);
            
            System.exit(0);
        });
    }
}