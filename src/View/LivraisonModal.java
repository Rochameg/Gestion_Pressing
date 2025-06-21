package View;

import dao.LivraisonDAO;
import modele.Livraison;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Connection;
import Utils.DatabaseConnection;
import java.util.Locale;

public class LivraisonModal extends JFrame {
    // Palette de couleurs moderne (inspirée du design de référence)
    private static final Color BACKGROUND_COLOR = new Color(44, 56, 74);      // Bleu-gris foncé
    private static final Color CARD_BACKGROUND = new Color(52, 64, 84);       // Légèrement plus clair
    private static final Color HEADER_START = new Color(139, 92, 246);        // Violet clair (gradient début)
    private static final Color HEADER_END = new Color(168, 85, 247);          // Violet plus intense (gradient fin)
    private static final Color INPUT_BACKGROUND = new Color(55, 65, 81);      // Gris-bleu pour les champs
    private static final Color INPUT_BORDER = new Color(75, 85, 99);          // Bordure des champs
    private static final Color INPUT_FOCUSED = new Color(139, 92, 246);       // Bordure focus (violet)
    private static final Color TEXT_PRIMARY = Color.WHITE;                    // Texte principal
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);     // Texte secondaire
    private static final Color TEXT_PLACEHOLDER = new Color(107, 114, 128);   // Placeholder
    private static final Color BUTTON_CANCEL = new Color(248, 113, 113);      // Rouge corail
    private static final Color BUTTON_SUCCESS = new Color(52, 211, 153);      // Vert emeraude
    private static final Color BUTTON_ADD = new Color(99, 102, 241);          // Bleu indigo

    private JComboBox<String> clientComboBox;
    private JComboBox<String> delivererComboBox;
    private JTextField addressField;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> priorityComboBox;
    private JPanel articlesPanel;
    private ArrayList<ArticleRow> articleRows;
    private int articleCounter = 0;
    private LivraisonView livraisonView;
    private Connection connection;

    public LivraisonModal(Connection connection, LivraisonView livraisonView) {
        this.connection = connection;
        this.livraisonView = livraisonView;
        setTitle("Planification de Livraison");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 750);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        
        // Créer une fenêtre avec coins arrondis
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    e.printStackTrace();
}


        articleRows = new ArrayList<>();
        initializeComponents();
        layoutComponents();
        addEventListeners();
        addArticleRow();
    }

    private void initializeComponents() {
       ArrayList<String> clients = fetchClientsFromDB();
clients.add(0, "Sélectionner un client");
clientComboBox = createStyledComboBox(clients.toArray(new String[0]));

ArrayList<String> livreurs = fetchLivreursFromDB();
livreurs.add(0, "Assigner un livreur");
delivererComboBox = createStyledComboBox(livreurs.toArray(new String[0]));

        priorityComboBox = createStyledComboBox(new String[]{"Normale", "Haute", "Urgente"});
        addressField = createStyledTextField("123 Rue de la Paix, 75001 Paris");
        dateField = createStyledTextField("Choisir une date");
        timeField = createStyledTextField("Choisir une heure");
        dateField.setEditable(false);
        timeField.setEditable(false);
    }
private ArrayList<String> fetchClientsFromDB() {
    ArrayList<String> clients = new ArrayList<>();
    try {
        var stmt = connection.createStatement();
        var rs = stmt.executeQuery("SELECT nom FROM clients");
        while (rs.next()) {
            clients.add(rs.getString("nom"));
        }
        rs.close();
        stmt.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return clients;
}

private ArrayList<String> fetchLivreursFromDB() {
    ArrayList<String> livreurs = new ArrayList<>();
    try {
        var stmt = connection.createStatement();
        var rs = stmt.executeQuery("SELECT nom FROM livreurs");
        while (rs.next()) {
            livreurs.add(rs.getString("nom"));
        }
        rs.close();
        stmt.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return livreurs;
}

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(INPUT_BACKGROUND);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BORDER, 12),
                new EmptyBorder(12, 16, 12, 16)
        ));
        comboBox.setUI(new ModernComboBoxUI());
        return comboBox;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(TEXT_PLACEHOLDER);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(INPUT_BORDER, 12),
                new EmptyBorder(12, 16, 12, 16)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(INPUT_FOCUSED, 12),
                        new EmptyBorder(11, 15, 11, 15)
                ));
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }

            public void focusLost(FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(INPUT_BORDER, 12),
                        new EmptyBorder(12, 16, 12, 16)
                ));
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_PLACEHOLDER);
                }
            }
        });
        return field;
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // En-tête avec gradient violet
        JPanel headerPanel = createGradientHeader();
        
        // Contenu principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(75, 85, 99), 16),
                new EmptyBorder(24, 24, 24, 24)
        ));

        // Section Client et Livreur
        JPanel clientDelivererPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        clientDelivererPanel.setBackground(CARD_BACKGROUND);
        clientDelivererPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel clientSection = createSection("Client", clientComboBox);
        JPanel delivererSection = createSection("Livreur", delivererComboBox);
        clientDelivererPanel.add(clientSection);
        clientDelivererPanel.add(delivererSection);
        formPanel.add(clientDelivererPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Section Adresse
        JPanel addressSection = createSection("Adresse de livraison", addressField);
        formPanel.add(addressSection);
        formPanel.add(Box.createVerticalStrut(20));

        // Section Date et Heure
        JPanel dateTimePanel = new JPanel(new GridLayout(1, 2, 16, 0));
        dateTimePanel.setBackground(CARD_BACKGROUND);
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel dateSection = createSection("Date de livraison", dateField);
        JPanel timeSection = createSection("Heure prévue", timeField);
        dateTimePanel.add(dateSection);
        dateTimePanel.add(timeSection);
        formPanel.add(dateTimePanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Section Priorité
        JPanel prioritySection = createSection("Priorité", priorityComboBox);
        formPanel.add(prioritySection);
        formPanel.add(Box.createVerticalStrut(20));

        // Section Articles
        JLabel articlesLabel = new JLabel("Articles à livrer");
        articlesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        articlesLabel.setForeground(TEXT_PRIMARY);
        articlesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        articlesLabel.setBorder(new EmptyBorder(0, 0, 12, 0));
        formPanel.add(articlesLabel);

        articlesPanel = new JPanel();
        articlesPanel.setLayout(new BoxLayout(articlesPanel, BoxLayout.Y_AXIS));
        articlesPanel.setBackground(CARD_BACKGROUND);

        JScrollPane articlesScrollPane = new JScrollPane(articlesPanel);
        articlesScrollPane.setBorder(new RoundedBorder(INPUT_BORDER, 12));
        articlesScrollPane.setBackground(CARD_BACKGROUND);
        articlesScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        articlesScrollPane.setPreferredSize(new Dimension(articlesScrollPane.getPreferredSize().width, 180));
        articlesScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        articlesScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        formPanel.add(articlesScrollPane);
        formPanel.add(Box.createVerticalStrut(12));

        // Bouton Ajouter Article
        JButton addArticleButton = createAddButton("+ Ajouter un article");
        addArticleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addArticleButton.addActionListener(e -> addArticleRow());
        formPanel.add(addArticleButton);

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(24, 0, 0, 0));
        
        JButton cancelButton = createCancelButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        JButton planButton = createSuccessButton("Planifier la livraison");
        planButton.addActionListener(e -> planDelivery());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(12));
        buttonPanel.add(planButton);
        formPanel.add(buttonPanel);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createGradientHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, HEADER_START,
                    getWidth(), 0, HEADER_END
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(700, 80));
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Icône et titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel("🚚");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        
        JLabel titleLabel = new JLabel("Planifier une Livraison");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Bouton de fermeture
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 255, 255, 30));
        closeButton.setBorder(new EmptyBorder(8, 12, 8, 12));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JButton createAddButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(BUTTON_ADD.getRed(), BUTTON_ADD.getGreen(), BUTTON_ADD.getBlue(), 20));
        button.setForeground(BUTTON_ADD);
        button.setBorder(new RoundedBorder(BUTTON_ADD, 12, new EmptyBorder(12, 20, 12, 20)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(BUTTON_ADD.getRed(), BUTTON_ADD.getGreen(), BUTTON_ADD.getBlue(), 40));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(BUTTON_ADD.getRed(), BUTTON_ADD.getGreen(), BUTTON_ADD.getBlue(), 20));
            }
        });
        
        return button;
    }

    private JButton createCancelButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(BUTTON_CANCEL);
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(BUTTON_CANCEL, 12, new EmptyBorder(12, 24, 12, 24)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_CANCEL.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_CANCEL);
            }
        });
        
        return button;
    }

    private JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(BUTTON_SUCCESS);
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedBorder(BUTTON_SUCCESS, 12, new EmptyBorder(12, 24, 12, 24)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_SUCCESS.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_SUCCESS);
            }
        });
        
        return button;
    }

    private JPanel createSection(String labelText, JComponent component) {
        JPanel sectionPanel = new JPanel(new BorderLayout(0, 8));
        sectionPanel.setBackground(CARD_BACKGROUND);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        
        sectionPanel.add(label, BorderLayout.NORTH);
        sectionPanel.add(component, BorderLayout.CENTER);
        return sectionPanel;
    }

    // Classe pour les bordures arrondies
    private static class RoundedBorder extends LineBorder {
        private int radius;
        private EmptyBorder padding;

        public RoundedBorder(Color color, int radius) {
            super(color, 1, true);
            this.radius = radius;
        }
        
        public RoundedBorder(Color color, int radius, EmptyBorder padding) {
            super(color, 1, true);
            this.radius = radius;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getLineColor());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            if (padding != null) {
                return padding.getBorderInsets(c);
            }
            return new Insets(1, 1, 1, 1);
        }
    }

    // Reste des méthodes identiques...
    private void addEventListeners() {
        dateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDatePicker();
            }
        });
        timeField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTimePicker();
            }
        });
    }

    private void showDatePicker() {
        JDialog dateDialog = new JDialog(this, "Choisir une date", true);
        dateDialog.setSize(300, 250);
        dateDialog.setLocationRelativeTo(dateField);
        dateDialog.getContentPane().setBackground(CARD_BACKGROUND);

        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(CARD_BACKGROUND);
        datePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel calendarPanel = new JPanel(new GridLayout(0, 7));
        calendarPanel.setBackground(CARD_BACKGROUND);
        String[] daysOfWeek = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(TEXT_SECONDARY);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            calendarPanel.add(dayLabel);
        }

        for (int i = 1; i <= 31; i++) {
            final int day = i;
            JButton dayButton = new JButton(String.valueOf(i));
            dayButton.setBackground(INPUT_BACKGROUND);
            dayButton.setForeground(TEXT_PRIMARY);
            dayButton.setBorder(new EmptyBorder(4, 4, 4, 4));
            dayButton.setFocusPainted(false);
            dayButton.addActionListener(e -> {
                dateField.setText(day + " juin 2025");
                dateField.setForeground(TEXT_PRIMARY);
                dateDialog.dispose();
            });
            calendarPanel.add(dayButton);
        }

        datePanel.add(calendarPanel, BorderLayout.CENTER);
        dateDialog.add(datePanel);
        dateDialog.setVisible(true);
    }

    private void showTimePicker() {
        JDialog timeDialog = new JDialog(this, "Choisir une heure", true);
        timeDialog.setSize(200, 300);
        timeDialog.setLocationRelativeTo(timeField);
        timeDialog.getContentPane().setBackground(CARD_BACKGROUND);

        JPanel timePanel = new JPanel(new GridLayout(0, 2));
        timePanel.setBackground(CARD_BACKGROUND);
        timePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                final String time = String.format("%02d:%02d", hour, minute);
                JButton timeButton = new JButton(time);
                timeButton.setBackground(INPUT_BACKGROUND);
                timeButton.setForeground(TEXT_PRIMARY);
                timeButton.setBorder(new EmptyBorder(4, 4, 4, 4));
                timeButton.setFocusPainted(false);
                timeButton.addActionListener(e -> {
                    timeField.setText(time);
                    timeField.setForeground(TEXT_PRIMARY);
                    timeDialog.dispose();
                });
                timePanel.add(timeButton);
            }
        }

        timeDialog.add(timePanel);
        timeDialog.setVisible(true);
    }

    private void addArticleRow() {
        ArticleRow row = new ArticleRow(this, articleCounter++);
        articleRows.add(row);
        articlesPanel.add(row.getPanel());
        articlesPanel.revalidate();
        articlesPanel.repaint();
        
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, articlesPanel);
        if (scrollPane != null) {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        }
    }

    private void removeArticleRow(ArticleRow row) {
        articleRows.remove(row);
        articlesPanel.remove(row.getPanel());
        articlesPanel.revalidate();
        articlesPanel.repaint();
    }

    private void planDelivery() {
        try {
            Livraison livraison = new Livraison();
            livraison.setClientId(clientComboBox.getSelectedIndex());
            livraison.setLivreurId(delivererComboBox.getSelectedIndex());
            livraison.setAdresseLivraison(addressField.getText());

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
            livraison.setDateLivraison(sdfDate.parse(dateField.getText()));

            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
            livraison.setHeureLivraison(new java.sql.Time(sdfTime.parse(timeField.getText()).getTime()));

            livraison.setPriorite((String) priorityComboBox.getSelectedItem());

            StringBuilder articles = new StringBuilder();
            for (ArticleRow row : articleRows) {
                String article = row.getArticleName().trim();
                if (!article.isEmpty()) {
                    articles.append(article).append("; ");
                }
            }
            livraison.setArticles(articles.toString());
            livraison.setStatut("En attente");

            LivraisonDAO dao = new LivraisonDAO(connection);
            if (dao.ajouterLivraison(livraison)) {
                JOptionPane.showMessageDialog(this, "Livraison ajoutée avec succès !");
                if (livraisonView != null) {
                    livraisonView.loadLivraisonData();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la livraison.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date ou d'heure invalide. Veuillez vérifier la saisie.", "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static class ArticleRow {
        private JPanel panel;
        private JTextField articleField;
        private JButton deleteButton;
        private int id;
        private LivraisonModal parent;

        public ArticleRow(LivraisonModal parent, int id) {
            this.parent = parent;
            this.id = id;
            createPanel();
        }

        private void createPanel() {
            panel = new JPanel(new BorderLayout(12, 0));
            panel.setBackground(CARD_BACKGROUND);
            panel.setBorder(new EmptyBorder(6, 12, 6, 12));

            articleField = new JTextField("Nom de l'article");
            articleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            articleField.setBackground(INPUT_BACKGROUND);
            articleField.setForeground(TEXT_PLACEHOLDER);
            articleField.setCaretColor(TEXT_PRIMARY);
            articleField.setBorder(new RoundedBorder(INPUT_BORDER, 8, new EmptyBorder(8, 12, 8, 12)));

            articleField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent evt) {
                    if (articleField.getText().equals("Nom de l'article")) {
                        articleField.setText("");
                        articleField.setForeground(TEXT_PRIMARY);
                    }
                }
                public void focusLost(FocusEvent evt) {
                    if (articleField.getText().isEmpty()) {
                        articleField.setText("Nom de l'article");
                        articleField.setForeground(TEXT_PLACEHOLDER);
                    }
                }
            });

            deleteButton = new JButton("✕");
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteButton.setBackground(new Color(BUTTON_CANCEL.getRed(), BUTTON_CANCEL.getGreen(), BUTTON_CANCEL.getBlue(), 30));
            deleteButton.setForeground(BUTTON_CANCEL);
            deleteButton.setBorder(new RoundedBorder(BUTTON_CANCEL, 6, new EmptyBorder(6, 8, 6, 8)));
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.addActionListener(e -> parent.removeArticleRow(this));

            panel.add(articleField, BorderLayout.CENTER);
            panel.add(deleteButton, BorderLayout.EAST);
        }

        public JPanel getPanel() {
            return panel;
        }

        public String getArticleName() {
            String text = articleField.getText();
            return text.equals("Nom de l'article") ? "" : text;
        }
    }

    private static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            button.setBackground(INPUT_BACKGROUND);
            button.setForeground(TEXT_SECONDARY);
            button.setBorder(new EmptyBorder(0, 0, 0, 0));
            button.setFocusPainted(false);
            return button;
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = INPUT_BORDER;
            this.trackColor = INPUT_BACKGROUND;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}