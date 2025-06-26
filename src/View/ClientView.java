package View;

/**
 * Interface moderne de gestion des clients avec CRUD complet
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
            this.clientDAO = new ClientDAO();
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
        String[] columnNames = {"ID", "Nom", "Prénom", "Téléphone", "Email", "Adresse", "Actions"};
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

            viewBtn = createIconButton("👁️", new Color(59, 130, 246), "Voir");
            editBtn = createIconButton("✏️", primaryColor, "Modifier");
            deleteBtn = createIconButton("🗑️", accentColor, "Supprimer");

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

            viewBtn = createIconButton("👁️", new Color(59, 130, 246), "Voir");
            editBtn = createIconButton("✏️", primaryColor, "Modifier");
            deleteBtn = createIconButton("🗑️", accentColor, "Supprimer");

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
            // Arrêter l'édition d'abord
            fireEditingStopped();
            
            // Vérifier que la ligne est valide
            if (currentRow < 0 || currentRow >= tableModel.getRowCount()) {
                JOptionPane.showMessageDialog(ClientView.this, 
                    "Ligne invalide sélectionnée!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int clientId = Integer.parseInt(tableModel.getValueAt(currentRow, 0).toString());
                Client client = clientDAO.obtenirClientParId(clientId);
                
                if (client != null) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ClientView.this);
                    ClientDetailsModal detailsModal = new ClientDetailsModal(parentFrame, client);
                    detailsModal.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(ClientView.this, 
                        "Client introuvable!", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ClientView.this, 
                    "Erreur lors de l'affichage du client: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void editClient() {
            // Arrêter l'édition d'abord
            fireEditingStopped();
            
            // Vérifier que la ligne est valide
            if (currentRow < 0 || currentRow >= tableModel.getRowCount()) {
                JOptionPane.showMessageDialog(ClientView.this, 
                    "Ligne invalide sélectionnée!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int clientId = Integer.parseInt(tableModel.getValueAt(currentRow, 0).toString());
                Client client = clientDAO.obtenirClientParId(clientId);
                
                if (client != null) {
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ClientView.this);
                    ClientFormModal editModal = new ClientFormModal(parentFrame, client, clientDAO);
                    editModal.setVisible(true);
                    
                    if (editModal.isClientSaved()) {
                        // Recharger les données après modification
                        SwingUtilities.invokeLater(() -> {
                            loadClientData();
                            JOptionPane.showMessageDialog(ClientView.this, 
                                "Client modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(ClientView.this, 
                        "Client introuvable!", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ClientView.this, 
                    "Erreur lors de la modification: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteClient() {
            // Arrêter l'édition d'abord pour éviter les conflits
            fireEditingStopped();
            
            // Vérifier que la ligne est valide
            if (currentRow < 0 || currentRow >= tableModel.getRowCount()) {
                JOptionPane.showMessageDialog(ClientView.this, 
                    "Ligne invalide sélectionnée!", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int option = JOptionPane.showConfirmDialog(
                ClientView.this,
                "Êtes-vous sûr de vouloir supprimer ce client ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                try {
                    int clientId = Integer.parseInt(tableModel.getValueAt(currentRow, 0).toString());
                    boolean success = clientDAO.supprimerClient(clientId);
                    
                    if (success) {
                        // Recharger les données après suppression
                        SwingUtilities.invokeLater(() -> {
                            loadClientData();
                            JOptionPane.showMessageDialog(ClientView.this, 
                                "Client supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } else {
                        JOptionPane.showMessageDialog(ClientView.this, 
                            "Erreur lors de la suppression du client!", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientView.this, 
                        "Erreur lors de la suppression: " + ex.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private JButton createIconButton(String icon, Color color, String tooltip) {
        JButton button = new JButton(icon) {
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

        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(32, 32));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);

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
        ClientFormModal modal = new ClientFormModal(parentFrame, null, clientDAO);
        modal.setVisible(true);
        
        if (modal.isClientSaved()) {
            // Recharger les données après ajout
            SwingUtilities.invokeLater(() -> {
                loadClientData();
                JOptionPane.showMessageDialog(this, 
                    "Client ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            });
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