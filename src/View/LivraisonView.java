package View;

// N'oubliez pas d'importer vos classes de DAO et de modèle pour les livraisons
// Par exemple :
// import dao.LivraisonDAO;
// import modele.Livraison;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList; // Exemple, si vous n'avez pas encore de classe Livraison
import java.util.logging.Level;
import java.util.logging.Logger;

public class LivraisonView extends JPanel {

    // Déclarez votre DAO pour les livraisons ici
    // private LivraisonDAO livraisonDAO;
    private final Connection dbConnection; // Gardez la connexion pour le DAO si besoin

    private final Color primaryColor = new Color(99, 102, 241);
    private final Color secondaryColor = new Color(16, 185, 129);
    private final Color lightText = new Color(226, 232, 240);
    private final Color mutedText = new Color(148, 163, 184);

    public LivraisonView(Connection connection) {
        this.dbConnection = connection; // Stocke la connexion
        if (connection != null) {
            // Initialisez votre LivraisonDAO ici
            // this.livraisonDAO = new LivraisonDAO(connection);
        } else {
            Logger.getLogger(LivraisonView.class.getName()).log(Level.WARNING, "LivraisonPanel initialized with null connection.");
        }

        setLayout(new BorderLayout());
        setOpaque(false);

        initComponents();
        loadLivraisonData();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Livraisons");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton newLivraisonBtn = createModernButton("+ Nouvelle Livraison", secondaryColor);
        newLivraisonBtn.addActionListener(e -> {
            // Logique pour ouvrir un modal de nouvelle livraison
            JOptionPane.showMessageDialog(this, "Ouvrir le modal de nouvelle livraison");
            // Exemple : NouvelleLivraisonModal modal = new NouvelleLivraisonModal((JFrame) SwingUtilities.getWindowAncestor(this), dbConnection);
            // modal.setVisible(true);
            // if (modal.isLivraisonAdded()) {
            //     loadLivraisonData();
            // }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(newLivraisonBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    public void loadLivraisonData() {
        // Supprime la table existante si elle y est déjà
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                remove(comp);
                break;
            }
        }

        // Vérifiez si votre livraisonDAO est initialisé
        // if (livraisonDAO == null) {
        //     JLabel errorLabel = new JLabel("Données livraisons non disponibles: Problème de connexion à la base de données.", SwingConstants.CENTER);
        //     errorLabel.setForeground(Color.RED);
        //     errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        //     add(errorLabel, BorderLayout.CENTER);
        //     revalidate();
        //     repaint();
        //     return;
        // }

        // REMPLACEZ CECI PAR LA RÉCUPÉRATION DE VOS VRAIES DONNÉES DE LIVRAISONS
        // List<Livraison> livraisons = livraisonDAO.obtenirToutesLesLivraisons();
        List<String[]> livraisons = new ArrayList<>(); // Exemple de données temporaires
        livraisons.add(new String[]{"LIV001", "CMD001", "2024-05-15", "En Cours", "Livreur Alpha"});
        livraisons.add(new String[]{"LIV002", "CMD002", "2024-05-18", "Livrée", "Livreur Beta"});
        livraisons.add(new String[]{"LIV003", "CMD003", "2024-05-20", "En Attente", "Livreur Gamma"});
        // FIN DE L'EXEMPLE DE DONNÉES TEMPORAIRES

        String[] columnNames = { "ID Livraison", "ID Commande", "Date Livraison", "Statut", "Livreur" }; // Adaptez les noms de colonnes
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] livraison : livraisons) { // Remplacez String[] par votre objet Livraison
            // Object[] row = {
            //     livraison.getIdLivraison(),
            //     livraison.getCommande().getIdCommande(), // Exemple
            //     livraison.getDateLivraison().toString(), // Exemple
            //     livraison.getStatut(),
            //     livraison.getLivreur().getNom() // Exemple
            // };
            model.addRow(livraison); // Utilisez row si vous avez de vrais objets
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