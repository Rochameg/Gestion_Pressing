package dao;

import modele.Commande; // Assurez-vous d'avoir une classe Commande
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class CommandeDAO {
    private Connection connection;

    public CommandeDAO(Connection connection) {
        this.connection = connection;
    }

    // Méthode existante (supposée)
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commandes";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Adaptez la construction de l'objet Commande à votre modèle
                Commande cmd = new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_client"),
                    rs.getDate("date_commande").toLocalDate(),
                    rs.getString("statut_commande"),
                    rs.getDouble("montant_total")
                    // ... autres champs
                );
                commandes.add(cmd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    // NOUVEAU : Obtenir le nombre de commandes par statut
    public Map<String, Integer> getCommandesCountByStatus() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT statut_commande, COUNT(*) AS count FROM commandes GROUP BY statut_commande";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statusCounts.put(rs.getString("statut_commande"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusCounts;
    }

    // NOUVEAU : Obtenir le nombre de livraisons en cours (supposons qu'il y ait un champ 'statut_livraison')
    public int getDeliveriesInProgressCount() {
        String sql = "SELECT COUNT(*) AS in_progress_deliveries FROM livraisons WHERE statut_livraison = 'En cours'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("in_progress_deliveries");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // NOUVEAU : Obtenir le revenu total pour le mois en cours
    public double getTotalRevenueCurrentMonth() {
        String sql = "SELECT SUM(montant_total) AS total_revenue FROM commandes " +
                     "WHERE strftime('%Y-%m', date_commande) = strftime('%Y-%m', 'now')"; // SQLite syntaxe
        // Pour MySQL: "WHERE DATE_FORMAT(date_commande, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')"
        // Pour PostgreSQL: "WHERE TO_CHAR(date_commande, 'YYYY-MM') = TO_CHAR(CURRENT_DATE, 'YYYY-MM')"
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // NOUVEAU : Obtenir les commandes récentes
    public List<Object[]> getRecentOrders(int limit) {
        List<Object[]> orders = new ArrayList<>();
        String sql = "SELECT c.nom_client, c.prenom_client, cmd.service_demande, cmd.statut_commande, cmd.montant_total " +
                     "FROM commandes cmd JOIN clients c ON cmd.id_client = c.id_client " +
                     "ORDER BY cmd.date_commande DESC LIMIT ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Object[]{
                        rs.getString("nom_client") + " " + rs.getString("prenom_client"),
                        rs.getString("service_demande"), // Assurez-vous d'avoir ce champ
                        rs.getString("statut_commande"),
                        rs.getDouble("montant_total") + " FCFA"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // NOUVEAU : Obtenir les commandes mensuelles pour le graphique
    public Map<String, Integer> getMonthlyOrders(int monthsBack) {
        Map<String, Integer> monthlyOrders = new HashMap<>();
        // Ceci est un exemple simplifié. Une requête plus complexe serait nécessaire
        // pour obtenir les données mensuelles sur plusieurs mois.
        // Pour l'instant, je simule juste un mois.
        String sql = "SELECT COUNT(*) AS count, strftime('%Y-%m', date_commande) AS month " +
                     "FROM commandes " +
                     "GROUP BY month ORDER BY month DESC LIMIT ?"; // SQLite syntaxe
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, monthsBack); // Ex: 6 pour les 6 derniers mois
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Ici, vous devrez mapper les 'YYYY-MM' à des noms de mois courts
                    // ou ajuster le graphique JFreeChart pour utiliser des labels complets
                    monthlyOrders.put(rs.getString("month"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyOrders;
    }

    // NOUVEAU : Obtenir la répartition des services (requiert une table ou un champ pour les services)
    public Map<String, Double> getServiceDistribution() {
        Map<String, Double> serviceDistribution = new HashMap<>();
        String sql = "SELECT service_demande, COUNT(*) AS count FROM commandes GROUP BY service_demande"; // Supposons un champ service_demande
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            double totalServices = 0;
            Map<String, Integer> rawCounts = new HashMap<>();
            while (rs.next()) {
                String service = rs.getString("service_demande");
                int count = rs.getInt("count");
                rawCounts.put(service, count);
                totalServices += count;
            }
            if (totalServices > 0) {
                for (Map.Entry<String, Integer> entry : rawCounts.entrySet()) {
                    serviceDistribution.put(entry.getKey(), (entry.getValue() / totalServices) * 100.0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceDistribution;
    }

    // ... autres méthodes
}