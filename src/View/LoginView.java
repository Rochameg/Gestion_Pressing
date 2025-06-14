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

    // Couleurs modernes inspirées de l'interface
    private static final Color DARK_BACKGROUND = new Color(30, 35, 50);
    private static final Color CARD_BACKGROUND = new Color(45, 52, 70);
    private static final Color CYAN_ACCENT = new Color(0, 255, 255);
    private static final Color PURPLE_ACCENT = new Color(138, 43, 226);
    private static final Color PINK_ACCENT = new Color(255, 20, 147);
    private static final Color BLUE_GRADIENT_START = new Color(100, 149, 237);
    private static final Color BLUE_GRADIENT_END = new Color(138, 43, 226);
    private static final Color TEXT_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_GRAY = new Color(160, 174, 192);
    private static final Color TEXT_LIGHT_GRAY = new Color(100, 116, 139);
    private static final Color INPUT_BACKGROUND = new Color(55, 65, 81);
    private static final Color INPUT_BORDER = new Color(75, 85, 99);
    private static final Color HOVER_COLOR = new Color(67, 56, 202);

    public LoginView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    public void afficher() {
        this.setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Pressing Royal - Interface Moderne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(DARK_BACKGROUND);
        setLayout(new BorderLayout());
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Dégradé de fond moderne
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_BACKGROUND,
                    getWidth(), getHeight(), new Color(20, 25, 40)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effets lumineux d'arrière-plan
                g2d.setColor(new Color(0, 255, 255, 10));
                g2d.fillOval(-200, -200, 600, 600);
                g2d.setColor(new Color(255, 20, 147, 8));
                g2d.fillOval(getWidth()-400, getHeight()-400, 600, 600);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(50, 80, 50, 80));

        JPanel presentationCard = createModernPresentationCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 30);
        contentPanel.add(presentationCard, gbc);

        JPanel loginCard = createModernLoginCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 30, 0, 0);
        contentPanel.add(loginCard, gbc);

        mainPanel.add(contentPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createModernPresentationCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Bordure néon cyan
                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(CYAN_ACCENT);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 25, 25);

                // Fond avec transparence
                g2d.setColor(new Color(45, 52, 70, 180));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 25, 25);

                // Effet de lueur
                g2d.setColor(new Color(0, 255, 255, 30));
                g2d.setStroke(new BasicStroke(4f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(80, 60, 80, 60));

        JLabel titleLabel = new JLabel("Pressing Royal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html><center>L'Excellence Vestimentaire<br><span style='color: #00FFFF;'>Réinventée pour l'Ère Moderne</span></center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(20, 0, 40, 0));

        // Panneaux de fonctionnalités avec style moderne
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));

        String[][] features = {
            {"🚀", "Service Express", "Livraison en 24h"},
            {"💎", "Qualité Premium", "Technologie avancée"},
            {"⭐", "Expérience 5D", "Satisfaction garantie"}
        };

        for (String[] feature : features) {
            JPanel featurePanel = createFeaturePanel(feature[0], feature[1], feature[2]);
            featuresPanel.add(featurePanel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(subtitleLabel);
        card.add(featuresPanel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createFeaturePanel(String icon, String title, String description) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(55, 65, 81, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(75, 85, 99, 100));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_GRAY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(Box.createRigidArea(new Dimension(15, 0)), BorderLayout.CENTER);
        panel.add(textPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createModernLoginCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Bordure néon magenta
                g2d.setStroke(new BasicStroke(2f));
                g2d.setColor(PINK_ACCENT);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 25, 25);

                // Fond avec transparence
                g2d.setColor(new Color(45, 52, 70, 200));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 25, 25);

                // Effet de lueur
                g2d.setColor(new Color(255, 20, 147, 30));
                g2d.setStroke(new BasicStroke(4f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(60, 60, 60, 60));

        // En-tête
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel lockIcon = new JLabel("🔒");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lockIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Connexion Sécurisée");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Accédez à votre espace personnel");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lockIcon);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(subtitleLabel);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel emailLabel = new JLabel("📧 Adresse Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_WHITE);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = createModernTextField();

        JLabel passwordLabel = new JLabel("🔑 Mot de Passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_WHITE);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(new EmptyBorder(25, 0, 8, 0));

        passwordField = createModernPasswordField();

        togglePasswordVisibilityButton = new JButton("👁️");
        togglePasswordVisibilityButton.setOpaque(false);
        togglePasswordVisibilityButton.setBorder(null);
        togglePasswordVisibilityButton.setContentAreaFilled(false);
        togglePasswordVisibilityButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordVisibilityButton.setForeground(TEXT_GRAY);

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

        loginButton = createModernButton();

        JPanel linksPanel = createModernLinksPanel();

        card.add(headerPanel);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 35)));
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(linksPanel);

        return card;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(INPUT_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(CYAN_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Placeholder effect
        field.setText("test@gmail.com");
        field.setForeground(TEXT_LIGHT_GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals("test@gmail.com")) {
                    field.setText("");
                    field.setForeground(TEXT_WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("test@gmail.com");
                    field.setForeground(TEXT_LIGHT_GRAY);
                }
            }
        });

        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(INPUT_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(CYAN_ACCENT);
        field.setEchoChar('•');
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return field;
    }

    private JButton createModernButton() {
        JButton button = new JButton("🔐 Accéder à l'Espace Royal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, BLUE_GRADIENT_START,
                    getWidth(), getHeight(), BLUE_GRADIENT_END
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                super.paintComponent(g);
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(18, 25, 18, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        return button;
    }

    private JPanel createModernLinksPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        panel.setOpaque(false);

        forgotPasswordLabel = new JLabel("🔄 Mot de passe oublié ?");
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(TEXT_GRAY);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel separatorLabel = new JLabel("•");
        separatorLabel.setForeground(TEXT_GRAY);
        separatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        signupLabel = new JLabel("✨ Créer un compte");
        signupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signupLabel.setForeground(TEXT_GRAY);
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(forgotPasswordLabel);
        panel.add(separatorLabel);
        panel.add(signupLabel);

        return panel;
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
                // Effet de survol avec animation
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Retour à l'état normal
            }
        });

        togglePasswordVisibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordField.getEchoChar() == '•') {
                    passwordField.setEchoChar((char) 0);
                    togglePasswordVisibilityButton.setText("🙈");
                } else {
                    passwordField.setEchoChar('•');
                    togglePasswordVisibilityButton.setText("👁️");
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
                forgotPasswordLabel.setForeground(CYAN_ACCENT);
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
                signupLabel.setForeground(PINK_ACCENT);
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
        if (email.equals("test@gmail.com")) {
            email = ""; // Clear placeholder
        }
        
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