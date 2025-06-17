package View ;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.imageio.ImageIO;
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
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 191, 36);

    // Chemin vers le dossier des images
    private static final String IMAGES_PATH = "src/images/";

    public LoginView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    public void afficher() {
        this.setVisible(true);
    }

    /**
     * Charge une image depuis le dossier images et la redimensionne
     */
    private ImageIcon loadIcon(String filename, int width, int height) {
        try {
            File imageFile = new File(IMAGES_PATH + filename);
            if (imageFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imageFile);
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Image non trouvée: " + IMAGES_PATH + filename);
                // Retourne une icône par défaut si l'image n'est pas trouvée
                return createDefaultIcon(width, height);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image: " + filename);
            e.printStackTrace();
            return createDefaultIcon(width, height);
        }
    }

    /**
     * Crée une icône par défaut en cas d'erreur de chargement
     */
    private ImageIcon createDefaultIcon(int width, int height) {
        BufferedImage defaultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = defaultImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(TEXT_GRAY);
        g2d.fillOval(2, 2, width-4, height-4);
        g2d.setColor(TEXT_WHITE);
        g2d.drawOval(2, 2, width-4, height-4);
        g2d.dispose();
        return new ImageIcon(defaultImage);
    }

    /**
     * Crée une icône d'œil barré pour le mot de passe masqué
     */
    private ImageIcon createEyeHiddenIcon() {
        BufferedImage eyeHiddenImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = eyeHiddenImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner un œil simple
        g2d.setColor(TEXT_GRAY);
        g2d.setStroke(new BasicStroke(2f));
        
        // Forme de l'œil
        g2d.drawArc(4, 8, 16, 8, 0, 180);
        g2d.drawArc(4, 8, 16, 8, 180, 180);
        
        // Pupille
        g2d.fillOval(10, 10, 4, 4);
        
        // Ligne barrée
        g2d.setColor(PINK_ACCENT);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawLine(2, 22, 22, 2);
        
        g2d.dispose();
        return new ImageIcon(eyeHiddenImage);
    }

    private void initializeComponents() {
        setTitle("Pressing Royal - Interface Moderne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(10280, 720);  // Taille optimisée pour ordinateur portable
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1024, 600));  // Taille minimum réduite

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

                // Dégradé de fond moderne avec animation
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK_BACKGROUND,
                    getWidth(), getHeight(), new Color(20, 25, 40)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Effets lumineux d'arrière-plan améliorés
                g2d.setColor(new Color(0, 255, 255, 12));
                g2d.fillOval(-300, -300, 800, 800);
                g2d.setColor(new Color(255, 20, 147, 10));
                g2d.fillOval(getWidth()-500, getHeight()-500, 800, 800);
                
                // Particules lumineuses additionnelles
                g2d.setColor(new Color(138, 43, 226, 15));
                g2d.fillOval(getWidth()/2-200, -100, 400, 400);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));  // Espacement réduit

        JPanel presentationCard = createModernPresentationCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15);  // Espacement réduit entre les cartes
        contentPanel.add(presentationCard, gbc);

        JPanel loginCard = createModernLoginCard();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 0, 0);  // Espacement réduit
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

                // Effet de verre avec dégradé
                GradientPaint glassGradient = new GradientPaint(
                    0, 0, new Color(45, 52, 70, 200),
                    0, getHeight(), new Color(30, 35, 50, 180)
                );
                g2d.setPaint(glassGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Bordure néon cyan avec effet de lueur
                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(new Color(0, 255, 255, 150));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);

                // Effet de lueur externe
                g2d.setColor(new Color(0, 255, 255, 50));
                g2d.setStroke(new BasicStroke(6f));
                g2d.drawRoundRect(-1, -1, getWidth()+2, getHeight()+2, 30, 30);

                // Reflets de lumière
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(10, 10, getWidth()-20, 50, 20, 20);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 35, 40, 35));  // Espacement interne réduit

        JLabel titleLabel = new JLabel("Pressing Royal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 44));  // Taille réduite
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html><center>L'Excellence Vestimentaire<br><span style='color: #00FFFF; font-size: 18px;'>Réinventée pour l'Ère Moderne</span></center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(15, 0, 35, 0));  // Espacement réduit

        // Panneaux de fonctionnalités avec style moderne
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));

        String[][] features = {
            {"rocket.png", "Service Express", "Livraison en 24h garantie"},
            {"diamond.png", "Qualité Premium", "Technologie de pointe"},
            {"star.png", "Expérience 5 ¬_¬", "Satisfaction client assurée"}
        };

        for (String[] feature : features) {
            JPanel featurePanel = createFeaturePanel(feature[0], feature[1], feature[2]);
            featuresPanel.add(featurePanel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 15)));  // Espacement réduit
        }

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(subtitleLabel);
        card.add(featuresPanel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel createFeaturePanel(String iconFile, String title, String description) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond avec dégradé subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(55, 65, 81, 180),
                    getWidth(), getHeight(), new Color(45, 52, 70, 150)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Bordure avec lueur
                g2d.setColor(new Color(75, 85, 99, 120));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                // Effet de lueur interne
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(5, 5, getWidth()-10, 20, 15, 15);
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel iconLabel = new JLabel(loadIcon(iconFile, 32, 32));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_GRAY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(Box.createRigidArea(new Dimension(20, 0)), BorderLayout.CENTER);
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

                // Effet de verre avec dégradé
                GradientPaint glassGradient = new GradientPaint(
                    0, 0, new Color(45, 52, 70, 220),
                    0, getHeight(), new Color(30, 35, 50, 200)
                );
                g2d.setPaint(glassGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Bordure néon magenta
                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(new Color(255, 20, 147, 150));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);

                // Effet de lueur externe
                g2d.setColor(new Color(255, 20, 147, 50));
                g2d.setStroke(new BasicStroke(6f));
                g2d.drawRoundRect(-1, -1, getWidth()+2, getHeight()+2, 30, 30);

                // Reflets de lumière
                g2d.setColor(new Color(255, 255, 255, 25));
                g2d.fillRoundRect(10, 10, getWidth()-20, 50, 20, 20);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 40, 35, 40));  // Espacement interne réduit

        // En-tête
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        

        JLabel titleLabel = new JLabel("Connexion Sécurisée");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));  // Taille réduite
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Accédez à votre espace personnel");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));  // Espacement réduit
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));   // Espacement réduit
        headerPanel.add(subtitleLabel);

        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Email field avec icône
        JPanel emailLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailLabelPanel.setOpaque(false);
        JLabel emailIcon = new JLabel(loadIcon("email.png", 20, 20));
        JLabel emailLabel = new JLabel(" Adresse Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        emailLabel.setForeground(TEXT_WHITE);
        emailLabelPanel.add(emailIcon);
        emailLabelPanel.add(emailLabel);

        emailField = createModernTextField();

        // Password field avec icône
        JPanel passwordLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordLabelPanel.setOpaque(false);
        passwordLabelPanel.setBorder(new EmptyBorder(30, 0, 8, 0));
        JLabel passwordIcon = new JLabel(loadIcon("key.png", 20, 20));
        JLabel passwordLabel = new JLabel(" Mot de Passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordLabel.setForeground(TEXT_WHITE);
        passwordLabelPanel.add(passwordIcon);
        passwordLabelPanel.add(passwordLabel);

        passwordField = createModernPasswordField();

        togglePasswordVisibilityButton = new JButton();
        togglePasswordVisibilityButton.setIcon(loadIcon("eye.png", 24, 24));
        togglePasswordVisibilityButton.setOpaque(false);
        togglePasswordVisibilityButton.setBorder(null);
        togglePasswordVisibilityButton.setContentAreaFilled(false);
        togglePasswordVisibilityButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordVisibilityButton.setFocusPainted(false);
        togglePasswordVisibilityButton.setToolTipText("Afficher le mot de passe");  // Tooltip initial

        // Créer un panel avec layout null pour positionner l'icône à l'intérieur
        JPanel passwordPanel = new JPanel();
        passwordPanel.setOpaque(false);
        passwordPanel.setLayout(new OverlayLayout(passwordPanel));
        
        // Panel pour positionner l'icône à droite
        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 15));  // Marge de 15px du bord droit
        iconPanel.add(togglePasswordVisibilityButton);
        
        passwordPanel.add(iconPanel);  // Ajouter l'icône par-dessus
        passwordPanel.add(passwordField);  // Ajouter le champ en arrière-plan

        formPanel.add(emailLabelPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(emailField);
        formPanel.add(passwordLabelPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordPanel);

        loginButton = createModernButton();

        JPanel linksPanel = createModernLinksPanel();

        card.add(headerPanel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));  // Espacement réduit
        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 25)));  // Espacement réduit
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 20)));  // Espacement réduit
        card.add(linksPanel);

        return card;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond avec dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, INPUT_BACKGROUND,
                    0, getHeight(), new Color(45, 52, 70)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Bordure avec focus
                if (hasFocus()) {
                    g2d.setColor(CYAN_ACCENT);
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                }

                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(CYAN_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1),
            new EmptyBorder(18, 25, 18, 25)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        field.setPreferredSize(new Dimension(350, 55));  // Largeur cohérente avec le champ password

        // Placeholder effect amélioré
        field.setText("test@gmail.com");
        field.setForeground(TEXT_LIGHT_GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals("test@gmail.com")) {
                    field.setText("");
                    field.setForeground(TEXT_WHITE);
                }
                field.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("test@gmail.com");
                    field.setForeground(TEXT_LIGHT_GRAY);
                }
                field.repaint();
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

                // Fond avec dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, INPUT_BACKGROUND,
                    0, getHeight(), new Color(45, 52, 70)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Bordure avec focus
                if (hasFocus()) {
                    g2d.setColor(CYAN_ACCENT);
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);
                }

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
            new EmptyBorder(18, 25, 18, 60)  // Padding à droite augmenté pour l'icône
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        field.setPreferredSize(new Dimension(350, 55));  // Largeur augmentée

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });

        return field;
    }

    private JButton createModernButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Animation au survol
                Color startColor = getModel().isRollover() ? 
                    new Color(120, 169, 255) : BLUE_GRADIENT_START;
                Color endColor = getModel().isRollover() ? 
                    new Color(158, 63, 246) : BLUE_GRADIENT_END;

                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    getWidth(), getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Effet de lueur
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(100, 149, 237, 100));
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                }

                super.paintComponent(g);
            }
        };

        // Panneau pour l'icône et le texte
        JPanel buttonContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonContent.setOpaque(false);
        
        JLabel lockIcon = new JLabel(loadIcon("login.png", 24, 24));
        JLabel buttonText = new JLabel("Accéder à l'Espace Royal");
        buttonText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        buttonText.setForeground(Color.WHITE);
        
        buttonContent.add(lockIcon);
        buttonContent.add(buttonText);
        
        button.setLayout(new BorderLayout());
        button.add(buttonContent, BorderLayout.CENTER);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(20, 30, 20, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 65));  // Largeur cohérente avec les champs
        button.setPreferredSize(new Dimension(350, 65));

        return button;
    }

    private JPanel createModernLinksPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panel.setOpaque(false);

        // Mot de passe oublié avec icône
        forgotPasswordLabel = new JLabel();
        forgotPasswordLabel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JLabel resetIcon = new JLabel(loadIcon("reset.png", 16, 16));
        JLabel resetText = new JLabel("Mot de passe oublié ?");
        resetText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resetText.setForeground(TEXT_GRAY);
        
        JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        resetPanel.setOpaque(false);
        resetPanel.add(resetIcon);
        resetPanel.add(resetText);
        forgotPasswordLabel.add(resetPanel);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel separatorLabel = new JLabel("•");
        separatorLabel.setForeground(TEXT_GRAY);
        separatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Créer un compte avec icône
        signupLabel = new JLabel();
        JLabel signupIcon = new JLabel(loadIcon("signup.png", 16, 16));
        JLabel signupText = new JLabel("Créer un compte");
        signupText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        signupText.setForeground(TEXT_GRAY);
        
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        signupPanel.setOpaque(false);
        signupPanel.add(signupIcon);
        signupPanel.add(signupText);
        signupLabel.add(signupPanel);
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

        togglePasswordVisibilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (passwordField.getEchoChar() == '•') {
                    // Mot de passe visible
                    passwordField.setEchoChar((char) 0);
                    // Charger l'icône eye_hidden ou utiliser une icône par défaut
                    ImageIcon hiddenIcon = loadIcon("eye_hidden.png", 24, 24);
                    if (hiddenIcon != null) {
                        togglePasswordVisibilityButton.setIcon(hiddenIcon);
                    } else {
                        // Fallback: créer une icône barrée
                        togglePasswordVisibilityButton.setIcon(createEyeHiddenIcon());
                    }
                    togglePasswordVisibilityButton.setToolTipText("Masquer le mot de passe");
                } else {
                    // Mot de passe masqué
                    passwordField.setEchoChar('•');
                    togglePasswordVisibilityButton.setIcon(loadIcon("eye.png", 24, 24));
                    togglePasswordVisibilityButton.setToolTipText("Afficher le mot de passe");
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
                Component[] components = ((JPanel)forgotPasswordLabel.getComponent(0)).getComponents();
                if (components.length > 1 && components[1] instanceof JLabel) {
                    ((JLabel)components[1]).setForeground(CYAN_ACCENT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component[] components = ((JPanel)forgotPasswordLabel.getComponent(0)).getComponents();
                if (components.length > 1 && components[1] instanceof JLabel) {
                    ((JLabel)components[1]).setForeground(TEXT_GRAY);
                }
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
                Component[] components = ((JPanel)signupLabel.getComponent(0)).getComponents();
                if (components.length > 1 && components[1] instanceof JLabel) {
                    ((JLabel)components[1]).setForeground(PINK_ACCENT);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Component[] components = ((JPanel)signupLabel.getComponent(0)).getComponents();
                if (components.length > 1 && components[1] instanceof JLabel) {
                    ((JLabel)components[1]).setForeground(TEXT_GRAY);
                }
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