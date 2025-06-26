package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.SwingConstants.LEFT;
import javax.swing.table.DefaultTableCellRenderer;
import dao.CommandeDAO;
import dao.CommandeDAO.Client;
import modele.Commande;
import View.NouvelleCommandePanel;

public class CommandeView extends JPanel {

    private final Connection dbConnection;
    private JTable commandeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton filtresBtn;
    private CommandeDAO commandeDAO;
    
    // Nouvelles variables pour la recherche dynamique
    private Timer searchTimer;
    private boolean isPlaceholderActive = true;

    // Couleurs modernes inspirées de Tailwind CSS
    private final Color primaryGreen = new Color(34, 197, 94);
    private final Color lightGray = new Color(249, 250, 251);
    private final Color darkText = new Color(31, 41, 55);
    private final Color lightText = new Color(107, 114, 128);
    private final Color borderColor = new Color(229, 231, 235);
    private final Color hoverColor = new Color(243, 244, 246);

    // Couleurs pour les statuts
    private final Color statusEnCours = new Color(219, 234, 254);
    private final Color statusEnCoursText = new Color(37, 99, 235);
    private final Color statusPret = new Color(220, 252, 231);
    private final Color statusPretText = new Color(22, 163, 74);
    private final Color statusEnAttente = new Color(254, 249, 195);
    private final Color statusEnAttenteText = new Color(161, 98, 7);

    // Couleurs pour les priorités
    private final Color prioriteNormale = new Color(243, 244, 246);
    private final Color prioriteNormaleText = new Color(75, 85, 99);
    private final Color prioriteUrgente = new Color(254, 226, 226);
    private final Color prioriteUrgenteText = new Color(220, 38, 38);

    public CommandeView(Connection connection) throws SQLException {
        this.dbConnection = connection;
        this.commandeDAO = new CommandeDAO();

        if (connection == null) {
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.WARNING, "CommandePressingView initialized with null connection.");
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(32, 32, 32, 32));

        initComponents();
        loadCommandeData();
    }

    private ImageIcon loadImageIcon(String filename, int size) {
        try {
            java.net.URL imgURL = getClass().getResource("/images/" + filename);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image img = originalIcon.getImage();
                Image resizedImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                return new ImageIcon(resizedImg);
            } else {
                Logger.getLogger(CommandeView.class.getName())
                        .log(Level.WARNING, "Image non trouvée: /images/" + filename);
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.SEVERE, "Erreur lors du chargement de l'image: " + filename, e);
            return null;
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(24, 0, 0, 0));

        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(24));

        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Gestion des Commandes");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(darkText);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Suivez et gérez toutes vos commandes");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        subtitleLabel.setForeground(lightText);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JButton nouvelleCommandeBtn = createPrimaryButton("+ Nouvelle commande");
        nouvelleCommandeBtn.addActionListener(e -> ouvrirNouvelleCommande());

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(nouvelleCommandeBtn, BorderLayout.EAST);

        return panel;
    }

    // MÉTHODE AJOUTÉE POUR OUVRIR LE PANEL NOUVELLE COMMANDE
    private void ouvrirNouvelleCommande() {
        try {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            NouvelleCommandePanel nouvelleCommandePanel = new NouvelleCommandePanel(
                parentFrame, 
                dbConnection, 
                this
            );
            nouvelleCommandePanel.setVisible(true);
        } catch (Exception e) {
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.SEVERE, "Erreur lors de l'ouverture du panel nouvelle commande", e);
            showStyledMessage(this, "❌ Erreur", 
                "Erreur lors de l'ouverture du formulaire: " + e.getMessage(), false);
        }
    }

    // MÉTHODE MODIFIÉE POUR LA RECHERCHE DYNAMIQUE
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(lightGray);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setOpaque(false);
        panel.putClientProperty("ARC", 8);

        JPanel searchInputPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        searchInputPanel.setOpaque(false);
        searchInputPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel searchIcon = new JLabel(loadImageIcon("search.png", 16));
        searchIcon.setBorder(new EmptyBorder(0, 0, 0, 8));

        searchField = new JTextField("Rechercher une commande...");
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setForeground(lightText);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        isPlaceholderActive = true;

        // Initialiser le timer de recherche avec un délai de 300ms
        searchTimer = new Timer(300, e -> performDynamicSearch());
        searchTimer.setRepeats(false);

        // Ajouter les listeners pour la recherche dynamique
        setupDynamicSearch();

        searchInputPanel.add(searchIcon, BorderLayout.WEST);
        searchInputPanel.add(searchField, BorderLayout.CENTER);

        filtresBtn = createSecondaryButton("Filtres", loadImageIcon("filtre.png", 16));
        filtresBtn.addActionListener(e -> ouvrirFiltres());

        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(lightGray);
        searchContainer.add(searchInputPanel, BorderLayout.CENTER);
        searchContainer.add(Box.createHorizontalStrut(16), BorderLayout.EAST);

        JPanel filterButtonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterButtonWrapper.setBackground(lightGray);
        filterButtonWrapper.add(filtresBtn);

        panel.add(searchContainer, BorderLayout.CENTER);
        panel.add(filterButtonWrapper, BorderLayout.EAST);

        return panel;
    }

    // NOUVELLES MÉTHODES POUR LA RECHERCHE DYNAMIQUE
    private void setupDynamicSearch() {
        // Gestion du placeholder et de la recherche
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (isPlaceholderActive) {
                    searchField.setText("");
                    searchField.setForeground(darkText);
                    isPlaceholderActive = false;
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Rechercher une commande...");
                    searchField.setForeground(lightText);
                    isPlaceholderActive = true;
                    // Réafficher toutes les commandes si le champ est vide
                    try {
                        loadCommandeData();
                    } catch (SQLException e) {
                        Logger.getLogger(CommandeView.class.getName())
                                .log(Level.SEVERE, "Erreur lors du rechargement des données", e);
                    }
                }
            }
        });

        // Ajouter un DocumentListener pour la recherche en temps réel
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleSearchChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleSearchChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleSearchChange();
            }
        });
    }

    private void handleSearchChange() {
        // Arrêter le timer précédent
        if (searchTimer.isRunning()) {
            searchTimer.stop();
        }

        // Ne pas rechercher si c'est le placeholder qui est affiché
        if (!isPlaceholderActive) {
            // Démarrer un nouveau timer pour débouncer la recherche
            searchTimer.start();
        }
    }

    private void performDynamicSearch() {
        String searchText = searchField.getText().trim();
        
        try {
            if (searchText.isEmpty() || isPlaceholderActive) {
                // Si le champ est vide, afficher toutes les commandes
                loadCommandeData();
            } else {
                // Effectuer la recherche avec le terme saisi
                searchCommandesWithFeedback(searchText);
            }
        } catch (SQLException e) {
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.SEVERE, "Erreur lors de la recherche dynamique", e);
            showStyledMessage(this, "❌ Erreur de recherche", 
                "Erreur lors de la recherche: " + e.getMessage(), false);
        }
    }

    private void searchCommandesWithFeedback(String searchTerm) throws SQLException {
        tableModel.setRowCount(0);
        
        // Ajouter un indicateur de chargement temporaire
        Object[] loadingRow = {"🔍 Recherche en cours...", "", "", "", "", "", ""};
        tableModel.addRow(loadingRow);
        
        // Effectuer la recherche dans un thread séparé pour ne pas bloquer l'UI
        SwingUtilities.invokeLater(() -> {
            try {
                // Supprimer la ligne de chargement
                tableModel.setRowCount(0);
                
                List<Commande> commandes = commandeDAO.searchCommandes(searchTerm);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                
                if (commandes.isEmpty()) {
                    // Afficher un message si aucun résultat
                    Object[] noResultRow = {
                        "❌ Aucun résultat", 
                        "Aucune commande trouvée pour \"" + searchTerm + "\"", 
                        "", "", "", "", ""
                    };
                    tableModel.addRow(noResultRow);
                    updateTableTitle(0);
                } else {
                    // Afficher les résultats
                    for (Commande commande : commandes) {
                        String dateStr = dateFormat.format(commande.getDate_reception());
                        Object[] row = {
                            commande.getId() + "\n" + dateStr,
                            commande.getClientDisplay(),
                            commande.getArticle(),
                            commande.getStatut(),
                            commande.getTotal(),
                            commande.getPriorite(),
                            "Actions"
                        };
                        tableModel.addRow(row);
                    }
                    updateTableTitle(commandes.size());
                }
                
                // Mettre en évidence le terme recherché dans les résultats
                highlightSearchResults(searchTerm);
                
            } catch (SQLException e) {
                Logger.getLogger(CommandeView.class.getName())
                        .log(Level.SEVERE, "Erreur lors de la recherche", e);
                
                tableModel.setRowCount(0);
                Object[] errorRow = {
                    "❌ Erreur", 
                    "Erreur lors de la recherche: " + e.getMessage(), 
                    "", "", "", "", ""
                };
                tableModel.addRow(errorRow);
            }
        });
    }

    private void highlightSearchResults(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return;
        }

        // Créer un renderer personnalisé pour mettre en évidence le texte
        searchField.putClientProperty("searchTerm", searchTerm.toLowerCase());
        
        // Actualiser l'affichage du tableau
        commandeTable.repaint();
    }

    // MÉTHODES PUBLIQUES POUR CONTRÔLER LA RECHERCHE
    public void clearSearch() {
        searchField.setText("Rechercher une commande...");
        searchField.setForeground(lightText);
        isPlaceholderActive = true;
        
        try {
            loadCommandeData();
        } catch (SQLException e) {
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.SEVERE, "Erreur lors du rechargement des données", e);
        }
    }

    public String getCurrentSearchTerm() {
        if (isPlaceholderActive || searchField.getText().trim().isEmpty()) {
            return "";
        }
        return searchField.getText().trim();
    }

    public void setSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            clearSearch();
        } else {
            searchField.setText(term);
            searchField.setForeground(darkText);
            isPlaceholderActive = false;
            performDynamicSearch();
        }
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel sectionTitle = new JLabel("Liste des commandes (3)");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 18));
        sectionTitle.setForeground(darkText);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        createCommandeTable();

        JPanel tableWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(commandeTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setColumnHeaderView(commandeTable.getTableHeader());

        commandeTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setBackground(lightGray);
                label.setForeground(lightText);
                label.setFont(new Font("Inter", Font.BOLD, 12));
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, (column == table.getColumnCount() - 1) ? 0 : 1,
                                borderColor),
                        new EmptyBorder(12, 16, 12, 16)));
                label.setHorizontalAlignment(LEFT);
                return label;
            }
        });

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        panel.add(sectionTitle, BorderLayout.NORTH);
        panel.add(tableWrapper, BorderLayout.CENTER);

        return panel;
    }

    // MÉTHODE MODIFIÉE POUR INCLURE LA MISE EN ÉVIDENCE DE LA RECHERCHE
    private void createCommandeTable() {
        String[] columnNames = {
                "Commande", "Client", "Articles", "Statut", "Total", "Priorite", "Actions"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        commandeTable = new JTable(tableModel);
        styleTable(commandeTable);

        for (int i = 0; i < commandeTable.getColumnCount(); i++) {
            System.out.println("Colonne[" + i + "] = " + commandeTable.getColumnName(i));
        }

        commandeTable.getColumn("Statut").setCellRenderer(new StatusRenderer());
        commandeTable.getColumn("Priorite").setCellRenderer(new PrioriteRenderer());
        commandeTable.getColumn("Actions").setCellRenderer(new ActionButtonRenderer());
        commandeTable.getColumn("Actions").setCellEditor(new ActionButtonEditor());

        commandeTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        commandeTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        commandeTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        commandeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        commandeTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        commandeTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        commandeTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        // Configurer la mise en évidence de la recherche
        setupSearchHighlighting();
    }

    private void setupSearchHighlighting() {
        SearchHighlightRenderer searchRenderer = new SearchHighlightRenderer();
        
        // Appliquer le renderer aux colonnes qui peuvent contenir du texte recherchable
        commandeTable.getColumnModel().getColumn(1).setCellRenderer(searchRenderer); // Client
        commandeTable.getColumnModel().getColumn(2).setCellRenderer(searchRenderer); // Articles
        // Garder les renderers spécialisés pour les autres colonnes
    }

    // RENDERER POUR LA MISE EN ÉVIDENCE DE LA RECHERCHE
    private class SearchHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel();
            label.setFont(new Font("Inter", Font.PLAIN, 14));
            label.setForeground(darkText);
            label.setBorder(new EmptyBorder(12, 16, 12, 16));
            label.setOpaque(true);
            label.setBackground(isSelected ? hoverColor : Color.WHITE);
            label.setVerticalAlignment(SwingConstants.CENTER);
            
            String searchTerm = (String) searchField.getClientProperty("searchTerm");
            
            if (searchTerm != null && !searchTerm.isEmpty() && value != null) {
                String cellText = value.toString();
                String lowerCellText = cellText.toLowerCase();
                
                if (lowerCellText.contains(searchTerm)) {
                    // Mettre en évidence le terme recherché
                    String highlightedText = cellText.replaceAll(
                        "(?i)" + java.util.regex.Pattern.quote(searchTerm),
                        "<mark style='background-color: #FEF08A; color: #A16207; font-weight: bold;'>$0</mark>"
                    );
                    
                    if (cellText.contains("\n")) {
                        highlightedText = highlightedText.replace("\n", "<br>");
                    }
                    
                    label.setText("<html>" + highlightedText + "</html>");
                } else {
                    if (value.toString().contains("\n")) {
                        label.setText("<html>" + value.toString().replace("\n", "<br>") + "</html>");
                    } else {
                        label.setText(value.toString());
                    }
                }
            } else {
                if (value != null && value.toString().contains("\n")) {
                    label.setText("<html>" + value.toString().replace("\n", "<br>") + "</html>");
                } else {
                    label.setText(value != null ? value.toString() : "");
                }
            }
            
            return label;
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        table.getTableHeader().setBackground(lightGray);
        table.getTableHeader().setForeground(lightText);
        table.getTableHeader().setBorder(null);

        table.setRowHeight(64);
        table.setGridColor(borderColor);
        table.setSelectionBackground(hoverColor);
        table.setSelectionForeground(darkText);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        table.setFocusable(false);
        table.setRowSelectionAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setFont(new Font("Inter", Font.PLAIN, 14));
                label.setForeground(darkText);
                label.setBorder(new EmptyBorder(12, 16, 12, 16));
                label.setOpaque(true);
                label.setBackground(isSelected ? hoverColor : Color.WHITE);
                label.setVerticalAlignment(SwingConstants.CENTER);
                if (value != null && value.toString().contains("\n")) {
                    label.setText("<html>" + value.toString().replace("\n", "<br>") + "</html>");
                } else {
                    label.setText(value != null ? value.toString() : "");
                }
                return label;
            }
        });
    }

    public void loadCommandeData() throws SQLException {
        tableModel.setRowCount(0);
        List<Commande> commandes = commandeDAO.getAllCommandes();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Commande commande : commandes) {
            String dateStr = dateFormat.format(commande.getDate_reception());
            Object[] row = {
                    commande.getId() + "\n" + dateStr,
                    commande.getClientDisplay(),
                    commande.getArticle(),
                    commande.getStatut(),
                    commande.getTotal(),
                    commande.getPriorite(),
                    "Actions"
            };
            tableModel.addRow(row);
        }
        updateTableTitle(commandes.size());
    }

    private void updateTableTitle(int count) {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] panelComponents = panel.getComponents();
                for (Component panelComp : panelComponents) {
                    if (panelComp instanceof JPanel) {
                        JPanel innerPanel = (JPanel) panelComp;
                        Component[] innerComponents = innerPanel.getComponents();
                        for (Component innerComp : innerComponents) {
                            if (innerComp instanceof JPanel) {
                                JPanel tablePanel = (JPanel) innerComp;
                                if (tablePanel.getLayout() instanceof BorderLayout) {
                                    Component northComp = ((BorderLayout) tablePanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                                    if (northComp instanceof JLabel) {
                                        ((JLabel) northComp).setText("Liste des commandes (" + count + ")");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor, Dimension size, int arc,
            ImageIcon icon) {
        JButton button = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = bgColor;
                if (getModel().isPressed()) {
                    currentBg = bgColor.darker();
                } else if (getModel().isRollover()) {
                    if (bgColor.equals(primaryGreen)) {
                        currentBg = new Color(0, 100, 0);
                    } else if (bgColor.equals(Color.WHITE)) {
                        currentBg = hoverColor;
                    }
                }
                g2d.setColor(currentBg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (bgColor.equals(primaryGreen)) {
                    if (getModel().isRollover() || getModel().isPressed()) {
                        g2d.setColor(new Color(0, 100, 0));
                    } else {
                        g2d.setColor(new Color(59, 130, 246, 128));
                    }
                } else {
                    g2d.setColor(borderColor);
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2d.dispose();
            }
        };

        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setPreferredSize(size);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createPrimaryButton(String text) {
        return createStyledButton(text, primaryGreen, Color.WHITE, new Dimension(200, 10), 8, null);
    }

    private JButton createSecondaryButton(String text, ImageIcon icon) {
        return createStyledButton(text, Color.WHITE, lightText, new Dimension(100, 40), 6, icon);
    }

    class StatusRenderer extends JPanel implements TableCellRenderer {
        private JLabel label;
        private int arc = 12;
        private Color statusAnnuleText;

        public StatusRenderer() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            label = new JLabel();
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setBorder(new EmptyBorder(4, 12, 4, 12));
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bgColor = Color.WHITE;
            String status = label.getText();
            switch (status) {
                case "en cours":
                    bgColor = statusEnCours;
                    break;
                case "prêt":
                    bgColor = statusPret;
                    break;
                case "en attente":
                    bgColor = statusEnAttente;
                    break;
                case "inconnu":
                    bgColor = Color.GRAY;
                    break;
            }


            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, (getHeight() - label.getPreferredSize().height) / 2 - label.getInsets().top,
                    getWidth(), label.getPreferredSize().height + label.getInsets().top + label.getInsets().bottom,
                    arc, arc);
            g2d.dispose();
            super.paintComponent(g);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            String status = (value != null) ? value.toString() : "inconnu"; // protection null

            switch (status) {
                case "en cours":
                    label.setForeground(statusEnCoursText);
                    label.setText("en cours");
                    break;
                case "terminé":
                    label.setForeground(statusPretText);
                    label.setText("terminé");
                    break;
                case "en attente":
                    label.setForeground(statusEnAttenteText);
                    label.setText("en attente");
                    break;
                case "annulé":
                    label.setForeground(statusAnnuleText);
                    label.setText("annulé");
                    break;
                default:
                    label.setForeground(darkText);
                    label.setText(status); // ça peut être "inconnu"
            }

            setBackground(isSelected ? hoverColor : Color.WHITE);
            return this;
        }

    }

    class PrioriteRenderer extends JPanel implements TableCellRenderer {
        private JLabel label;
        private int arc = 12;

        public PrioriteRenderer() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            label = new JLabel();
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setBorder(new EmptyBorder(4, 12, 4, 12));
            add(label);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bgColor = Color.WHITE;
            String priorite = label.getText();
            switch (priorite) {
                case "normale":
                    bgColor = prioriteNormale;
                    break;
                case "urgente":
                    bgColor = prioriteUrgente;
                    break;
            }

            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, (getHeight() - label.getPreferredSize().height) / 2 - label.getInsets().top,
                    getWidth(), label.getPreferredSize().height + label.getInsets().top + label.getInsets().bottom,
                    arc, arc);
            g2d.dispose();
            super.paintComponent(g);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String priorite = String.valueOf(value).toLowerCase();

            switch (priorite) {
                case "normale":
                    label.setForeground(prioriteNormaleText);
                    label.setText("normale");
                    break;
                case "urgente":
                    label.setForeground(prioriteUrgenteText);
                    label.setText("urgente");
                    break;
                default:
                    label.setForeground(darkText);
                    label.setText(priorite);
            }

            setBackground(isSelected ? hoverColor : Color.WHITE);
            return this;
        }
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewBtn, editBtn, deleteBtn;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 8));
            setOpaque(true);
            setBackground(Color.WHITE);

            viewBtn = createSmallActionButton(loadImageIcon("eye.png", 18));
            editBtn = createSmallActionButton(loadImageIcon("modifier_icon.png", 18));
            deleteBtn = createSmallActionButton(loadImageIcon("supprimer_icon.png", 18));

            add(viewBtn);
            add(editBtn);
            add(deleteBtn);
        }

        private JButton createSmallActionButton(ImageIcon icon) {
            JButton btn = new JButton(icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color currentBg = hoverColor;
                    if (getModel().isPressed()) {
                        currentBg = borderColor;
                    } else if (getModel().isRollover()) {
                        currentBg = new Color(230, 230, 230);
                    }
                    g2d.setColor(currentBg);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2d.dispose();
                    super.paintComponent(g);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(borderColor);
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    g2d.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setForeground(darkText);
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? hoverColor : Color.WHITE);
            return this;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn, editBtn, deleteBtn;
        private int currentRow;

        public ActionButtonEditor() {
            super(new JCheckBox());

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);

            viewBtn = createSmallActionButton(loadImageIcon("eye.png", 18));
            editBtn = createSmallActionButton(loadImageIcon("modifier_icon.png", 18));
            deleteBtn = createSmallActionButton(loadImageIcon("supprimer_icon.png", 18));

            viewBtn.addActionListener(e -> {
                viewCommande(currentRow);
                fireEditingStopped();
            });

            editBtn.addActionListener(e -> {
                editCommande(currentRow);
                fireEditingStopped();
            });
            deleteBtn.addActionListener(e -> {
                deleteCommande(currentRow);
                // ✅ Appelé *après* que Swing ait terminé le traitement de l’événement courant
                SwingUtilities.invokeLater(() -> {
                    fireEditingStopped();
                });
            });


            

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }


        private JButton createSmallActionButton(ImageIcon icon) {
            JButton btn = new JButton(icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color currentBg = hoverColor;
                    if (getModel().isPressed()) {
                        currentBg = borderColor;
                    } else if (getModel().isRollover()) {
                        currentBg = new Color(230, 230, 230);
                    }
                    g2d.setColor(currentBg);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2d.dispose();
                    super.paintComponent(g);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(borderColor);
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    g2d.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(true);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setForeground(darkText);
            return btn;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(isSelected ? hoverColor : Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    // MÉTHODES AMÉLIORÉES POUR LES ACTIONS

    private void ouvrirFiltres() {
        JOptionPane.showMessageDialog(this,
                "Ouverture du panneau de filtres...",
                "Filtres",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Méthode améliorée pour afficher les détails de la commande - CENTRAGE ET DIMENSIONS FIXÉS
    private void viewCommande(int row) {
        try {
            String commandeInfo = tableModel.getValueAt(row, 0).toString();
            String commandeId = commandeInfo.split("\n")[0];
            
            Commande commande = commandeDAO.getCommandeById(Integer.parseInt(commandeId));
            
            if (commande != null) {
                // Créer une fenêtre modale avec un design moderne
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Détails de la commande", true);
                dialog.setUndecorated(true);
                dialog.setBackground(new Color(0, 0, 0, 0));
                
                // Panel principal avec ombre et coins arrondis
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Ombre portée plus prononcée
                        g2d.setColor(new Color(0, 0, 0, 25));
                        for (int i = 0; i < 12; i++) {
                            g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 24 - i, 24 - i);
                        }
                        
                        // Fond principal
                        g2d.setColor(Color.WHITE);
                        g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 20, 20);
                        
                        // Bordure subtile
                        g2d.setColor(new Color(229, 231, 235));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRoundRect(12, 12, getWidth() - 25, getHeight() - 25, 20, 20);
                        
                        g2d.dispose();
                    }
                };
                mainPanel.setOpaque(false);
                mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
                
                // Panel de contenu avec padding optimisé
                JPanel contentPanel = new JPanel(new BorderLayout());
                contentPanel.setBackground(Color.WHITE);
                contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
                contentPanel.setOpaque(false);
                
                // En-tête élégant avec dégradé - hauteur réduite
                JPanel headerPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Dégradé de fond plus subtil
                        GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(99, 102, 241, 8),
                            0, getHeight(), new Color(139, 92, 246, 3)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                        
                        // Bordure gauche colorée plus fine
                        g2d.setColor(primaryGreen);
                        g2d.fillRoundRect(0, 0, 4, getHeight(), 16, 16);
                        
                        g2d.dispose();
                    }
                };
                headerPanel.setOpaque(false);
                headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                // Titre avec icône - taille optimisée
                JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                titlePanel.setOpaque(false);
                
                JLabel iconLabel = new JLabel("📋");
                iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
                
                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setOpaque(false);
                
                JLabel titleLabel = new JLabel("Commande #" + commandeId);
                titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
                titleLabel.setForeground(new Color(31, 41, 55));
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel subtitleLabel = new JLabel("Détails complets de la commande");
                subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 13));
                subtitleLabel.setForeground(new Color(107, 114, 128));
                subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
                
                textPanel.add(titleLabel);
                textPanel.add(subtitleLabel);
                
                titlePanel.add(iconLabel);
                titlePanel.add(textPanel);
                
                // Badge de statut amélioré
                JPanel statusBadge = createEnhancedStatusBadge(commande.getStatut());
                
                headerPanel.add(titlePanel, BorderLayout.WEST);
                headerPanel.add(statusBadge, BorderLayout.EAST);
                
                // Corps principal avec scroll personnalisé - espacement optimisé
                JPanel bodyPanel = new JPanel();
                bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
                bodyPanel.setBackground(Color.WHITE);
                bodyPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
                
                // Section Client avec design carte - hauteur réduite
                JPanel clientCard = createDetailedInfoCard("👤 Informations Client", 
                    createClientInfoContent(commande), primaryGreen);
                bodyPanel.add(clientCard);
                bodyPanel.add(Box.createVerticalStrut(15));
                
                // Grille pour les informations principales - espacement réduit
                JPanel infoGrid = new JPanel(new GridLayout(2, 2, 15, 15));
                infoGrid.setBackground(Color.WHITE);
                
                infoGrid.add(createCompactInfoCard("📦 Article", commande.getArticle(), new Color(59, 130, 246)));
                infoGrid.add(createCompactInfoCard("💰 Montant", String.format("%.2f Fcfa", commande.getTotal()), new Color(34, 197, 94)));
                infoGrid.add(createCompactInfoCard("⚡ Priorité", commande.getPriorite(), 
                    "urgente".equals(commande.getPriorite()) ? new Color(220, 38, 38) : new Color(107, 114, 128)));
                infoGrid.add(createCompactInfoCard("📅 Réception", 
                    new SimpleDateFormat("dd/MM/yyyy").format(commande.getDate_reception()), new Color(139, 92, 246)));
                
                bodyPanel.add(infoGrid);
                bodyPanel.add(Box.createVerticalStrut(15));
                
                // Section dates avec timeline - hauteur réduite
                if (commande.getDate_livraison() != null) {
                    JPanel timelinePanel = createCompactTimelinePanel(commande);
                    bodyPanel.add(timelinePanel);
                    bodyPanel.add(Box.createVerticalStrut(10));
                }
                
                // Scroll pane avec scrollbar personnalisée - taille optimisée
                JScrollPane scrollPane = new JScrollPane(bodyPanel);
                scrollPane.setBorder(null);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
                scrollPane.setPreferredSize(new Dimension(450, 280)); // Taille augmentée pour afficher tout le contenu
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                
                // Panel de boutons avec animations - espacement réduit
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
                buttonPanel.setOpaque(false);
                
                JButton editButton = createModernButton("✏️ Modifier", primaryGreen, Color.WHITE);
                editButton.addActionListener(e -> {
                    dialog.dispose();
                    editCommande(row);
                });
                
                JButton closeButton = createModernButton("Fermer", new Color(107, 114, 128), Color.WHITE);
                closeButton.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(editButton);
                buttonPanel.add(closeButton);
                
                // Assemblage final
                contentPanel.add(headerPanel, BorderLayout.NORTH);
                contentPanel.add(scrollPane, BorderLayout.CENTER);
                contentPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                mainPanel.add(contentPanel, BorderLayout.CENTER);
                
                dialog.add(mainPanel);
                dialog.setSize(500, 650); // Dimensions augmentées pour un meilleur affichage
                
                // CENTRAGE FORCÉ AU MILIEU DE L'ÉCRAN
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width - dialog.getWidth()) / 2;
                int y = (screenSize.height - dialog.getHeight()) / 2;
                dialog.setLocation(x, y);
                
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            showStyledMessage(this, "❌ Erreur", 
                "Erreur lors de l'affichage des détails: " + e.getMessage(), false);
        }
    }

    // Méthode améliorée pour modifier une commande - CENTRAGE ET DIMENSIONS FIXÉS
    private void editCommande(int row) {
        try {
            String commandeInfo = tableModel.getValueAt(row, 0).toString();
            String commandeId = commandeInfo.split("\n")[0];
            
            Commande commande = commandeDAO.getCommandeById(Integer.parseInt(commandeId));
            
            if (commande != null) {
                List<Client> clients = commandeDAO.getAllClients();
                
                // Créer une fenêtre modale moderne avec dimensions optimisées
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier la commande", true);
                dialog.setUndecorated(true);
                dialog.setBackground(new Color(0, 0, 0, 0));
                
                // Panel principal avec ombre plus prononcée
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Ombre portée améliorée
                        g2d.setColor(new Color(0, 0, 0, 35));
                        for (int i = 0; i < 15; i++) {
                            g2d.fillRoundRect(i, i, getWidth() - 2 * i, getHeight() - 2 * i, 28 - i, 28 - i);
                        }
                        
                        // Fond principal avec légère teinte
                        g2d.setColor(new Color(255, 255, 255));
                        g2d.fillRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 24, 24);
                        
                        // Bordure subtile
                        g2d.setColor(new Color(220, 222, 228));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRoundRect(15, 15, getWidth() - 31, getHeight() - 31, 24, 24);
                        
                        g2d.dispose();
                    }
                };
                mainPanel.setOpaque(false);
                mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                
                // Panel de contenu principal
                JPanel contentPanel = new JPanel(new BorderLayout());
                contentPanel.setBackground(Color.WHITE);
                contentPanel.setBorder(new EmptyBorder(30, 35, 25, 35));
                contentPanel.setOpaque(false);
                
                // En-tête redessiné avec gradient horizontal
                JPanel headerPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Gradient horizontal moderne
                        GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(59, 130, 246, 12),
                            getWidth(), 0, new Color(139, 92, 246, 8)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                        
                        // Bordure gauche accentuée
                        GradientPaint accentGradient = new GradientPaint(
                            0, 0, new Color(59, 130, 246),
                            0, getHeight(), new Color(139, 92, 246)
                        );
                        g2d.setPaint(accentGradient);
                        g2d.fillRoundRect(0, 0, 5, getHeight(), 18, 18);
                        
                        g2d.dispose();
                    }
                };
                headerPanel.setOpaque(false);
                headerPanel.setBorder(new EmptyBorder(22, 25, 22, 25));
                
                // Section titre réorganisée
                JPanel titleSection = new JPanel(new BorderLayout());
                titleSection.setOpaque(false);
                
                JPanel leftTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                leftTitlePanel.setOpaque(false);
                
                JLabel iconLabel = new JLabel("✏️");
                iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                iconLabel.setBorder(new EmptyBorder(0, 0, 0, 15));
                
                JPanel textInfoPanel = new JPanel();
                textInfoPanel.setLayout(new BoxLayout(textInfoPanel, BoxLayout.Y_AXIS));
                textInfoPanel.setOpaque(false);
                
                JLabel titleLabel = new JLabel("Modifier la commande");
                titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
                titleLabel.setForeground(new Color(31, 41, 55));
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel commandeIdLabel = new JLabel("Commande #" + commandeId);
                commandeIdLabel.setFont(new Font("Inter", Font.BOLD, 16));
                commandeIdLabel.setForeground(new Color(59, 130, 246));
                commandeIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                commandeIdLabel.setBorder(new EmptyBorder(2, 0, 0, 0));
                
                JLabel subtitleLabel = new JLabel("Mettez à jour les informations");
                subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 13));
                subtitleLabel.setForeground(new Color(107, 114, 128));
                subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
                
                textInfoPanel.add(titleLabel);
                textInfoPanel.add(commandeIdLabel);
                textInfoPanel.add(subtitleLabel);
                
                leftTitlePanel.add(iconLabel);
                leftTitlePanel.add(textInfoPanel);
                
                titleSection.add(leftTitlePanel, BorderLayout.WEST);
                headerPanel.add(titleSection, BorderLayout.CENTER);
                
                // Formulaire repensé avec une meilleure organisation
                JPanel formContainer = new JPanel(new BorderLayout());
                formContainer.setBackground(Color.WHITE);
                formContainer.setBorder(new EmptyBorder(20, 0, 0, 0));
                
                JPanel formPanel = new JPanel();
                formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
                formPanel.setBackground(Color.WHITE);
                
                // Création des composants avec un style unifié
                JComboBox<Client> clientCombo = createEnhancedComboBox();
                for (Client client : clients) {
                    clientCombo.addItem(client);
                    if (client.getId() == commande.getClient_id()) {
                        clientCombo.setSelectedItem(client);
                    }
                }
                
                JTextField articleField = createEnhancedTextField(commande.getArticle(), "Ex: Chemise blanche taille M");
                JTextField totalField = createEnhancedTextField(String.valueOf(commande.getTotal()), "0.00");
                totalField.setFont(new Font("Inter", Font.BOLD, 15));
                totalField.setForeground(new Color(34, 197, 94));
                
                String[] statutOptions = {"en cours", "terminé", "en attente", "annulé"};
                JComboBox<String> statutCombo = createEnhancedComboBox();
                for (String option : statutOptions) {
                    statutCombo.addItem(option);
                    if (option.equals(commande.getStatut().trim())) {
                        statutCombo.setSelectedItem(option);
                    }
                }
                
                String[] prioriteOptions = {"normale", "urgente"};
                JComboBox<String> prioriteCombo = createEnhancedComboBox();
                for (String option : prioriteOptions) {
                    prioriteCombo.addItem(option);
                    if (option.equals(commande.getPriorite().trim())) {
                        prioriteCombo.setSelectedItem(option);
                    }
                }
                
                // Section 1: Informations principales
                JPanel mainInfoSection = createEnhancedFormGroup("Informations principales");
                mainInfoSection.add(createFieldRow("👤 Client", "Sélectionnez le client de la commande", clientCombo));
                mainInfoSection.add(Box.createVerticalStrut(15));
                mainInfoSection.add(createFieldRow("📦 Article", "Description détaillée de l'article", articleField));
                formPanel.add(mainInfoSection);
                formPanel.add(Box.createVerticalStrut(20));
                
                // Section 2: État et priorité (en ligne)
                JPanel statusSection = createEnhancedFormGroup("État de la commande");
                JPanel statusRow = new JPanel(new GridLayout(1, 2, 20, 0));
                statusRow.setBackground(Color.WHITE);
                statusRow.add(createFieldRow("📊 Statut", "État actuel", statutCombo));
                statusRow.add(createFieldRow("⚡ Priorité", "Niveau d'urgence", prioriteCombo));
                statusSection.add(statusRow);
                formPanel.add(statusSection);
                formPanel.add(Box.createVerticalStrut(20));
                
                // Section 3: Montant (mise en évidence)
                JPanel totalSection = createHighlightedFormGroup("💰 Montant de la commande");
                totalSection.add(createFieldRow("Total (€)", "Montant final à facturer", totalField));
                formPanel.add(totalSection);
                formPanel.add(Box.createVerticalStrut(15)); // Espacement supplémentaire en bas
                
                // Scroll pane avec design amélioré - DIMENSIONS AUGMENTÉES
                JScrollPane formScrollPane = new JScrollPane(formPanel);
                formScrollPane.setBorder(null);
                formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
                formScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
                formScrollPane.setOpaque(false);
                formScrollPane.getViewport().setOpaque(false);
                formScrollPane.setPreferredSize(new Dimension(580, 300)); // Taille augmentée pour afficher tout le contenu
                
                formContainer.add(formScrollPane, BorderLayout.CENTER);
                
                // Panel de boutons avec design amélioré
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
                buttonPanel.setOpaque(false);
                
                JButton cancelButton = createEnhancedButton("Annuler", new Color(107, 114, 128), Color.WHITE, 130, 42);
                cancelButton.addActionListener(e -> dialog.dispose());
                
                JButton saveButton = createEnhancedButton("💾 Sauvegarder", primaryGreen, Color.WHITE, 160, 42);
                saveButton.addActionListener(e -> {
                    try {
                        Client selectedClient = (Client) clientCombo.getSelectedItem();
                        String articleText = articleField.getText().trim();
                        String statutText = ((String) statutCombo.getSelectedItem()).trim();
                        String prioriteText = ((String) prioriteCombo.getSelectedItem()).trim();
                        String totalText = totalField.getText().trim();
                        
                        if (selectedClient == null || articleText.isEmpty() || totalText.isEmpty()) {
                            showStyledMessage(dialog, "⚠️ Validation", 
                                "Tous les champs doivent être remplis.", false);
                            return;
                        }
                        
                        commande.setClient_id(selectedClient.getId());
                        commande.setArticle(articleText);
                        commande.setStatut(statutText);
                        commande.setPriorite(prioriteText);
                        commande.setTotal(Double.parseDouble(totalText));
                        
                        boolean success = commandeDAO.updateCommande(commande);
                        
                        if (success) {
                            dialog.dispose();
                            loadCommandeData();
                            showStyledMessage(this, "✅ Succès", 
                                "Commande modifiée avec succès!", true);
                        } else {
                            showStyledMessage(dialog, "❌ Erreur", 
                                "Erreur lors de la modification de la commande.", false);
                        }
                        
                    } catch (NumberFormatException nfe) {
                        showStyledMessage(dialog, "❌ Erreur de format", 
                            "Veuillez saisir une valeur numérique valide pour le Total.", false);
                    } catch (SQLException sqle) {
                        showStyledMessage(dialog, "❌ Erreur", 
                            "Erreur de base de données: " + sqle.getMessage(), false);
                    }
                });
                
                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);
                
                // Assemblage final avec proportions optimisées
                contentPanel.add(headerPanel, BorderLayout.NORTH);
                contentPanel.add(formContainer, BorderLayout.CENTER);
                contentPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                mainPanel.add(contentPanel, BorderLayout.CENTER);
                
                dialog.add(mainPanel);
                dialog.setSize(500, 650); // Dimensions augmentées pour un meilleur affichage de tout le contenu
                
                // CENTRAGE FORCÉ AU MILIEU DE L'ÉCRAN
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width - dialog.getWidth()) / 2;
                int y = (screenSize.height - dialog.getHeight()) / 2;
                dialog.setLocation(x, y);
                
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            showStyledMessage(this, "❌ Erreur", 
                "Erreur lors de la modification: " + e.getMessage(), false);
        }
    }

    private void deleteCommande(int row) {
        try {
            String commandeInfo = tableModel.getValueAt(row, 0).toString();
            String commandeId = commandeInfo.split("\n")[0];
            
            // Créer une fenêtre de confirmation personnalisée - CENTRAGE FIXÉ
            JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirmer la suppression", true);
            confirmDialog.setLayout(new BorderLayout());
            confirmDialog.setBackground(Color.WHITE);
            
            // Panneau principal
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
            
            // En-tête avec icône d'avertissement
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            JLabel warningIcon = new JLabel("⚠️");
            warningIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            headerPanel.add(warningIcon);
            
            // Message de confirmation
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Suppression définitive");
            titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
            titleLabel.setForeground(new Color(220, 38, 38));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "Êtes-vous sûr de vouloir supprimer définitivement<br>" +
                "la <strong>commande #" + commandeId + "</strong> ?<br><br>" +
                "<span style='color: #6B7280;'>Cette action ne peut pas être annulée.</span>" +
                "</div></html>");
            messageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
            messageLabel.setForeground(darkText);
            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messageLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            
            messagePanel.add(titleLabel);
            messagePanel.add(messageLabel);
            
            // Panneau des boutons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
            
            JButton cancelButton = createStyledButton("Annuler", Color.WHITE, lightText, 
                new Dimension(100, 40), 8, null);
            cancelButton.addActionListener(e -> confirmDialog.dispose());
            
            JButton deleteButton = createStyledButton("🗑️ Supprimer", new Color(220, 38, 38), Color.WHITE, 
                new Dimension(130, 40), 8, null);
            deleteButton.addActionListener(e -> {
                confirmDialog.dispose();
                performDelete(commandeId);
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(deleteButton);
            
            // Assemblage
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(messagePanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            confirmDialog.add(mainPanel);
            confirmDialog.setSize(450, 320); // Dimensions ajustées
            
            // CENTRAGE FORCÉ AU MILIEU DE L'ÉCRAN
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - confirmDialog.getWidth()) / 2;
            int y = (screenSize.height - confirmDialog.getHeight()) / 2;
            confirmDialog.setLocation(x, y);
            
            confirmDialog.setResizable(false);
            confirmDialog.setVisible(true);
            
        } catch (Exception e) {
            showStyledMessage(this, "❌ Erreur", 
                "Erreur lors de la suppression: " + e.getMessage(), false);
            Logger.getLogger(CommandeView.class.getName())
                    .log(Level.SEVERE, "Erreur lors de la suppression de la commande", e);
        }
    }
    
    private void performDelete(String commandeId) {
        try {
            boolean success = commandeDAO.deleteCommande(Integer.parseInt(commandeId));
            
            if (success) {
                loadCommandeData();
                showStyledMessage(this, "✅ Suppression réussie", 
                    "Commande #" + commandeId + " supprimée avec succès!", true);
            } else {
                showStyledMessage(this, "❌ Erreur de suppression", 
                    "Erreur lors de la suppression de la commande.\n" +
                    "Vérifiez qu'elle n'est pas liée à d'autres données.", false);
            }
        } catch (Exception e) {
            showStyledMessage(this, "❌ Erreur", 
                "Erreur lors de la suppression: " + e.getMessage(), false);
        }
    }

    // Méthode pour la recherche avec affichage des noms clients
    private void searchCommandes(String searchTerm) throws SQLException {
        tableModel.setRowCount(0);
        List<Commande> commandes = commandeDAO.searchCommandes(searchTerm);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Commande commande : commandes) {
            String dateStr = dateFormat.format(commande.getDate_reception());
            Object[] row = {
                commande.getId() + "\n" + dateStr,
                commande.getClientDisplay(),
                commande.getArticle(),
                commande.getStatut(),
                commande.getTotal(),
                commande.getPriorite(),
                "Actions"
            };
            tableModel.addRow(row);
        }
        updateTableTitle(commandes.size());
    }

    // MÉTHODES UTILITAIRES POUR LE DESIGN MODERNE

    private JPanel createInfoCard(String title, String content, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec bordure
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure colorée
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                
                // Accent en haut
                g2d.fillRoundRect(0, 0, getWidth(), 4, 12, 12);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 13));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel contentLabel = new JLabel("<html><div style='line-height: 1.4;'>" + content + "</div></html>");
        contentLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        contentLabel.setForeground(new Color(31, 41, 55));
        contentLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(contentLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createInfoCard(String title, Object content, Color accentColor) {
        return createInfoCard(title, content.toString(), accentColor);
    }

    private String createClientInfoContent(Commande commande) {
        StringBuilder sb = new StringBuilder();
        sb.append("<strong>").append(commande.getClientNomComplet()).append("</strong><br>");
        sb.append("📧 ").append(commande.getClientEmail()).append("<br>");
        sb.append("📞 ").append(commande.getClientTelephone()).append("<br>");
        sb.append("🏠 ").append(commande.getClientAdresse());
        return sb.toString();
    }

    private JPanel createEnhancedStatusBadge(String status) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor, borderColor;
                switch (status.toLowerCase()) {
                    case "en cours":
                        bgColor = new Color(219, 234, 254);
                        borderColor = new Color(37, 99, 235);
                        break;
                    case "terminé":
                        bgColor = new Color(220, 252, 231);
                        borderColor = new Color(22, 163, 74);
                        break;
                    case "en attente":
                        bgColor = new Color(254, 249, 195);
                        borderColor = new Color(161, 98, 7);
                        break;
                    default:
                        bgColor = new Color(243, 244, 246);
                        borderColor = new Color(107, 114, 128);
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                g2d.dispose();
            }
        };
        badge.setOpaque(false);
        
        String emoji = status.toLowerCase().equals("terminé") ? "✅" : 
                      status.toLowerCase().equals("en cours") ? "⏳" : "⏸️";
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Inter", Font.BOLD, 13));
        Color textColor = status.toLowerCase().equals("terminé") ? new Color(22, 163, 74) :
                         status.toLowerCase().equals("en cours") ? new Color(37, 99, 235) :
                         new Color(161, 98, 7);
        statusLabel.setForeground(textColor);
        
        badge.add(emojiLabel);
        badge.add(statusLabel);
        
        return badge;
    }

    private JPanel createTimelinePanel(Commande commande) {
        JPanel timeline = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(249, 250, 251),
                    0, getHeight(), new Color(243, 244, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Ligne de timeline
                g2d.setColor(primaryGreen);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(50, 30, 50, getHeight() - 30);
                
                // Points de timeline
                g2d.setColor(primaryGreen);
                g2d.fillOval(42, 35, 16, 16);
                g2d.fillOval(42, getHeight() - 51, 16, 16);
                
                g2d.setColor(Color.WHITE);
                g2d.fillOval(45, 38, 10, 10);
                g2d.fillOval(45, getHeight() - 48, 10, 10);
                
                g2d.dispose();
            }
        };
        timeline.setLayout(null);
        timeline.setPreferredSize(new Dimension(0, 120));
        timeline.setOpaque(false);
        
        JLabel receptionLabel = new JLabel("📅 Réception: " + 
            new SimpleDateFormat("dd/MM/yyyy").format(commande.getDate_reception()));
        receptionLabel.setFont(new Font("Inter", Font.BOLD, 14));
        receptionLabel.setForeground(new Color(31, 41, 55));
        receptionLabel.setBounds(80, 25, 300, 25);
        
        JLabel livraisonLabel = new JLabel("🚚 Livraison: " + 
            new SimpleDateFormat("dd/MM/yyyy").format(commande.getDate_livraison()));
        livraisonLabel.setFont(new Font("Inter", Font.BOLD, 14));
        livraisonLabel.setForeground(new Color(31, 41, 55));
        livraisonLabel.setBounds(80, 75, 300, 25);
        
        timeline.add(receptionLabel);
        timeline.add(livraisonLabel);
        
        return timeline;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentBg = bgColor;
                if (getModel().isPressed()) {
                    currentBg = bgColor.darker();
                } else if (getModel().isRollover()) {
                    currentBg = new Color(
                        Math.min(255, bgColor.getRed() + 20),
                        Math.min(255, bgColor.getGreen() + 20),
                        Math.min(255, bgColor.getBlue() + 20)
                    );
                }
                
                g2d.setColor(currentBg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Ombre interne
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(140, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private JTextField createModernTextField(String text, String placeholder) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordure
                if (hasFocus()) {
                    g2d.setColor(primaryGreen);
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setPreferredSize(new Dimension(0, 45));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setOpaque(false);
        
        return field;
    }

    private <T> JComboBox<T> createModernComboBox() {
        JComboBox<T> combo = new JComboBox<T>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordure
                if (hasFocus()) {
                    g2d.setColor(primaryGreen);
                    g2d.setStroke(new BasicStroke(2));
                } else {
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        combo.setFont(new Font("Inter", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(0, 45));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        combo.setOpaque(false);
        
        return combo;
    }

    private JPanel createModernFormSection(String title, String description, JComponent component) {
        JPanel section = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec légère ombre
                g2d.setColor(new Color(249, 250, 251));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure
                g2d.setColor(new Color(229, 231, 235));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
            }
        };
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(new EmptyBorder(20, 24, 20, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 15));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(2, 0, 12, 0));
        
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(titleLabel);
        section.add(descLabel);
        section.add(component);
        
        return section;
    }

    private void showStyledMessage(Component parent, String title, String message, boolean isSuccess) {
        // Créer une fenêtre de message personnalisée - CENTRAGE FIXÉ
        JDialog messageDialog = new JDialog();
        messageDialog.setModal(true);
        messageDialog.setUndecorated(true);
        messageDialog.setBackground(Color.WHITE);
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isSuccess ? primaryGreen : new Color(220, 38, 38), 2),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(darkText);
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + 
                                       message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        messageLabel.setForeground(lightText);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton okButton = createStyledButton("OK", 
            isSuccess ? primaryGreen : new Color(220, 38, 38), 
            Color.WHITE, new Dimension(80, 35), 6, null);
        okButton.addActionListener(e -> messageDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        buttonPanel.add(okButton);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(messageLabel);
        
        messagePanel.add(contentPanel, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        messageDialog.add(messagePanel);
        messageDialog.pack();
        
        // CENTRAGE FORCÉ AU MILIEU DE L'ÉCRAN
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - messageDialog.getWidth()) / 2;
        int y = (screenSize.height - messageDialog.getHeight()) / 2;
        messageDialog.setLocation(x, y);
        
        messageDialog.setVisible(true);
    }

    // ScrollBar UI moderne
    class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = new Color(248, 250, 252);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }
        
        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(thumbColor);
            g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            
            g2d.dispose();
        }
    }

    // NOUVELLES MÉTHODES UTILITAIRES POUR LES DIMENSIONS OPTIMISÉES

    private JPanel createDetailedInfoCard(String title, String content, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec bordure
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                
                // Bordure colorée plus épaisse
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                
                // Accent en haut plus prononcé
                g2d.fillRoundRect(0, 0, getWidth(), 5, 14, 14);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        titleLabel.setForeground(new Color(75, 85, 99));
        
        JLabel contentLabel = new JLabel("<html><div style='line-height: 1.5;'>" + content + "</div></html>");
        contentLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        contentLabel.setForeground(new Color(31, 41, 55));
        contentLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(contentLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCompactInfoCard(String title, String content, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure colorée
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                
                // Accent en haut
                g2d.fillRoundRect(0, 0, getWidth(), 4, 12, 12);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 18, 15, 18));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 12));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        contentLabel.setForeground(new Color(31, 41, 55));
        contentLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(contentLabel);
        
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCompactTimelinePanel(Commande commande) {
        JPanel timeline = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(249, 250, 251),
                    0, getHeight(), new Color(243, 244, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Ligne de timeline
                g2d.setColor(primaryGreen);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(40, 20, 40, getHeight() - 20);
                
                // Points de timeline
                g2d.setColor(primaryGreen);
                g2d.fillOval(33, 22, 14, 14);
                g2d.fillOval(33, getHeight() - 36, 14, 14);
                
                g2d.setColor(Color.WHITE);
                g2d.fillOval(36, 25, 8, 8);
                g2d.fillOval(36, getHeight() - 33, 8, 8);
                
                g2d.dispose();
            }
        };
        timeline.setLayout(null);
        timeline.setPreferredSize(new Dimension(0, 90));
        timeline.setOpaque(false);
        
        JLabel receptionLabel = new JLabel("📅 Réception: " + 
            new SimpleDateFormat("dd/MM/yyyy").format(commande.getDate_reception()));
        receptionLabel.setFont(new Font("Inter", Font.BOLD, 13));
        receptionLabel.setForeground(new Color(31, 41, 55));
        receptionLabel.setBounds(65, 18, 300, 20);
        
        JLabel livraisonLabel = new JLabel("🚚 Livraison: " + 
            new SimpleDateFormat("dd/MM/yyyy").format(commande.getDate_livraison()));
        livraisonLabel.setFont(new Font("Inter", Font.BOLD, 13));
        livraisonLabel.setForeground(new Color(31, 41, 55));
        livraisonLabel.setBounds(65, 55, 300, 20);
        
        timeline.add(receptionLabel);
        timeline.add(livraisonLabel);
        
        return timeline;
    }

    // NOUVELLES MÉTHODES POUR LE FORMULAIRE DE MODIFICATION AMÉLIORÉ

    private JTextField createEnhancedTextField(String text, String placeholder) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec légère teinte
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Bordure interactive
                if (hasFocus()) {
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                    
                    // Effet de lueur
                    g2d.setColor(new Color(59, 130, 246, 30));
                    g2d.setStroke(new BasicStroke(5f));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(209, 213, 219));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(14, 18, 14, 18));
        field.setPreferredSize(new Dimension(0, 48));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setOpaque(false);
        
        return field;
    }

    private <T> JComboBox<T> createEnhancedComboBox() {
        JComboBox<T> combo = new JComboBox<T>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Bordure interactive
                if (hasFocus()) {
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                    
                    // Effet de lueur
                    g2d.setColor(new Color(59, 130, 246, 30));
                    g2d.setStroke(new BasicStroke(5f));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    
                    g2d.setColor(new Color(59, 130, 246));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else {
                    g2d.setColor(new Color(209, 213, 219));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        combo.setFont(new Font("Inter", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(0, 48));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        combo.setOpaque(false);
        
        return combo;
    }

    private JPanel createEnhancedFormGroup(String title) {
        JPanel group = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec subtile teinte
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Bordure fine
                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                g2d.dispose();
            }
        };
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Titre de section
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 65, 85));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        group.add(titleLabel);
        
        return group;
    }

    private JPanel createHighlightedFormGroup(String title) {
        JPanel group = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec teinte verte
                g2d.setColor(new Color(240, 253, 244));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Bordure verte
                g2d.setColor(new Color(34, 197, 94));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                
                g2d.dispose();
            }
        };
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Titre de section avec icône
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(22, 163, 74));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        group.add(titleLabel);
        
        return group;
    }

    private JPanel createFieldRow(String label, String description, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(Color.WHITE);
        row.setOpaque(false);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Inter", Font.BOLD, 14));
        labelComponent.setForeground(new Color(55, 65, 81));
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descComponent = new JLabel(description);
        descComponent.setFont(new Font("Inter", Font.PLAIN, 12));
        descComponent.setForeground(new Color(107, 114, 128));
        descComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        descComponent.setBorder(new EmptyBorder(2, 0, 8, 0));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        row.add(labelComponent);
        row.add(descComponent);
        row.add(field);
        
        return row;
    }

    private JButton createEnhancedButton(String text, Color bgColor, Color textColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentBg = bgColor;
                if (getModel().isPressed()) {
                    currentBg = bgColor.darker();
                    // Ombre interne
                    g2d.setColor(new Color(0, 0, 0, 40));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                } else if (getModel().isRollover()) {
                    currentBg = new Color(
                        Math.min(255, bgColor.getRed() + 25),
                        Math.min(255, bgColor.getGreen() + 25),
                        Math.min(255, bgColor.getBlue() + 25)
                    );
                    
                    // Effet de lueur au survol
                    g2d.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 50));
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 16, 16);
                }
                
                g2d.setColor(currentBg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
}