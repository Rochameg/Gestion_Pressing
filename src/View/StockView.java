package View;

// N'oubliez pas d'importer vos classes de DAO et de modèle pour les stocks (produits)
// Par exemple :
// import dao.ProduitDAO;
// import modele.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList; // Exemple, si vous n'avez pas encore de classe Produit
import java.util.logging.Level;
import java.util.logging.Logger;

public class StockView extends JPanel {

    // Déclarez votre DAO pour les produits/stocks ici
    // private ProduitDAO produitDAO;
    private final Connection dbConnection; // Gardez la connexion pour le DAO si besoin

    private final Color primaryColor = new Color(99, 102, 241);
    private final Color secondaryColor = new Color(16, 185, 129);
    private final Color lightText = new Color(226, 232, 240);
    private final Color mutedText = new Color(148, 163, 184);

    public StockView(Connection connection) {
        this.dbConnection = connection; // Stocke la connexion
        if (connection != null) {
            // Initialisez votre ProduitDAO ici
            // this.produitDAO = new ProduitDAO(connection);
        } else {
            Logger.getLogger(StockView.class.getName()).log(Level.WARNING, "StockPanel initialized with null connection.");
        }

        setLayout(new BorderLayout());
        setOpaque(false);

        initComponents();
        loadStockData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Stocks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton newProduitBtn = createModernButton("+ Nouveau Produit", secondaryColor);
        newProduitBtn.addActionListener(e -> {
            // Logique pour ouvrir un modal de nouveau produit/stock
            JOptionPane.showMessageDialog(this, "Ouvrir le modal de nouveau produit/stock");
            // Exemple : NouveauProduitModal modal = new NouveauProduitModal((JFrame) SwingUtilities.getWindowAncestor(this), dbConnection);
            // modal.setVisible(true);
            // if (modal.isProduitAdded()) {
            //     loadStockData();
            // }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(newProduitBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    public void loadStockData() {
        // Supprime la table existante si elle y est déjà
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                remove(comp);
                break;
            }
        }

        // Vérifiez si votre produitDAO est initialisé
        // if (produitDAO == null) {
        //     JLabel errorLabel = new JLabel("Données stocks non disponibles: Problème de connexion à la base de données.", SwingConstants.CENTER);
        //     errorLabel.setForeground(Color.RED);
        //     errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        //     add(errorLabel, BorderLayout.CENTER);
        //     revalidate();
        //     repaint();
        //     return;
        // }

        // REMPLACEZ CECI PAR LA RÉCUPÉRATION DE VOS VRAIES DONNÉES DE PRODUITS/STOCKS
        // List<Produit> produits = produitDAO.obtenirTousLesProduits();
        List<String[]> produits = new ArrayList<>(); // Exemple de données temporaires
        produits.add(new String[]{"SHAMPOO", "Shampooing spécial", "50", "Litre", "20.00"});
        produits.add(new String[]{"DETERGENT", "Détergent puissant", "120", "Kg", "15.50"});
        produits.add(new String[]{"EAU_DEMI", "Eau déminéralisée", "80", "Litre", "5.00"});
        // FIN DE L'EXEMPLE DE DONNÉES TEMPORAIRES

        String[] columnNames = { "ID Produit", "Nom", "Quantité", "Unité", "Prix Unitaire" }; // Adaptez les noms de colonnes
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] produit : produits) { // Remplacez String[] par votre objet Produit
            // Object[] row = {
            //     produit.getIdProduit(),
            //     produit.getNom(),
            //     produit.getQuantite(),
            //     produit.getUnite(),
            //     produit.getPrixUnitaire()
            // };
            model.addRow(produit); // Utilisez row si vous avez de vrais objets
        }

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

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
        button.setPreferredSize(new Dimension(180, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }
}