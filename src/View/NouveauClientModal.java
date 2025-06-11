package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class NouveauClientModal extends JDialog {

    private JTextField prenomField;
    private JTextField nomField;
    private JTextField emailField;
    private JTextField telephoneField;
    private JTextField adresseField;
    private JCheckBox emailCheckBox;
    private JCheckBox telephoneCheckBox;

    private boolean clientCree = false;
    private Color primaryColor = new Color(67, 56, 202);
    private Color accentColor = new Color(99, 102, 241);
    private Color successColor = new Color(34, 197, 94);
    private Color backgroundColor = new Color(248, 250, 252);
    private Color cardColor = Color.WHITE;
    private Color textColor = new Color(30, 41, 59);

    public NouveauClientModal(JFrame parent) {
        super(parent, "Nouveau Client", true); // Modal bloquant
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        initComponents();
        setupLayout();
        setupEvents();

        setSize(650, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    private void initComponents() {
        prenomField = createModernTextField("Prénom");
        nomField = createModernTextField("Nom");
        emailField = createModernTextField("E-mail");
        telephoneField = createModernTextField("Téléphone");
        adresseField = createModernTextField("Adresse");

        emailCheckBox = createModernCheckBox();
        telephoneCheckBox = createModernCheckBox();
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (hasFocus()) {
                    g2.setColor(primaryColor);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(new Color(200, 200, 200));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        field.setPreferredSize(new Dimension(250, 50));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(156, 163, 175));
        field.setText(placeholder);
        field.setOpaque(false);

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
                    field.setForeground(new Color(156, 163, 175));
                    field.setText(placeholder);
                }
                field.repaint();
            }
        });

        return field;
    }

    private JCheckBox createModernCheckBox() {
        JCheckBox checkBox = new JCheckBox() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 20;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                if (isSelected()) {
                    g2.setColor(primaryColor);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(x, y, size, size, 6, 6);

                g2.setColor(isSelected() ? primaryColor : new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, size, size, 6, 6);

                if (isSelected()) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] xPoints = {x + 5, x + 8, x + 15};
                    int[] yPoints = {y + 10, y + 13, y + 7};
                    for (int i = 0; i < xPoints.length - 1; i++) {
                        g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                    }
                }

                g2.dispose();
            }
        };

        checkBox.setPreferredSize(new Dimension(30, 30));
        checkBox.setBackground(cardColor);
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setOpaque(false);

        return checkBox;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);

                g2.setColor(cardColor);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout(0, 30));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel headerPanel = createHeaderPanel();

        JPanel formPanel = createFormPanel();

        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Nouveau Client", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(primaryColor);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(e -> dispose());

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(cardColor);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        formPanel.add(createLabel("Prénom", labelFont, textColor), gbc);
        gbc.gridx = 1;
        formPanel.add(createLabel("Nom", labelFont, textColor), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(prenomField, gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel emailPanel = createFieldPanel(emailCheckBox, "E-mail", labelFont);
        formPanel.add(emailPanel, gbc);

        gbc.gridy = 3;
        formPanel.add(emailField, gbc);

        gbc.gridy = 4;
        JPanel telPanel = createFieldPanel(telephoneCheckBox, "Téléphone", labelFont);
        formPanel.add(telPanel, gbc);

        gbc.gridy = 5;
        formPanel.add(telephoneField, gbc);

        gbc.gridy = 6; gbc.gridwidth = 1;
        formPanel.add(createLabel("Adresse", labelFont, textColor), gbc);

        gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(adresseField, gbc);

        return formPanel;
    }

    private JPanel createFieldPanel(JCheckBox checkBox, String labelText, Font font) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(cardColor);
        panel.add(checkBox);
        panel.add(createLabel(labelText, font, textColor));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(cardColor);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton cancelButton = createModernButton("Annuler", new Color(239, 68, 68), false);
        JButton saveButton = createModernButton("Enregistrer", successColor, true);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> enregistrerClient());

        return buttonPanel;
    }

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = getModel().isPressed() ? bgColor.darker() :
                                 getModel().isRollover() ? bgColor.brighter() : bgColor;

                if (isPrimary) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, currentBg,
                        0, getHeight(), currentBg.darker()
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(currentBg);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(140, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void setupEvents() {
        for (JTextField field : new JTextField[]{prenomField, nomField, emailField, telephoneField, adresseField}) {
            field.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!field.hasFocus()) {
                        field.setBackground(new Color(249, 250, 251));
                        field.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!field.hasFocus()) {
                        field.setBackground(Color.WHITE);
                        field.repaint();
                    }
                }
            });
        }
    }

    private void enregistrerClient() {
        if (isFieldEmpty(prenomField, "Prénom") ||
            isFieldEmpty(nomField, "Nom") ||
            isFieldEmpty(emailField, "E-mail") ||
            isFieldEmpty(telephoneField, "Téléphone") ||
            isFieldEmpty(adresseField, "Adresse")) {

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
                "E-mail invalide",
                "Veuillez saisir une adresse e-mail valide.",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        clientCree = true;

        showModernDialog(
            "Succès",
            "Client créé avec succès !\n\n" +
            "Nom: " + nomField.getText() + "\n" +
            "Prénom: " + prenomField.getText() + "\n" +
            "E-mail: " + emailField.getText() + "\n" +
            "Téléphone: " + telephoneField.getText(),
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

            // Créer une frame parent invisible
            JFrame parentFrame = new JFrame();
            parentFrame.setUndecorated(true);
            parentFrame.setSize(0, 0);
            
            // Créer et afficher le modal
            NouveauClientModal modal = new NouveauClientModal(parentFrame);
            modal.setVisible(true);
            
            // Fermer l'application quand le modal est fermé
            System.exit(0);
        });
    }
}