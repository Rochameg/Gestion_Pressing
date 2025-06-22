package View;

import Utils.DatabaseConnection;
import dao.LivraisonDAO;
import modele.Livraison;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.util.Map;
import java.util.HashMap;


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
    LivraisonDAO dao;
    private List<Livraison> livraisonCache;
    private long lastCacheUpdate = 0;
   

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
private void loadLivraisonDataWithAnimation() {
    cardsContainer.removeAll();
    
    // Afficher un indicateur de chargement
    JPanel loadingPanel = new JPanel(new BorderLayout());
    loadingPanel.setBackground(backgroundColor);
    
    JLabel loadingLabel = new JLabel("Chargement des livraisons...", JLabel.CENTER);
    loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
    loadingLabel.setForeground(textSecondary);
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setPreferredSize(new Dimension(200, 10));
    
    loadingPanel.add(loadingLabel, BorderLayout.CENTER);
    loadingPanel.add(progressBar, BorderLayout.SOUTH);
    loadingPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
    
    cardsContainer.add(loadingPanel);
    cardsContainer.revalidate();
    cardsContainer.repaint();
    
    // Chargement asynchrone
    new SwingWorker<List<Livraison>, Void>() {
        @Override
        protected List<Livraison> doInBackground() throws Exception {
            return getLivraisonsWithCache();
            
        }
        
        @Override
        protected void done() {
            try {
                List<Livraison> livraisons = get();
                displayLivraisons(livraisons);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(LivraisonView.this,
                    "Erreur lors du chargement des livraisons",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private List<Livraison> getLivraisonsWithCache() {
            long now = System.currentTimeMillis();
            if (livraisonCache == null || now - lastCacheUpdate > 30000) {
        livraisonCache = dao.getToutesLesLivraisons();
        lastCacheUpdate = now;
    }
    return livraisonCache; // Retournez le cache des livraisons
}
    }.execute();
}

private void displayLivraisons(List<Livraison> livraisons) {
    cardsContainer.removeAll();
    
    if (livraisons.isEmpty()) {
        JLabel emptyLabel = new JLabel("Aucune livraison trouvée", JLabel.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emptyLabel.setForeground(textSecondary);
        cardsContainer.add(emptyLabel);
    } else {
        for (Livraison livraison : livraisons) {
            JPanel card = creerCarteLivraisonPremium(livraison);
            card.setToolTipText("Livraison pour " + livraison.getNomClient() + " - " + livraison.getStatut());
            cardsContainer.add(card);
            cardsContainer.add(Box.createVerticalStrut(16));
        }
    }
    
    cardsContainer.revalidate();
    cardsContainer.repaint();
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
            Livraison livraison = null;
        LivraisonModal livraisonModal = new LivraisonModal(connection, this, livraison); // 'this' est crucial
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
    filtresBtn.addActionListener(e -> showFilterDialog());

    searchContainer.add(searchIcon, BorderLayout.WEST);
    searchContainer.add(searchField, BorderLayout.CENTER);

    // Ajoutez un écouteur pour la recherche en temps réel
    searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filterLivraisons();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filterLivraisons();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filterLivraisons();
        }
    });

    searchPanel.add(searchContainer, BorderLayout.CENTER);
    searchPanel.add(filtresBtn, BorderLayout.EAST);

    return searchPanel;
}
    private void showFilterDialog() {
    JDialog filterDialog = new JDialog();
    filterDialog.setTitle("Filtrer les livraisons");
    filterDialog.setLayout(new BorderLayout());
    filterDialog.setSize(300, 400);
    filterDialog.setLocationRelativeTo(this);
    filterDialog.setModal(true);

    JPanel filterPanel = new JPanel(new GridLayout(0, 1, 10, 10));
    filterPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Filtre par statut
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.add(new JLabel("Statut:"), BorderLayout.NORTH);
    
    String[] statuses = {"Tous", "Planifiée", "En cours", "Livrée", "Retardée", "Urgente"};
    JComboBox<String> statusCombo = new JComboBox<>(statuses);
    statusPanel.add(statusCombo, BorderLayout.CENTER);

    // Filtre par date
    JPanel datePanel = new JPanel(new BorderLayout());
    datePanel.add(new JLabel("Date:"), BorderLayout.NORTH);
    
    JComboBox<String> dateCombo = new JComboBox<>(new String[]{"Toutes", "Aujourd'hui", "Cette semaine", "Ce mois"});
    datePanel.add(dateCombo, BorderLayout.CENTER);

    // Filtre par priorité
    JPanel priorityPanel = new JPanel(new BorderLayout());
    priorityPanel.add(new JLabel("Priorité:"), BorderLayout.NORTH);
    
    JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Toutes", "Normale", "Moyenne", "Haute"});
    priorityPanel.add(priorityCombo, BorderLayout.CENTER);

    filterPanel.add(statusPanel);
    filterPanel.add(datePanel);
    filterPanel.add(priorityPanel);

    // Boutons Appliquer/Annuler
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton applyButton = new JButton("Appliquer");
    JButton cancelButton = new JButton("Annuler");

    applyButton.addActionListener(e -> {
        // Récupérer les valeurs des filtres
        String selectedStatus = statusCombo.getSelectedItem().toString();
        String selectedDate = dateCombo.getSelectedItem().toString();
        String selectedPriority = priorityCombo.getSelectedItem().toString();
        
        // Appliquer le filtre
        filterLivraisons(selectedStatus, selectedDate, selectedPriority);
        filterDialog.dispose();
    });

    cancelButton.addActionListener(e -> filterDialog.dispose());

    buttonPanel.add(cancelButton);
    buttonPanel.add(applyButton);

    filterDialog.add(filterPanel, BorderLayout.CENTER);
    filterDialog.add(buttonPanel, BorderLayout.SOUTH);
    filterDialog.setVisible(true);
}

private void filterLivraisons() {
    String searchText = searchField.getText().toLowerCase();
    if (searchText.equals("rechercher une livraison...")) {
        searchText = "";
    }
    
    filterLivraisons("Tous", "Toutes", "Toutes", searchText);
}

private void filterLivraisons(String statusFilter, String dateFilter, String priorityFilter) {
    filterLivraisons(statusFilter, dateFilter, priorityFilter, searchField.getText().toLowerCase());
}

private void filterLivraisons(String statusFilter, String dateFilter, String priorityFilter, String searchText) {
    cardsContainer.removeAll();
    List<Livraison> livraisons = dao.getToutesLesLivraisons();
  for (Livraison livraison : livraisons) {
    // Filtre par statut
    boolean statusMatch = statusFilter.equals("Tous") || 
                          (livraison.getStatut() != null && livraison.getStatut().equalsIgnoreCase(statusFilter));

    // Filtre par texte de recherche
    String nomClient = livraison.getNomClient();
    String adresse = livraison.getAdresse();
    String nomLivreur = livraison.getNomLivreur();

    boolean textMatch = searchText.isEmpty() ||
                        (nomClient != null && nomClient.toLowerCase().contains(searchText)) ||
                        (adresse != null && adresse.toLowerCase().contains(searchText)) ||
                        (nomLivreur != null && nomLivreur.toLowerCase().contains(searchText)) ||
                        String.valueOf(livraison.getId()).contains(searchText);

    // Filtre par priorité
    boolean priorityMatch = priorityFilter.equals("Toutes") ||
                            (livraison.getStatut() != null && getPriorityText(livraison.getStatut()).equalsIgnoreCase(priorityFilter));

    // Filtre par date (à personnaliser)
    boolean dateMatch = true;

    if (statusMatch && textMatch && priorityMatch && dateMatch) {
        cardsContainer.add(creerCarteLivraisonPremium(livraison));
        cardsContainer.add(Box.createVerticalStrut(16));
    }
}

cardsContainer.revalidate();
cardsContainer.repaint();

}
// Ajoutez ces méthodes dans la partie "méthodes utilitaires" de votre classe
private Map<String, Object> calculateStats(List<Livraison> livraisons) {
    Map<String, Object> stats = new HashMap<>();
    
    // Total des livraisons
    stats.put("total", livraisons.size());
    
    // Livraisons en transit
    long enTransit = livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("en transit".equalsIgnoreCase(l.getStatut()) || 
                "en cours".equalsIgnoreCase(l.getStatut())))
        .count();
    stats.put("enTransit", enTransit);
    
    // Livraisons planifiées
    long planifiees = livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("planifiée".equalsIgnoreCase(l.getStatut()) || 
                "en attente".equalsIgnoreCase(l.getStatut())))
        .count();
    stats.put("planifiees", planifiees);
    
    // Livraisons livrées
    long livrees = livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("livrée".equalsIgnoreCase(l.getStatut()) || 
                "terminée".equalsIgnoreCase(l.getStatut())))
        .count();
    stats.put("livrees", livrees);
    
    // Calcul du temps moyen (exemple simplifié)
    stats.put("tempsMoyen", calculerTempsMoyen(livraisons));
    
    // Pourcentage à temps
    stats.put("pourcentageATemps", calculerPourcentageATemps(livraisons));
    
    // Prochaine livraison
    stats.put("prochaineLivraison", trouverProchaineLivraison(livraisons));
    
    // Variation hebdomadaire
    stats.put("variationHebdo", calculerVariationHebdomadaire(livraisons));
    
    return stats;
}

private String calculerTempsMoyen(List<Livraison> livraisons) {
    // Implémentez votre logique de calcul ici
    // Par exemple, moyenne des temps de livraison
    return "2h"; // Valeur par défaut
}

private String calculerPourcentageATemps(List<Livraison> livraisons) {
    long totalLivrees = livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("livrée".equalsIgnoreCase(l.getStatut()) || 
                "terminée".equalsIgnoreCase(l.getStatut())))
        .count();
    
    long aTemps = livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("livrée".equalsIgnoreCase(l.getStatut()) || 
                "terminée".equalsIgnoreCase(l.getStatut())))
        .filter(l -> /* condition pour livraison à temps */ true)
        .count();
    
    if (totalLivrees == 0) return "100%";
    return Math.round((aTemps * 100.0 / totalLivrees)) + "%";
}

private String trouverProchaineLivraison(List<Livraison> livraisons) {
    // Trouver la prochaine livraison planifiée
    // Retourne l'heure ou "Aucune"
    return livraisons.stream()
        .filter(l -> l.getStatut() != null && 
               ("planifiée".equalsIgnoreCase(l.getStatut()) || 
                "en attente".equalsIgnoreCase(l.getStatut())))
        .sorted(/* par date */)
        .findFirst()
        .map(l -> l.getDateLivraison().toString())
        .orElse("Aucune");
}

private String calculerVariationHebdomadaire(List<Livraison> livraisons) {
    // Comparaison avec la semaine précédente
    return "+2 cette semaine"; // Valeur par défaut
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

    JPanel statsPanel = new JPanel(new GridLayout(1, 4, 24, 0));
    statsPanel.setBackground(backgroundColor);

    // Récupérer les données dynamiques
    List<Livraison> livraisons;
try {
    livraisons = getLivraisonsWithCache();
} catch (SQLException e) {
    e.printStackTrace();
    livraisons = new ArrayList<>(); // ou null, selon ce que tu veux afficher par défaut
}
    Map<String, Object> stats = calculateStats(livraisons);

    // Cartes statistiques dynamiques
    statsPanel.add(createPremiumStatCard(
        "Total Livraisons", 
        String.valueOf(stats.get("total")), 
        primaryColor, 
        "📦", 
        stats.get("variationHebdo").toString()
    ));
    
    statsPanel.add(createPremiumStatCard(
        "En Transit", 
        String.valueOf(stats.get("enTransit")), 
        warningColor, 
        "🚚", 
        "Temps moyen: " + stats.get("tempsMoyen")
    ));
    
    statsPanel.add(createPremiumStatCard(
        "Planifiées", 
        String.valueOf(stats.get("planifiees")), 
        accentBlue, 
        "⏰", 
        "Prochaine: " + stats.get("prochaineLivraison")
    ));
    
    statsPanel.add(createPremiumStatCard(
        "Livrées", 
        String.valueOf(stats.get("livrees")), 
        successColor, 
        "✅", 
        stats.get("pourcentageATemps") + " à temps"
    ));

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

        JLabel sectionTitle = new JLabel(" Planning des livraisons");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);

        //JLabel sectionSubtitle = new JLabel("Vue d'ensemble de toutes vos livraisons");
       // sectionSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
       // sectionSubtitle.setForeground(textSecondary);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(sectionTitle, BorderLayout.NORTH);
        //titleWrapper.add(sectionSubtitle, BorderLayout.CENTER);

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
         loadLivraisonDataWithAnimation();
        filterLivraisons(); 
         refreshStats();
    System.out.println("Début du chargement des données...");
    
    cardsContainer.removeAll();
    List<Livraison> livraisons = dao.getToutesLesLivraisons();
    
    System.out.println("Nombre de livraisons récupérées: " + livraisons.size());
    
    for (Livraison livraison : livraisons) {
        cardsContainer.add(creerCarteLivraisonPremium(livraison));
        cardsContainer.add(Box.createVerticalStrut(16));
    }
    
    cardsContainer.revalidate();
    cardsContainer.repaint();
    System.out.println("Rafraîchissement de l'UI terminé");
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
        String tooltipText = String.format(
        "<html><b>Client:</b> %s<br><b>Livreur:</b> %s<br><b>Statut:</b> %s<br><b>Articles:</b> %s</html>",
        livraison.getNomClient(),
        livraison.getNomLivreur(),
        livraison.getStatut(),
        livraison.getArticles()
    );
    card.setToolTipText(tooltipText);
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

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
    header.setBorder(new EmptyBorder(0, 0, 12, 0));

    JLabel idLabel = new JLabel("LIV-" + String.format("%03d", livraison.getId()));
    idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    idLabel.setForeground(textPrimary);

    JLabel statusBadge = createModernStatusBadge(livraison.getStatut());
    statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Texte plus grand

    header.add(idLabel, BorderLayout.WEST);
    header.add(statusBadge, BorderLayout.EAST);

    return header;
}
    
// Augmenter les polices et les espacements
private JPanel createCardContent(Livraison livraison) {
    JPanel content = new JPanel(new GridLayout(3, 2, 24, 16)); // Plus d'espace entre les éléments
    content.setOpaque(false);
    content.setBorder(new EmptyBorder(16, 0, 16, 0)); // Augmenter le padding

    // Garder la même structure mais avec des polices plus grandes
    content.add(createInfoRow("👤", "Client", livraison.getNomClient(), 14));
    content.add(createInfoRow("🚚", "Livreur", livraison.getNomLivreur(), 14));
    content.add(createInfoRow("📍", "Adresse", livraison.getAdresse(), 14));
    content.add(createInfoRow("📅", "Date", livraison.getDateLivraison().toString(), 14));
    content.add(createInfoRow("📋", "Priorité", getPriorityText(livraison.getStatut()), 14));
    content.add(createArticlesPanel(livraison.getArticles()));

    return content;
}

// Modifier createInfoRow pour accepter la taille de police
private JPanel createInfoRow(String icon, String label, String value, int fontSize) {
    JPanel row = new JPanel(new BorderLayout(8, 0));
    row.setOpaque(false);

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize + 2));

    JPanel textPanel = new JPanel(new BorderLayout());
    textPanel.setOpaque(false);

    JLabel labelText = new JLabel(label);
    labelText.setFont(new Font("Segoe UI", Font.PLAIN, fontSize - 2));
    labelText.setForeground(textMuted);

    JLabel valueText = new JLabel(value);
    valueText.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
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
    panel.setBorder(new EmptyBorder(8, 0, 0, 0));

    JLabel label = new JLabel("📦 Articles");
    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
    label.setForeground(textPrimary);

    JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    tagsPanel.setOpaque(false);

    String[] articleArray = articles.split(",");
    for (String article : articleArray) {
        if (article.trim().length() > 0) {
            tagsPanel.add(createArticleTag(article.trim()));
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
            // Garder le même style mais avec police plus grande
            super.paintComponent(g);
        }
    };
    tag.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Police légèrement augmentée
    tag.setBorder(new EmptyBorder(6, 12, 6, 12));
    return tag;
}

    private JPanel createCardActions(Livraison livraison) {
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    actions.setOpaque(false);
    actions.setBorder(new EmptyBorder(12, 0, 0, 0));

    JButton editBtn = createActionButton("✏️", "Modifier", accentBlue);
    editBtn.addActionListener(e -> {
        LivraisonModal editModal = new LivraisonModal(connection, this, livraison);
        editModal.setVisible(true);
    });

    JButton trackBtn = createActionButton("📍", "Suivre", successColor);
    trackBtn.addActionListener(e -> {
        // Implémentez le suivi de livraison ici
        JOptionPane.showMessageDialog(this, 
            "Suivi de la livraison " + livraison.getId() + " en cours...",
            "Suivi de livraison",
            JOptionPane.INFORMATION_MESSAGE);
    });

    JButton moreBtn = createActionButton("⋯", "Plus", textMuted);
    moreBtn.addActionListener(e -> {
        // Menu contextuel avec plus d'options
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem detailsItem = new JMenuItem("Détails complets");
        detailsItem.addActionListener(ev -> showLivraisonDetails(livraison));
        
        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Êtes-vous sûr de vouloir supprimer cette livraison?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dao.supprimerLivraison(livraison.getId());
                if (success) {
                    loadLivraisonData();
                    JOptionPane.showMessageDialog(this, "Livraison supprimée avec succès!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        popupMenu.add(detailsItem);
        popupMenu.add(deleteItem);
        popupMenu.show(moreBtn, 0, moreBtn.getHeight());
    });

    actions.add(editBtn);
    actions.add(Box.createHorizontalStrut(8));
    actions.add(trackBtn);
    actions.add(Box.createHorizontalStrut(8));
    actions.add(moreBtn);

    return actions;
}

private void showLivraisonDetails(Livraison livraison) {
    JDialog detailsDialog = new JDialog();
    detailsDialog.setTitle("Détails de la livraison LIV-" + livraison.getId());
    detailsDialog.setLayout(new BorderLayout());
    detailsDialog.setSize(500, 400);
    detailsDialog.setLocationRelativeTo(this);

    JTextArea detailsArea = new JTextArea();
    detailsArea.setEditable(false);
    detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    detailsArea.setText(
        "ID: LIV-" + livraison.getId() + "\n" +
        "Client: " + livraison.getNomClient() + "\n" +
        "Livreur: " + livraison.getNomLivreur() + "\n" +
        "Adresse: " + livraison.getAdresse() + "\n" +
        "Date: " + livraison.getDateLivraison() + "\n" +
        "Statut: " + livraison.getStatut() + "\n" +
        "Articles:\n" + livraison.getArticles().replace(",", "\n")
    );

    detailsDialog.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
    
    JButton closeButton = new JButton("Fermer");
    closeButton.addActionListener(e -> detailsDialog.dispose());
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(closeButton);
    
    detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
    detailsDialog.setVisible(true);
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
    JLabel badge = new JLabel(status.toUpperCase()) { // Texte en majuscules
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color statusColor = getStatusColor(status);
            g2d.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 30));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            g2d.dispose();
            super.paintComponent(g);
        }
    };
    
    badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
    badge.setForeground(getStatusColor(status));
    badge.setBorder(new EmptyBorder(6, 16, 6, 16)); // Plus large
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

    private void refreshStats() {
    // Solution optimisée - ne rafraîchit que la section des statistiques
    
    // 1. Trouver le conteneur parent des statistiques
    Component[] mainComponents = this.getComponents();
    for (Component mainComp : mainComponents) {
        if (mainComp instanceof JPanel) {
            JPanel mainPanel = (JPanel) mainComp;
            
            // 2. Parcourir les composants du panel principal
            Component[] contentComponents = mainPanel.getComponents();
            for (Component contentComp : contentComponents) {
                if (contentComp instanceof JPanel && "statsPanelWrapper".equals(contentComp.getName())) {
                    // 3. Trouver le panel des statistiques
                    JPanel statsWrapper = (JPanel) contentComp;
                    statsWrapper.removeAll();
                    
                    // 4. Recréer et ajouter le nouveau panel de stats
                    JPanel newStatsPanel = createEnhancedStatsPanel();
                    statsWrapper.add(newStatsPanel, BorderLayout.CENTER);
                    
                    // 5. Rafraîchir l'affichage
                    statsWrapper.revalidate();
                    statsWrapper.repaint();
                    return;
                }
            }
        }
    }
    
    // Solution de repli si la structure n'est pas trouvée
    reinitUI();
}

private void reinitUI() {
    // Solution plus radicale mais garantie
    this.removeAll();
    this.initComponents();
    this.revalidate();
    this.repaint();
}
    public List<Livraison> getLivraisonsWithCache() throws SQLException {
    Connection conn = DatabaseConnection.getConnection();
    LivraisonDAO livraisonDAO = new LivraisonDAO(conn);
    return livraisonDAO.getAllLivraisons();
}

}