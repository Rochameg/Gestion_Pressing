/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import modele.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modal pour afficher les détails complets d'un client
 */
public class ClientDetailsModal extends JDialog {
    private final Color primaryColor = new Color(99, 102, 241);
    private final Color secondaryColor = new Color(16, 185, 129);
    private final Color backgroundColor = new Color(248, 250, 252);
    private final Color cardBackground = Color.WHITE;
    private final Color textPrimary = new Color(15, 23, 42);
    private final Color textSecondary = new Color(100, 116, 139);
    private final Color borderColor = new Color(226, 232, 240);

    private Client client;

    public ClientDetailsModal(JFrame parent, Client client) {
        super(parent, "Détails du Client", true);
        this.client = client;
        
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Header avec avatar et nom
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content avec informations détaillées
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Footer avec boutons d'action
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Avatar avec initiales
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Cercle blanc pour l'avatar
                g2d.setColor(Color.WHITE);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                // Bordure subtile
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(3, 3, getWidth()-6, getHeight()-6);
                
                // Initiales
                g2d.setColor(primaryColor);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
                
                String initiales = "";
                if (client.getNom() != null && !client.getNom().isEmpty()) {
                    initiales += client.getNom().charAt(0);
                }
                if (client.getPrenom() != null && !client.getPrenom().isEmpty()) {
                    initiales += client.getPrenom().charAt(0);
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initiales)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 3;
                g2d.drawString(initiales, x, y);
                
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(120, 120));
        avatarPanel.setOpaque(false);

        // Informations principales
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(client.getPrenom() + " " + client.getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(Color.WHITE);

        JLabel idLabel = new JLabel("ID Client: #" + client.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(new Color(255, 255, 255, 180));

        JLabel statusLabel = new JLabel("🟢 Client Actif");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(new Color(255, 255, 255, 200));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(statusLabel);

        // Container principal du header
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(avatarPanel, BorderLayout.WEST);
        headerContent.add(Box.createHorizontalStrut(30), BorderLayout.CENTER);
        
        JPanel infoContainer = new JPanel(new BorderLayout());
        infoContainer.setOpaque(false);
        infoContainer.add(infoPanel, BorderLayout.CENTER);
        headerContent.add(infoContainer, BorderLayout.EAST);

        headerPanel.add(headerContent, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Section Informations Personnelles
        contentPanel.add(createInfoSection("👤 Informations Personnelles", new String[][]{
            {"Nom complet", client.getPrenom() + " " + client.getNom()},
            {"Nom de famille", client.getNom()},
            {"Prénom", client.getPrenom()}
        }));

        contentPanel.add(Box.createVerticalStrut(25));

        // Section Contact
        contentPanel.add(createInfoSection("📞 Informations de Contact", new String[][]{
            {"Numéro de téléphone", client.getTelephone()},
            {"Adresse email", client.getEmail()},
            {"Adresse postale", client.getAdresse()}
        }));

        contentPanel.add(Box.createVerticalStrut(25));

        // Section Statistiques (simulées)
        //contentPanel.add(createStatsSection());

        contentPanel.add(Box.createVerticalStrut(25));

        // Section Activité Récente (simulée)
        //contentPanel.add(createActivitySection());

        return contentPanel;
    }

    private JPanel createInfoSection(String title, String[][] infos) {
        JPanel sectionPanel = createCard();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Titre de section avec icône
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(titleLabel);
        sectionPanel.add(Box.createVerticalStrut(20));

        // Informations
        for (String[] info : infos) {
            JPanel infoRow = createInfoRow(info[0], info[1]);
            sectionPanel.add(infoRow);
            sectionPanel.add(Box.createVerticalStrut(15));
        }

        return sectionPanel;
    }

    

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel statCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec couleur
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure colorée
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                
                g2d.dispose();
            }
        };
        statCard.setLayout(new BoxLayout(statCard, BoxLayout.Y_AXIS));
        statCard.setOpaque(false);
        statCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        statCard.setPreferredSize(new Dimension(0, 80));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(textPrimary);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(textSecondary);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statCard.add(iconLabel);
        statCard.add(Box.createVerticalStrut(5));
        statCard.add(valueLabel);
        statCard.add(Box.createVerticalStrut(2));
        statCard.add(titleLabel);

        return statCard;
    }

    
    private JPanel createActivityRow(String action, String time, String icon) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 20));

        JLabel actionLabel = new JLabel(action);
        actionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        actionLabel.setForeground(textPrimary);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(textSecondary);

        row.add(iconLabel, BorderLayout.WEST);
        row.add(actionLabel, BorderLayout.CENTER);
        row.add(timeLabel, BorderLayout.EAST);

        return row;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(textSecondary);
        labelComp.setPreferredSize(new Dimension(150, 20));

        JLabel valueComp = new JLabel(value != null && !value.trim().isEmpty() ? value : "Non renseigné");
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueComp.setForeground(textPrimary);

        // Effet de copie pour l'email et le téléphone
        if (label.toLowerCase().contains("email") || label.toLowerCase().contains("téléphone")) {
            valueComp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            valueComp.setToolTipText("Cliquer pour copier");
            valueComp.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    java.awt.datatransfer.StringSelection stringSelection = 
                        new java.awt.datatransfer.StringSelection(value);
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(stringSelection, null);
                    
                    JOptionPane.showMessageDialog(ClientDetailsModal.this,
                        "Copié dans le presse-papiers!", "Information", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);

        return row;
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 16, 16);
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(1, 1, getWidth()-1, getHeight()-1, 16, 16);
                
                // Fond blanc
                g2d.setColor(cardBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Bordure
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(new EmptyBorder(20, 40, 40, 40));

        JButton closeBtn = createStyledButton("Fermer", new Color(107, 114, 128));
        closeBtn.addActionListener(e -> dispose());

        //JButton editBtn = createStyledButton("Modifier", primaryColor);
        //editBtn.addActionListener(e -> {
            // TODO: Ouvrir la modal de modification
            //dispose();
        //});

        JButton printBtn = createStyledButton("Imprimer", secondaryColor);
        printBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité d'impression à implémenter", "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        footerPanel.add(printBtn);
        footerPanel.add(closeBtn);
        //footerPanel.add(editBtn);

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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Highlight subtil
                if (!getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 20));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                }
                
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(110, 44));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }
}