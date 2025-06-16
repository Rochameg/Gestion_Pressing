package View;

/**
 * Interface moderne de gestion des clients avec design amélioré
 * @author Megui_rocha 🐶🐶..!!
 */
import dao.ClientDAO;
import modele.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientView extends JPanel {

    private ClientDAO clientDAO;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalClientsLabel;
    private List<Client> allClients;

    // Palette de couleurs moderne
    private final Color primaryColor = new Color(99, 102, 241);
    private final Color primaryDark = new Color(79, 70, 229);
    private final Color secondaryColor = new Color(16, 185, 129);
    private final Color accentColor = new Color(245, 101, 101);
    private final Color backgroundColor = new Color(248, 250, 252);
    private final Color cardBackground = Color.WHITE;
    private final Color textPrimary = new Color(15, 23, 42);
    private final Color textSecondary = new Color(100, 116, 139);
    private final Color borderColor = new Color(226, 232, 240);
    private final Color hoverColor = new Color(241, 245, 249);

    public ClientView(Connection connection) {
        if (connection != null) {
            this.clientDAO = new ClientDAO(connection);
        } else {
            Logger.getLogger(ClientView.class.getName()).log(Level.WARNING,
                    "ClientPanel initialized with null connection.");
        }

        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadClientData();
    }

    private void initComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Titre et statistiques
        JPanel titleStatsPanel = new JPanel(new BorderLayout());
        titleStatsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestion des Clients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textPrimary);

        totalClientsLabel = new JLabel("0 clients");
        totalClientsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        totalClientsLabel.setForeground(textSecondary);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createVerticalStrut(5));
        titleContainer.add(totalClientsLabel);

        titleStatsPanel.add(titleContainer, BorderLayout.WEST);

        // Actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        // Barre de recherche
        searchField = createSearchField();
        actionsPanel.add(searchField);

        // Bouton nouveau client
        JButton newClientBtn = createModernButton("+ Nouveau Client", secondaryColor, secondaryColor.darker());
        newClientBtn.addActionListener(e -> openNewClientModal());
        actionsPanel.add(newClientBtn);

        titleStatsPanel.add(actionsPanel, BorderLayout.EAST);
        headerPanel.add(titleStatsPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setBackground(cardBackground);
        field.setForeground(textPrimary);
        field.setOpaque(false);

        // Placeholder
        field.setText("Rechercher un client...");
        field.setForeground(textSecondary);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals("Rechercher un client...")) {
                    field.setText("");
                    field.setForeground(textPrimary);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("Rechercher un client...");
                    field.setForeground(textSecondary);
                }
            }
        });

        // Search functionality
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterClients(field.getText());
            }
        });

        field.setPreferredSize(new Dimension(250, 44));
        return field;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Card container pour la table
        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout());

        // Header de la table
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        JLabel tableTitle = new JLabel("Liste des Clients");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(textPrimary);
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(cardBackground);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(20, 20, 20, 20));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        tableCard.add(tableContainer, BorderLayout.CENTER);
        mainPanel.add(tableCard, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 16, 16);
                g2d.fillRoundRect(1, 1, getWidth()-1, getHeight()-1, 16, 16);
                
                // Background
                g2d.setColor(cardBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private void createTable() {
        String[] columnNames = {"N°facture", "Nom", "Prénom", "Téléphone", "Email", "Adresse", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Seulement la colonne Actions est éditable
            }
        };

        clientTable = new JTable(tableModel);
        styleTable(clientTable);
    }

    private void styleTable(JTable table) {
        // Configuration générale
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(56);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(textSecondary);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));

        // Custom cell renderer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Couleurs alternées pour les lignes
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(cardBackground);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                } else {
                    c.setBackground(new Color(239, 246, 255));
                }
                
                c.setForeground(textPrimary);
                setBorder(new EmptyBorder(8, 16, 8, 16));
                
                return c;
            }
        };

        // Appliquer le renderer à toutes les colonnes sauf Actions
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Renderer spécial pour la colonne Actions
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor());

        // Largeurs des colonnes
        int[] columnWidths = {60, 120, 120, 120, 180, 200, 150};
        for (int i = 0; i < columnWidths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Selection styling
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(textPrimary);
    }

    // Renderer pour les boutons d'action
    private class ActionCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton viewBtn, editBtn, deleteBtn;

        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 8));
            setOpaque(true);

            viewBtn = createIconButton("src/images/voir_icon.png", new Color(59, 130, 246));
            editBtn = createIconButton("src/images/modifier_icon.png", primaryColor);
            deleteBtn = createIconButton("src/images/supprimer_icon.png", accentColor);

            add(viewBtn);
            add(editBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? cardBackground : new Color(249, 250, 251));
            }
            
            return this;
        }
    }

    // Editor pour les boutons d'action
    private class ActionCellEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn, editBtn, deleteBtn;
        private int currentRow;

        public ActionCellEditor() {
            super(new JCheckBox());

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 8));
            panel.setOpaque(true);

            viewBtn = createIconButton("src/images/voir_icon.png", new Color(59, 130, 246));
            editBtn = createIconButton("src/images/modifier_icon.png", primaryColor);
            deleteBtn = createIconButton("src/images/supprimer_icon.png", accentColor);

            viewBtn.addActionListener(e -> viewClient());
            editBtn.addActionListener(e -> editClient());
            deleteBtn.addActionListener(e -> deleteClient());

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        private void viewClient() {
            // Récupérer les données du client depuis la table
            String clientId = tableModel.getValueAt(currentRow, 0).toString();
            String nom = tableModel.getValueAt(currentRow, 1).toString();
            String prenom = tableModel.getValueAt(currentRow, 2).toString();
            String telephone = tableModel.getValueAt(currentRow, 3).toString();
            String email = tableModel.getValueAt(currentRow, 4).toString();
            String adresse = tableModel.getValueAt(currentRow, 5).toString();
            
            // Créer et afficher le modal de détails
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ClientView.this);
            ClientDetailsModal detailsModal = new ClientDetailsModal(parentFrame, clientId, nom, prenom, telephone, email, adresse);
            detailsModal.setVisible(true);
            
            fireEditingStopped();
        }

        private void editClient() {
            // Logique d'édition
            fireEditingStopped();
        }

        private void deleteClient() {
            int option = JOptionPane.showConfirmDialog(
                ClientView.this,
                "Êtes-vous sûr de vouloir supprimer ce client ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Logique de suppression
                loadClientData(); // Recharger après suppression
            }
            fireEditingStopped();
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private JButton createIconButton(String imagePath, Color color) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isPressed() ? color.darker() : 
                               getModel().isRollover() ? color : color.brighter();
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        // Charger l'icône depuis le chemin d'image
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            // Redimensionner l'icône à 16x16 pixels
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // En cas d'erreur, bouton vide (pas d'icône fallback)
            System.out.println("Impossible de charger l'icône: " + imagePath);
        }

        button.setPreferredSize(new Dimension(32, 32));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createModernButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isPressed() ? baseColor.darker() : 
                               getModel().isRollover() ? hoverColor : baseColor;
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Subtle highlight
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 20));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                
                g2d.dispose();

                // Text
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g.setColor(Color.WHITE);
                g.setFont(getFont());
                g.drawString(getText(), x, y);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 44));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void openNewClientModal() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        NouveauClientModal modal = new NouveauClientModal(parentFrame);
        modal.setVisible(true);
        
        if (modal.isClientAdded()) {
            loadClientData();
        }
    }

    private void filterClients(String searchText) {
        if (allClients == null) return;

        tableModel.setRowCount(0);
        
        for (Client client : allClients) {
            if (searchText.equals("Rechercher un client...") || searchText.isEmpty() ||
                client.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                client.getPrenom().toLowerCase().contains(searchText.toLowerCase()) ||
                client.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                client.getTelephone().contains(searchText)) {
                
                Object[] row = {
                    client.getId(),
                    client.getNom(),
                    client.getPrenom(),
                    client.getTelephone(),
                    client.getEmail(),
                    client.getAdresse(),
                    ""
                };
                tableModel.addRow(row);
            }
        }
        
        updateClientCount();
    }

    public void loadClientData() {
        if (clientDAO == null) {
            showErrorMessage("Données clients non disponibles: Problème de connexion à la base de données.");
            return;
        }

        allClients = clientDAO.obtenirTousLesClients();
        
        tableModel.setRowCount(0);
        for (Client client : allClients) {
            Object[] row = {
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getTelephone(),
                client.getEmail(),
                client.getAdresse(),
                ""
            };
            tableModel.addRow(row);
        }

        updateClientCount();
        revalidate();
        repaint();
    }

    private void updateClientCount() {
        if (tableModel != null) {
            int count = tableModel.getRowCount();
            totalClientsLabel.setText(count + " client" + (count != 1 ? "s" : ""));
        }
    }

    private void showErrorMessage(String message) {
        removeAll();
        
        JPanel errorPanel = new JPanel(new GridBagLayout());
        errorPanel.setBackground(backgroundColor);
        
        JLabel errorLabel = new JLabel(message);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorLabel.setForeground(accentColor);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        errorPanel.add(errorLabel);
        add(errorPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
}

/**
 * Modal pour afficher les détails complets d'un client
 */
class ClientDetailsModal extends JDialog {
    private final Color primaryColor = new Color(99, 102, 241);
    private final Color backgroundColor = new Color(248, 250, 252);
    private final Color cardBackground = Color.WHITE;
    private final Color textPrimary = new Color(15, 23, 42);
    private final Color textSecondary = new Color(100, 116, 139);
    private final Color borderColor = new Color(226, 232, 240);

    public ClientDetailsModal(JFrame parent, String clientId, String nom, String prenom, 
                             String telephone, String email, String adresse) {
        super(parent, "Détails du Client", true);
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents(clientId, nom, prenom, telephone, email, adresse);
    }

    private void initComponents(String clientId, String nom, String prenom, 
                               String telephone, String email, String adresse) {
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Header
        JPanel headerPanel = createHeaderPanel(nom, prenom);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = createContentPanel(clientId, nom, prenom, telephone, email, adresse);
        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel(String nom, String prenom) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Avatar (initiales)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.setColor(primaryColor);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                
                String initiales = "";
                if (nom.length() > 0) initiales += nom.charAt(0);
                if (prenom.length() > 0) initiales += prenom.charAt(0);
                
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initiales)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(initiales, x, y);
                
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setOpaque(false);

        // Nom complet
        JLabel nameLabel = new JLabel(prenom + " " + nom);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("Client");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setForeground(new Color(255, 255, 255, 180));

        JPanel nameContainer = new JPanel();
        nameContainer.setLayout(new BoxLayout(nameContainer, BoxLayout.Y_AXIS));
        nameContainer.setOpaque(false);
        nameContainer.add(nameLabel);
        nameContainer.add(Box.createVerticalStrut(5));
        nameContainer.add(roleLabel);

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(avatarPanel, BorderLayout.WEST);
        headerContent.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
        
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setOpaque(false);
        namePanel.add(nameContainer, BorderLayout.CENTER);
        headerContent.add(namePanel, BorderLayout.EAST);

        headerPanel.add(headerContent, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createContentPanel(String clientId, String nom, String prenom, 
                                     String telephone, String email, String adresse) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        // Informations personnelles
        contentPanel.add(createInfoSection("Informations Personnelles", new String[][]{
            {"ID Client", clientId},
            {"Nom", nom},
            {"Prénom", prenom}
        }));

        contentPanel.add(Box.createVerticalStrut(20));

        // Contact
        contentPanel.add(createInfoSection("Contact", new String[][]{
            {"Téléphone", telephone},
            {"Email", email}
        }));

        contentPanel.add(Box.createVerticalStrut(20));

        // Adresse
        contentPanel.add(createInfoSection("Adresse", new String[][]{
            {"Adresse complète", adresse}
        }));

        return contentPanel;
    }

    private JPanel createInfoSection(String title, String[][] infos) {
        JPanel sectionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(cardBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2d.dispose();
            }
        };
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre de section
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(15));

        // Informations
        for (String[] info : infos) {
            JPanel infoRow = createInfoRow(info[0], info[1]);
            sectionPanel.add(infoRow);
            sectionPanel.add(Box.createVerticalStrut(12));
        }

        return sectionPanel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(textSecondary);
        labelComp.setPreferredSize(new Dimension(120, 20));

        JLabel valueComp = new JLabel(value != null ? value : "Non renseigné");
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(textPrimary);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);

        return row;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        JButton closeBtn = createStyledButton("Fermer", new Color(107, 114, 128));
        closeBtn.addActionListener(e -> dispose());

        JButton editBtn = createStyledButton("Modifier", primaryColor);
        editBtn.addActionListener(e -> {
            // Logique pour modifier le client
            dispose();
        });

        footerPanel.add(closeBtn);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(editBtn);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = getModel().isPressed() ? color.darker() : 
                               getModel().isRollover() ? color.brighter() : color;
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }
}