package View;

import dao.ClientDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel; // Ajout pour les tableaux
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.SQLException;
import Utils.DatabaseConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

// Importations pour JFreeChart (si vous l'utilisez)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset; // Pour un graphique à barres/lignes
import org.jfree.chart.plot.PlotOrientation; // Pour les graphiques à barres/lignes

public class DashboardView extends JFrame {

    private final Color primaryColor = new Color(99, 102, 241);
    private final Color secondaryColor = new Color(16, 185, 129);
    private final Color accentColor = new Color(245, 101, 101);
    private final Color warningColor = new Color(251, 191, 36);
    private final Color darkBg = new Color(15, 23, 42);
    private final Color cardBg = new Color(30, 41, 59);
    private final Color lightText = new Color(226, 232, 240);
    private final Color mutedText = new Color(148, 163, 184);

    private JLabel timeLabel;
    private JLabel dateLabel;
    private final JPanel mainContentPanel;
    private final CardLayout cardLayout;

    private JPanel selectedMenuItem = null;
    private final String[] menuLabels = { "Dashboard", "Clients", "Commande(s)", "Livraison(s)", "Stocks",
            "Déconnexion" };

    private JLabel mainHeaderTitleLabel;
    private JLabel mainHeaderSubtitleLabel;

    private Connection dbConnection;

    public DashboardView() throws SQLException {
        try {
            this.dbConnection = DatabaseConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DashboardView.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Impossible de se connecter à la base de données. Certaines fonctionnalités peuvent être limitées.",
                    "Erreur de Connexion", JOptionPane.ERROR_MESSAGE);
            this.dbConnection = null;
        }

        setTitle("RoyalPressing");
        setIconImage(Toolkit.getDefaultToolkit()
                .getImage(Objects.requireNonNull(getClass().getResource("/images/logo.png"))));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setBackground(darkBg);

        setLayout(new BorderLayout());

        JPanel sidePanel = createModernSidePanel();
        add(sidePanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createDashboardPanel(), menuLabels[0]);
        mainContentPanel.add(new ClientView(this.dbConnection), menuLabels[1]);
        mainContentPanel.add(new CommandeView(this.dbConnection), menuLabels[2]);
        mainContentPanel.add(new LivraisonView(this.dbConnection), menuLabels[3]);
        mainContentPanel.add(new StockView(this.dbConnection), menuLabels[4]);
        mainContentPanel.add(createGenericContentPanel("Déconnexion"), menuLabels[5]);

        JPanel mainPanelContainer = createModernMainPanel();
        mainPanelContainer.add(mainContentPanel, BorderLayout.CENTER);

        add(mainPanelContainer, BorderLayout.CENTER);

        startTimeUpdater();
        startDateUpdater();

        setVisible(true);

        SwingUtilities.invokeLater(() -> {
            for (Component comp : sidePanel.getComponents()) {
                if (comp instanceof JPanel && "NavigationPanel".equals(comp.getName())) {
                    JPanel navigationPanel = (JPanel) comp;
                    for (Component navItem : navigationPanel.getComponents()) {
                        if (navItem instanceof JPanel) {
                            JPanel item = (JPanel) navItem;
                            for (Component subComp : item.getComponents()) {
                                if (subComp instanceof JLabel) {
                                    JLabel labelComp = (JLabel) subComp;
                                    String labelText = labelComp.getText();
                                    if (labelText != null && labelText.equals("Dashboard")) {
                                        updateSelection(item);
                                        // updateMainHeaderTitle(menuLabels[0]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private JPanel createModernSidePanel() {
        JPanel sidePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, darkBg, 0, getHeight(), cardBg);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(280, 0));
        sidePanel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JPanel logoPanel = createLogoPanel();
        sidePanel.add(logoPanel);
        sidePanel.add(Box.createVerticalStrut(40));

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setOpaque(false);
        navigationPanel.setName("NavigationPanel");

        createModernNavigation(navigationPanel);
        sidePanel.add(navigationPanel);

        sidePanel.add(Box.createVerticalGlue());

        JPanel footerPanel = createFooterPanel();
        sidePanel.add(footerPanel);

        return sidePanel;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel logoContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        logoContainer.setLayout(new BorderLayout());
        logoContainer.setPreferredSize(new Dimension(230, 80));
        logoContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/logo.png")));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledLogo));

        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel("Royal Pressing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(lightText);

        JLabel subtitleLabel = new JLabel("Votre partenaire de nettoyage");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(mutedText);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        logoContainer.add(iconLabel, BorderLayout.WEST);
        logoContainer.add(textPanel, BorderLayout.CENTER);
        logoPanel.add(logoContainer, BorderLayout.CENTER);

        return logoPanel;
    }

    private void createModernNavigation(JPanel parent) {
        String[] iconPaths = {
                "/images/dashboard_icon.png",
                "/images/clients_icon.png",
                "/images/commande_icon.png",
                "/images/delivery_icon.png",
                "/images/stock_icon.png",
                "/images/deconnexion_icon.png"
        };
        int iconSize = 24;

        for (int i = 0; i < menuLabels.length; i++) {
            ImageIcon icon = null;
            try {
                java.net.URL imgURL = getClass().getResource(iconPaths[i]);
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize,
                            Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImage);
                } else {
                    System.err.println("Couldn't find image file: " + iconPaths[i]
                            + ". Using a placeholder or defaulting to no icon.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                icon = null;
            }

            JPanel menuItem = createNavigationItem(icon, menuLabels[i]);
            final int index = i;
            menuItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    updateSelection(menuItem);
                    if (menuLabels[index].equals("Déconnexion")) {
                        DashboardView.this.dispose();
                        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
                    } else {
                        cardLayout.show(mainContentPanel, menuLabels[index]);
                        // updateMainHeaderTitle(menuLabels[index]);

                        // --- AJOUT : Rafraîchissement des données pour Clients, Commandes, Livraisons,
                        // et Stocks ---
                        if (menuLabels[index].equals("Clients")) {
                            Component[] components = mainContentPanel.getComponents();
                            for (Component comp : components) {
                                if (comp instanceof ClientView) {
                                    ((ClientView) comp).loadClientData();
                                    break;
                                }
                            }
                        } else if (menuLabels[index].equals("Commande(s)")) {
                            Component[] components = mainContentPanel.getComponents();
                            for (Component comp : components) {
                                if (comp instanceof CommandeView) {
                                    try {
                                        ((CommandeView) comp).loadCommandeData();
                                    } catch (SQLException ex) {
                                        Logger.getLogger(DashboardView.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                                }
                            }
                        } else if (menuLabels[index].equals("Livraison(s)")) {
                            Component[] components = mainContentPanel.getComponents();
                            for (Component comp : components) {
                                if (comp instanceof LivraisonView) {
                                    ((LivraisonView) comp).loadLivraisonData();
                                    break;
                                }
                            }
                        } else if (menuLabels[index].equals("Stocks")) {
                            Component[] components = mainContentPanel.getComponents();
                            for (Component comp : components) {
                                if (comp instanceof StockView) {
                                    ((StockView) comp).loadStockData();
                                    break;
                                }
                            }
                        }
                        // -----------------------------------------------------------------------------------------
                    }
                }
            });
            parent.add(menuItem);
            parent.add(Box.createVerticalStrut(8));
        }
    }

    private JPanel createNavigationItem(ImageIcon icon, String label) {
        JPanel item = new JPanel() {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean isThisSelected = (selectedMenuItem == this);

                if (isThisSelected) {
                    GradientPaint gradient = new GradientPaint(0, 0, primaryColor, getWidth(), 0, secondaryColor);
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                } else if (hovered) {
                    g2d.setColor(new Color(255, 255, 255, 10));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2d.dispose();
            }
        };

        item.setLayout(new BorderLayout());
        item.setPreferredSize(new Dimension(230, 50));
        item.setMaximumSize(new Dimension(230, 50));
        item.setBorder(new EmptyBorder(12, 20, 12, 20));
        item.setOpaque(false);
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel;
        if (icon != null) {
            iconLabel = new JLabel(icon);
        } else {
            iconLabel = new JLabel(" ");
            iconLabel.setPreferredSize(new Dimension(30, 30));
        }

        iconLabel.setPreferredSize(new Dimension(30, 30));
        iconLabel.setForeground(lightText);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        textLabel.setForeground(lightText);
        textLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", true);
                item.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", false);
                item.repaint();
            }
        });

        return item;
    }

    private void updateSelection(JPanel newItem) {
        if (selectedMenuItem != null) {
            for (Component comp : selectedMenuItem.getComponents()) {
                if (comp instanceof JLabel) {
                    JLabel labelComp = (JLabel) comp;
                    if (labelComp.getIcon() == null) {
                        labelComp.setForeground(lightText);
                    }
                }
            }
            selectedMenuItem.repaint();
        }

        selectedMenuItem = newItem;
        for (Component comp : selectedMenuItem.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel labelComp = (JLabel) comp;
                if (labelComp.getIcon() == null) {
                    labelComp.setForeground(Color.WHITE);
                }
            }
        }
        selectedMenuItem.repaint();
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setForeground(secondaryColor);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(mutedText);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footer.add(timeLabel);
        footer.add(Box.createVerticalStrut(5));
        footer.add(dateLabel);

        return footer;
    }

    private JPanel createModernMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(248, 250, 252), 0, getHeight(),
                        new Color(241, 245, 249));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // JPanel headerPanel = createModernHeader("📊 Tableau de Bord", "Vue d'overview
        // de votre pressing moderne");
        JPanel headerPanel = createModernHeader(
                "Tableau de Bord",
                "/images/tableau_icon.png" // Chemin relatif dans le classpath
        );

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    private JPanel createModernHeader(String title,  String iconPath) {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);

    // Création du titre avec icône
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    titleLabel.setForeground(new Color(30, 41, 59));

    if (iconPath != null && !iconPath.isEmpty()) {
        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(scaledImage));
            titleLabel.setIconTextGap(10); // Espace entre l’icône et le texte
        } else {
            System.err.println("❌ Image introuvable : " + iconPath);
        }
    }

    //JLabel subtitleLabel = new JLabel(subtitle);
    //subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
    //subtitleLabel.setForeground(new Color(100, 116, 139));

    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.setOpaque(false);
    textPanel.add(titleLabel);
    textPanel.add(Box.createVerticalStrut(5));
    //textPanel.add(subtitleLabel);

    headerPanel.add(textPanel, BorderLayout.WEST);

    return headerPanel;
}

    // private void updateMainHeaderTitle(String menuLabel) {
    // String title = "";
    // String subtitle = "";

    // switch (menuLabel) {
    // case "Dashboard":
    // title = " Tableau de Bord";
    // subtitle = "Vue d'ensemble de votre pressing moderne";
    // break;
    // case "Clients":
    // title = " Gestion des Clients";
    // subtitle = "Gérez vos informations clients et leur historique";
    // break;
    // case "Commande(s)":
    // title = " Suivi des Commandes";
    // subtitle = "Gérez vos informations commandes et leur progression";
    // break;
    // case "Livraison(s)":
    // title = " Gestion des Livraisons";
    // subtitle = "Surveillez et gérez vos livraisons";
    // break;
    // case "Stocks":
    // title = " Gestion des Stocks";
    // subtitle = "Gérez les stocks de produits et fournitures";
    // break;
    // case "Déconnexion":
    // title = " Rapports et Analyses";
    // subtitle = "Accédez à des analyses et des statistiques détaillées";
    // break;
    // }
    // mainHeaderTitleLabel.setText(title);
    // mainHeaderSubtitleLabel.setText(subtitle);
    // }

    // private JPanel createActionButtons() {
    //     JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    //     panel.setOpaque(false);

    //     JButton newClientBtn = createModernButton("+ Nouveau Client", secondaryColor);
    //     JButton reportBtn = createModernButton("/ Rapport", primaryColor);

    //     newClientBtn.addActionListener(e -> {
    //         NouveauClientModal modal = new NouveauClientModal(DashboardView.this);
    //         modal.setVisible(true);
    //         if (modal.isClientAdded()) {
    //             Component[] components = mainContentPanel.getComponents();
    //             for (Component comp : components) {
    //                 if (comp instanceof ClientView) {
    //                     ((ClientView) comp).loadClientData();
    //                     break;
    //                 }
    //             }
    //         }
    //     });

    //     reportBtn.addActionListener(e -> {
    //         JOptionPane.showMessageDialog(this, "Fonctionnalité de rapport à implémenter.");
    //     });

    //     panel.add(newClientBtn);
    //     panel.add(reportBtn);

    //     return panel;
    // }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, color, 0, getHeight(),
                        new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                g2d.dispose();

                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g.setColor(Color.WHITE);
                g.setFont(getFont());
                g.drawString(getText(), x, y);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(140, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 10)); // Ajout d'un espace vertical
        content.setOpaque(false);

        // Panneau supérieur pour les statistiques
        JPanel statsPanel = createModernStatsPanel();
        content.add(statsPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 30)); // Grille 1x2 pour les tableaux
        bottomPanel.setOpaque(false);

        content.add(bottomPanel, BorderLayout.SOUTH);

        return content;
    }

    // Nouvelle méthode pour créer une carte générique pour le contenu
    private JPanel createModernCard(String title, JComponent contentComponent) {
        JPanel card = new JPanel() {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 50, getHeight() - 1, 0, 20);
                g2d.setColor(new Color(25, 23, 24));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 50, getHeight() -1, 0, 20);

                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 15)); // Espacement interne
        card.setBorder(new EmptyBorder(50, 20, 20, 50));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));
        card.add(titleLabel, BorderLayout.NORTH);

        card.add(contentComponent, BorderLayout.CENTER); // Ajout du composant de contenu passé en paramètre

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", true);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", false);
                card.repaint();
            }
        });

        return card;
    }

    private JPanel createGenericContentPanel(String menuName) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel("Contenu de la section : " + menuName);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(new Color(30, 41, 59));
        panel.add(label);
        return panel;
    }

    private JPanel createModernStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        statsPanel.add(createModernStatCard("/images/clients_stat.png", "Clients Actifs", "156", secondaryColor));
        statsPanel.add(createModernStatCard("/images/delivery_stat.png", "Livraisons", "23", primaryColor));
        statsPanel.add(createModernStatCard("/images/revenue_stat.png", "Revenus", " 2,840 Fcfa", warningColor));
        statsPanel.add(createModernStatCard("/images/stock_stat.png", "Stock", "89%", accentColor));

        return statsPanel;
    }

    private JPanel createModernStatCard(String iconPath, String title, String value, Color accentColor) {
        JPanel card;
        card = new JPanel() {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hovered) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 20, 20);
                }

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);

                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);

                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth() - 4, 6, 20, 20);

                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 140));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(100, 116, 139));

        JLabel iconLabel;
        try {
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(iconPath)));
            Image scaledImage = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Couldn't find stat card icon: " + iconPath + ". Using placeholder text.");
            iconLabel = new JLabel("?");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(45, 45));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(iconLabel, BorderLayout.EAST);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(new Color(30, 41, 59));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.add(valueLabel);
        bottomPanel.add(Box.createVerticalStrut(5));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(bottomPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", true);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", false);
                card.repaint();
            }
        });

        return card;
    }

    // --- NOUVELLES MÉTHODES POUR LE CONTENU DU DASHBOARD ---

    private ChartPanel createServicesPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        // Remplacez ces données par des données réelles de votre BDD
        dataset.setValue("Lavage à sec", 45);
        dataset.setValue("Nettoyage humide", 30);
        dataset.setValue("Repassage", 15);
        dataset.setValue("Retouches", 10);

        JFreeChart chart = ChartFactory.createPieChart(
                "", // Titre du graphique (vide car déjà dans la carte)
                dataset,
                true, // Légende
                true, // Tooltips
                false // URLs
        );

        // Personnalisation du graphique
        chart.setBackgroundPaint(null); // Rendre le fond transparent
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null); // Rendre le fond du plot transparent
        plot.setOutlineVisible(false); // Cacher la bordure du plot
        plot.setSectionPaint("Lavage à sec", primaryColor);
        plot.setSectionPaint("Nettoyage humide", secondaryColor);
        plot.setSectionPaint("Repassage", warningColor);
        plot.setSectionPaint("Retouches", accentColor);
        plot.setLabelBackgroundPaint(Color.WHITE); // Couleur de fond des labels
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 10)); // Police des labels

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false); // Rend le panneau du graphique transparent
        chartPanel.setPreferredSize(new Dimension(300, 250)); // Taille par défaut, sera ajustée par le layout
        chartPanel.setBorder(BorderFactory.createEmptyBorder()); // Pas de bordure par défaut
        return chartPanel;
    }

    // Réutilisation de la méthode de style de table existante
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(197, 222, 255));
        table.setFillsViewportHeight(true);
    }

    private void startTimeUpdater() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    LocalTime now = LocalTime.now();
                    timeLabel.setText(String.format("%02d:%02d:%02d",
                            now.getHour(), now.getMinute(), now.getSecond()));
                });
            }
        }, 0, 1000);
    }

    private void startDateUpdater() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    java.time.LocalDate today = java.time.LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM");
                    dateLabel.setText(today.format(formatter));
                });
            }
        }, 0, 60 * 1000);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                new DashboardView();
            } catch (SQLException ex) {
                Logger.getLogger(DashboardView.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}