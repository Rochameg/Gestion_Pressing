package View ;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import Utils.DatabaseConnection;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton togglePasswordVisibilityButton;
    private JLabel forgotPasswordLabel;
    private JLabel signupLabel;

    private static final Color BACKGROUND_COLOR = new Color(220, 220, 230);
    private static final Color CARD_COLOR = new Color(240, 240, 245);
    private static final Color GOLD_COLOR = new Color(255, 193, 7);
    private static final Color GOLD_HOVER = new Color(255, 215, 0);
    private static final Color TEXT_BLACK = new Color(0, 0, 0);
    private static final Color TEXT_GRAY = new Color(100, 100, 100);
    private static final Color TEXT_LIGHT_GRAY = new Color(150, 150, 150);
    private static final Color INPUT_COLOR = new Color(250, 250, 250);
    private static final Color ACCENT_COLOR = new Color(138, 43, 226);
    private static final Color BLUE_COLOR = new Color(0, 102, 204);

    public LoginView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    public void afficher() {
        this.setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Pressing Royal - Connexion Privilégiée");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
    }

    private void setupLayout() {
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_COLOR,
                    getWidth(), getHeight(), new Color(200, 200, 220)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(40, 60, 20, 60));

        JPanel presentationCard = createEnhancedPresentationCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 25);
        mainPanel.add(presentationCard, gbc);

        JPanel loginCard = createEnhancedLoginCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 25, 0, 0);
        mainPanel.add(loginCard, gbc);

        gradientPanel.add(mainPanel);

        JPanel footerPanel = createFooter();

        add(gradientPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createEnhancedPresentationCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(8, 8, getWidth()-8, getHeight()-8, 20, 20);

                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(230, 230, 235)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 20, 20);

                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(GOLD_COLOR);
                g2d.drawRoundRect(1, 1, getWidth()-10, getHeight()-10, 20, 20);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(70, 50, 70, 50));

        JLabel crownIcon = new JLabel("👑") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 193, 7, 40));
                g2d.fillOval(10, 10, getWidth()-20, getHeight()-20);

                super.paintComponent(g);
            }
        };
        crownIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 58));
        crownIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        crownIcon.setBorder(new EmptyBorder(0, 0, 40, 0));

        JLabel titleLabel = new JLabel("Pressing Royal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(TEXT_BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel decorativeLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, getHeight()/2, new Color(255, 193, 7, 0),
                    getWidth()/2, getHeight()/2, GOLD_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth()/2, 2);

                gradient = new GradientPaint(
                    getWidth()/2, getHeight()/2, GOLD_COLOR,
                    getWidth(), getHeight()/2, new Color(255, 193, 7, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(getWidth()/2, 0, getWidth()/2, 2);
            }
        };
        decorativeLine.setOpaque(false);
        decorativeLine.setMaximumSize(new Dimension(200, 2));
        decorativeLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html><center>L'excellence vestimentaire à la française<br><span style='color: #FFC107;'>Un service d'exception depuis 1985</span><br><br><i>« Votre élégance, notre passion »</i></center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        badgesPanel.setOpaque(false);
        badgesPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        String[] badges = {"⭐ Qualité Premium", "🏆 Service 5 étoiles", "🚚 Livraison rapide"};
        for (String badge : badges) {
            JLabel badgeLabel = new JLabel(badge);
            badgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            badgeLabel.setForeground(TEXT_LIGHT_GRAY);
            badgeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7, 50), 1),
                new EmptyBorder(5, 10, 5, 10)
            ));
            badgesPanel.add(badgeLabel);
        }

        card.add(Box.createVerticalGlue());
        card.add(crownIcon);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(decorativeLine);
        card.add(subtitleLabel);
        card.add(badgesPanel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createEnhancedLoginCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(8, 8, getWidth()-8, getHeight()-8, 20, 20);

                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(230, 230, 235)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 20, 20);

                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(GOLD_COLOR);
                g2d.drawRoundRect(1, 1, getWidth()-10, getHeight()-10, 20, 20);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel privateIcon = new JLabel("🔐");
        privateIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        privateIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel privateTitle = new JLabel("Espace Privé Royal");
        privateTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        privateTitle.setForeground(TEXT_BLACK);
        privateTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel accessLabel = new JLabel("Accès réservé aux membres privilégiés");
        accessLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        accessLabel.setForeground(TEXT_GRAY);
        accessLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(privateIcon);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(privateTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(accessLabel);

        JPanel separator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(GOLD_COLOR);
                g2d.fillRect(getWidth()/3, getHeight()/2, getWidth()/3, 1);
            }
        };
        separator.setOpaque(false);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel emailLabel = new JLabel("📧 Adresse email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_BLACK);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = createStyledTextField();

        JLabel passwordLabel = new JLabel("🔑 Mot de passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_BLACK);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(new EmptyBorder(25, 0, 8, 0));

        passwordField = createStyledPasswordField();

        togglePasswordVisibilityButton = new JButton("👁️");
        togglePasswordVisibilityButton.setOpaque(false);
        togglePasswordVisibilityButton.setBorder(null);
        togglePasswordVisibilityButton.setContentAreaFilled(false);
        togglePasswordVisibilityButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordVisibilityButton, BorderLayout.EAST);

        formPanel.add(emailLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(passwordPanel);

        loginButton = createStyledButton();

        JPanel linksPanel = createLinksPanel();

        card.add(headerPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(separator);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 35)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(linksPanel);

        return card;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(INPUT_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBackground(INPUT_COLOR);
        field.setForeground(TEXT_BLACK);
        field.setCaretColor(GOLD_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7, 100), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(INPUT_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBackground(INPUT_COLOR);
        field.setForeground(TEXT_BLACK);
        field.setCaretColor(GOLD_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7, 100), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return field;
    }

    private JButton createStyledButton() {
        JButton button = new JButton("Accéder à l'Espace Royal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, BLUE_COLOR,
                    0, getHeight(), BLUE_COLOR.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                super.paintComponent(g);
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBackground(BLUE_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(18, 25, 18, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        return button;
    }

    private JPanel createLinksPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);

        forgotPasswordLabel = new JLabel("🔄 Mot de passe oublié ?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(TEXT_GRAY);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel separatorLabel = new JLabel("•");
        separatorLabel.setForeground(TEXT_GRAY);
        separatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        signupLabel = new JLabel("✨ Devenir membre");
        signupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signupLabel.setForeground(TEXT_GRAY);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(forgotPasswordLabel);
        panel.add(separatorLabel);
        panel.add(signupLabel);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 193, 7, 0),
                    getWidth()/2, 0, new Color(255, 193, 7, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth()/2, 1);

                gradient = new GradientPaint(
                    getWidth()/2, 0, new Color(255, 193, 7, 80),
                    getWidth(), 0, new Color(255, 193, 7, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(getWidth()/2, 0, getWidth()/2, 1);
            }
        };

        footer.setBackground(BACKGROUND_COLOR);
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        footer.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel copyrightLabel = new JLabel("© 2025 Pressing Royal - Tous droits réservés");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(TEXT_LIGHT_GRAY);

        JLabel separator1 = new JLabel("•");
        separator1.setForeground(TEXT_LIGHT_GRAY);

        JLabel infoLabel = new JLabel("Conçu avec passion en France");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(TEXT_LIGHT_GRAY);

        JLabel separator2 = new JLabel("•");
        separator2.setForeground(TEXT_LIGHT_GRAY);

        JLabel versionLabel = new JLabel("v2.5.1");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(100, 100, 110));

        footer.add(copyrightLabel);
        footer.add(separator1);
        footer.add(infoLabel);
        footer.add(separator2);
        footer.add(versionLabel);

        return footer;
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(GOLD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(BLUE_COLOR);
            }
        });

        togglePasswordVisibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordField.getEchoChar() == '\u2022') {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('\u2022');
                }
            }
        });

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginView.this,
                    "Un email de récupération sera envoyé à votre adresse.\nFonctionnalité en cours de développement.",
                    "Récupération de mot de passe",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(GOLD_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(TEXT_GRAY);
            }
        });

        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginView.this,
                    "Rejoignez notre communauté d'élégance !\nInscription prochainement disponible.",
                    "Devenir membre Royal",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                signupLabel.setForeground(GOLD_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                signupLabel.setForeground(TEXT_GRAY);
            }
        });

        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        };

        emailField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    private void handleLogin() {
    String email = emailField.getText().trim();
    char[] passwordChars = passwordField.getPassword();
    String password = new String(passwordChars);
    
    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Veuillez remplir tous les champs requis.",
            "Informations manquantes",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Authentification réussie
                    JOptionPane.showMessageDialog(this,
                        "Bienvenue dans votre espace royal !\nVous êtes maintenant connecté(e) à Pressing Royal.",
                        "Connexion réussie ✨",
                        JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("Connexion réussie pour : " + email);

                    SwingUtilities.invokeLater(() -> {
                        new DashboardView().setVisible(true);
                        dispose();
                    });
                } else {
                    // Authentification échouée
                    JOptionPane.showMessageDialog(this,
                        "Email ou mot de passe incorrect.\nVeuillez vérifier vos identifiants.",
                        "Erreur d'authentification",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Erreur de connexion à la base de données.\nVeuillez réessayer plus tard.",
            "Erreur technique",
            JOptionPane.ERROR_MESSAGE);
    } finally {
        // Nettoyer le mot de passe en mémoire
        Arrays.fill(passwordChars, '0');
    }
}
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }
}
