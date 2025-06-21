package View;

import dao.LivraisonDAO;
import modele.Livraison;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.sql.Connection;

public class LivraisonView extends JPanel {
    // Palette de couleurs moderne et cohérente
    private final Color primaryColor = new Color(79, 70, 229);      // Indigo moderne
    private final Color primaryLight = new Color(129, 140, 248);    // Indigo clair
    private final Color secondaryColor = new Color(16, 185, 129);   // Emerald
    private final Color accentOrange = new Color(249, 115, 22);     // Orange vif
    private final Color accentBlue = new Color(59, 130, 246);       // Blue moderne
    private final Color successColor = new Color(34, 197, 94);      // Green
    private final Color warningColor = new Color(245, 158, 11);     // Amber
    private final Color errorColor = new Color(239, 68, 68);        // Red
    
    // Couleurs de fond et texte
    private final Color backgroundColor = new Color(248, 250, 252); // Slate-50
    private final Color surfaceColor = Color.WHITE;
    private final Color surfaceElevated = new Color(255, 255, 255);
    private final Color textPrimary = new Color(15, 23, 42);        // Slate-900
    private final Color textSecondary = new Color(71, 85, 105);     // Slate-600
    private final Color textMuted = new Color(148, 163, 184);       // Slate-400
    private final Color borderColor = new Color(226, 232, 240);     // Slate-200
    private final Color borderLight = new Color(241, 245, 249);     // Slate-100

    private JPanel cardsContainer;
    private JTextField searchField;
    private Connection connection;
    private LivraisonDAO dao;

    public LivraisonView(Connection connection) {
        this.connection = connection;
        this.dao = new LivraisonDAO(connection);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        initComponents();
        loadLivraisonData();
    }

    LivraisonView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Header avec gradient subtil
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Contenu principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Barre de recherche redesignée
        JPanel searchPanel = createModernSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Stats et planning
        JPanel statsAndPlanningWrapper = new JPanel(new BorderLayout());
        statsAndPlanningWrapper.setBackground(backgroundColor);

        JPanel statsPanel = createEnhancedStatsPanel();
        statsAndPlanningWrapper.add(statsPanel, BorderLayout.NORTH);

        JPanel planningSection = createModernPlanningSection();
        statsAndPlanningWrapper.add(planningSection, BorderLayout.CENTER);

        contentPanel.add(statsAndPlanningWrapper, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient subtil de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 240),
                    0, getHeight(), new Color(248, 250, 252, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Bordure subtile
                g2d.setColor(new Color(226, 232, 240, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Titre avec icône
        JPanel titleSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleSection.setOpaque(false);

        JLabel iconLabel = new JLabel("📦");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));

        JLabel titleLabel = new JLabel("Gestion des Livraisons");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);

        JLabel subtitleLabel = new JLabel("Organisez et suivez vos livraisons en temps réel");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(textSecondary);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        
        JPanel titleAndIcon = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleAndIcon.setOpaque(false);
        titleAndIcon.add(iconLabel);
        titleAndIcon.add(titleLabel);
        
        titleWrapper.add(titleAndIcon, BorderLayout.NORTH);
        titleWrapper.add(subtitleLabel, BorderLayout.CENTER);

        // Bouton nouvelle livraison amélioré
        JButton newLivraisonBtn = createPremiumButton("✨ Nouvelle Livraison", primaryColor);
        newLivraisonBtn.addActionListener(e -> {
            LivraisonModal livraisonModal = new LivraisonModal(connection, this);
            livraisonModal.setVisible(true);
        });

        headerPanel.add(titleWrapper, BorderLayout.WEST);
        headerPanel.add(newLivraisonBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createModernSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(backgroundColor);
        searchPanel.setBorder(new EmptyBorder(0, 0, 32, 0));

        // Container avec ombre
        JPanel searchContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 12, 12);
                
                // Fond
                g2d.setColor(surfaceColor);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 12, 12);
                
                // Bordure
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-3, getHeight()-3, 12, 12);
                
                g2d.dispose();
            }
        };
        searchContainer.setOpaque(false);
        searchContainer.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Icône de recherche stylisée
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 12));

        // Champ de recherche
        searchField = new JTextField("Rechercher une livraison...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(textMuted);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        
        // Effet focus sur le champ
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Rechercher une livraison...")) {
                    searchField.setText("");
                    searchField.setForeground(textPrimary);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher une livraison...");
                    searchField.setForeground(textMuted);
                }
            }
        });

        // Bouton filtres moderne
        JButton filtresBtn = createIconButton("🎛️ Filtres", accentBlue);

        searchContainer.add(searchIcon, BorderLayout.WEST);
        searchContainer.add(searchField, BorderLayout.CENTER);

        searchPanel.add(searchContainer, BorderLayout.CENTER);
        searchPanel.add(filtresBtn, BorderLayout.EAST);

        return searchPanel;
    }

    private JButton createIconButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 15));
                    g2d.fillRoundRect(1, 1, getWidth()-1, getHeight()-1, 10, 10);
                } else {
                    g2d.setColor(new Color(0, 0, 0, 8));
                    g2d.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 10, 10);
                }
                
                // Fond avec gradient
                Color baseColor = getModel().isRollover() ? 
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), 240) : color;
                GradientPaint gradient = new GradientPaint(
                    0, 0, baseColor,
                    0, getHeight(), new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 10, 10);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 52));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private JPanel createEnhancedStatsPanel() {
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(backgroundColor);
        statsWrapper.setBorder(new EmptyBorder(0, 0, 32, 0));

       
       // statsTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 24, 0));
        statsPanel.setBackground(backgroundColor);

        // Cartes statistiques avec animations visuelles
        statsPanel.add(createPremiumStatCard("Total Livraisons", "12", primaryColor, "📦", "+2 cette semaine"));
        statsPanel.add(createPremiumStatCard("En Transit", "4", warningColor, "🚚", "Temps moyen: 2h"));
        statsPanel.add(createPremiumStatCard("Planifiées", "5", accentBlue, "⏰", "Prochaine: 14h30"));
        statsPanel.add(createPremiumStatCard("Livrées", "3", successColor, "✅", "100% à temps"));

        //statsWrapper.add(statsTitle, BorderLayout.NORTH);
        statsWrapper.add(statsPanel, BorderLayout.CENTER);

        return statsWrapper;
    }

    private JPanel createPremiumStatCard(String title, String value, Color color, String icon, String subtitle) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 16, 16);
                
                // Fond dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.WHITE,
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 16, 16);
                
                // Accent coloré en haut
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth()-4, 4, 16, 16);
                
                // Bordure subtile
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 16, 16);
                
                g2d.dispose();
            }
        };
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        // Icône avec fond coloré
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(48, 48));
        iconPanel.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconPanel.add(iconLabel);

        // Contenu textuel
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(textSecondary);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(textPrimary);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(textMuted);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModernPlanningSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(backgroundColor);

        // En-tête de section
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(backgroundColor);
        sectionHeader.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel sectionTitle = new JLabel("🚛 Planning des livraisons");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);

        JLabel sectionSubtitle = new JLabel("Vue d'ensemble de toutes vos livraisons");
        sectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionSubtitle.setForeground(textSecondary);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(sectionTitle, BorderLayout.NORTH);
        titleWrapper.add(sectionSubtitle, BorderLayout.CENTER);

        sectionHeader.add(titleWrapper, BorderLayout.WEST);

        // Container des cartes
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(backgroundColor);
        cardsContainer.setBorder(new EmptyBorder(0, 0, 50, 0));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(backgroundColor);
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        section.add(sectionHeader, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    public void loadLivraisonData() {
        cardsContainer.removeAll();

        List<Livraison> livraisons = dao.getToutesLesLivraisons();

        for (Livraison livraison : livraisons) {
            JPanel carte = creerCarteLivraisonPremium(livraison);
            cardsContainer.add(carte);
            cardsContainer.add(Box.createVerticalStrut(16));
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    public JPanel creerCarteLivraisonPremium(Livraison livraison) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée élégante
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 20, 20);
                
                // Fond avec gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.WHITE,
                    0, getHeight(), new Color(252, 252, 253)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                
                // Bordure avec couleur de statut
                Color statusColor = getStatusColor(livraison.getStatut());
                g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 40));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                
                g2d.dispose();
            }
        };
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Header de la carte
        JPanel headerPanel = createCardHeader(livraison);
        
        // Contenu principal
        JPanel contentPanel = createCardContent(livraison);
        
        // Actions de la carte
        JPanel actionsPanel = createCardActions(livraison);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(actionsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCardHeader(Livraison livraison) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Partie gauche avec ID et statut
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);

        // Icône avec statut
        JPanel iconContainer = createStatusIcon(livraison.getStatut());
        
        JLabel idLabel = new JLabel("LIV-" + String.format("%03d", livraison.getId()));
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idLabel.setForeground(textPrimary);
        idLabel.setBorder(new EmptyBorder(0, 12, 0, 16));

        JLabel statusBadge = createModernStatusBadge(livraison.getStatut());

        leftHeader.add(iconContainer);
        leftHeader.add(idLabel);
        leftHeader.add(statusBadge);

        // Partie droite avec heure
        JPanel timeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        timeContainer.setOpaque(false);
        
        JLabel timeIcon = new JLabel("🕐");
        timeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        JLabel timeLabel = new JLabel(livraison.getHeure().toString());
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setForeground(primaryColor);
        timeLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        timeContainer.add(timeIcon);
        timeContainer.add(timeLabel);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(timeContainer, BorderLayout.EAST);

        return header;
    }

    private JPanel createCardContent(Livraison livraison) {
        JPanel content = new JPanel(new GridLayout(3, 2, 16, 12));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Informations organisées en grid
        content.add(createInfoRow("👤", "Client", livraison.getNomClient()));
        content.add(createInfoRow("🚚", "Livreur", livraison.getNomLivreur()));
        content.add(createInfoRow("📍", "Adresse", livraison.getAdresse()));
        content.add(createInfoRow("📅", "Date", livraison.getDateLivraison().toString()));
        
        // Articles avec tags
        JPanel articlesPanel = createArticlesPanel(livraison.getArticles());
        content.add(articlesPanel);
        
        // Informations supplémentaires
        content.add(createInfoRow("📋", "Priorité", getPriorityText(livraison.getStatut())));

        return content;
    }

    private JPanel createInfoRow(String icon, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLabel.setPreferredSize(new Dimension(20, 20));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelText.setForeground(textMuted);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valueText.setForeground(textPrimary);

        textPanel.add(labelText, BorderLayout.NORTH);
        textPanel.add(valueText, BorderLayout.CENTER);

        row.add(iconLabel, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);

        return row;
    }

    private JPanel createArticlesPanel(String articles) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel label = new JLabel("Articles");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(textMuted);

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        tagsPanel.setOpaque(false);

        String[] articleArray = articles.split(",");
        for (String article : articleArray) {
            if (article.trim().length() > 0) {
                JLabel tag = createArticleTag(article.trim());
                tagsPanel.add(tag);
                tagsPanel.add(Box.createHorizontalStrut(4));
            }
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(tagsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createArticleTag(String article) {
        JLabel tag = new JLabel(article) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(241, 245, 249));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tag.setForeground(textSecondary);
        tag.setBorder(new EmptyBorder(4, 8, 4, 8));
        tag.setOpaque(false);
        return tag;
    }

    private JPanel createCardActions(Livraison livraison) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton editBtn = createActionButton("✏️", "Modifier", accentBlue);
        JButton trackBtn = createActionButton("📍", "Suivre", successColor);
        JButton moreBtn = createActionButton("⋯", "Plus", textMuted);

        actions.add(editBtn);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(trackBtn);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(moreBtn);

        return actions;
    }

    private JButton createActionButton(String icon, String tooltip, Color color) {
        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setForeground(color);
        button.setPreferredSize(new Dimension(32, 32));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        return button;
    }

    private JPanel createStatusIcon(String status) {
        JPanel iconContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color statusColor = getStatusColor(status);
                g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.setColor(statusColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(1, 1, getWidth()-3, getHeight()-3);
                
                g2d.dispose();
            }
        };
        
        iconContainer.setOpaque(false);
        iconContainer.setPreferredSize(new Dimension(40, 40));
        iconContainer.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(getStatusIcon(status), SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconContainer.add(iconLabel);

        return iconContainer;
    }

    private JLabel createModernStatusBadge(String status) {
        JLabel badge = new JLabel(status) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color statusColor = getStatusColor(status);
                g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 60));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(getStatusColor(status));
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        badge.setOpaque(false);

        return badge;
    }

    private JButton createPremiumButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 15));
                    g2d.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 14, 14);
                }
                
                // Gradient de fond
                Color startColor = getModel().isRollover() ? 
                    new Color(color.getRed() + 10, color.getGreen() + 10, color.getBlue() + 10) : color;
                Color endColor = new Color(
                    Math.max(0, startColor.getRed() - 20),
                    Math.max(0, startColor.getGreen() - 20),
                    Math.max(0, startColor.getBlue() - 20)
                );
                
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 14, 14);
                
                // Reflet subtil
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()/2, 14, 14);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 48));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });
        
        return button;
    }

    // Méthodes utilitaires pour les couleurs et icônes de statut
    private Color getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "en transit", "en cours" -> warningColor;
            case "normale", "planifiée", "en attente" -> accentBlue;
            case "livrée", "terminée" -> successColor;
            case "urgente" -> errorColor;
            case "retardée" -> new Color(239, 68, 68);
            default -> textMuted;
        };
    }

    private String getStatusIcon(String status) {
        return switch (status.toLowerCase()) {
            case "en transit", "en cours" -> "🚚";
            case "normale", "planifiée", "en attente" -> "⏰";
            case "livrée", "terminée" -> "✅";
            case "urgente" -> "🚨";
            case "retardée" -> "⚠️";
            default -> "📦";
        };
    }

    private String getPriorityText(String status) {
        return switch (status.toLowerCase()) {
            case "urgente" -> "Haute";
            case "en transit", "en cours" -> "Moyenne";
            case "normale", "planifiée" -> "Normale";
            default -> "Standard";
        };
    }
}