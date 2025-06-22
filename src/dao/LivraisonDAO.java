package dao;

import modele.Livraison;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivraisonDAO {
    private Connection connection;

    public LivraisonDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Livraison> getToutesLesLivraisons() {
        List<Livraison> livraisons = new ArrayList<>();
        String sql = "SELECT l.*, c.nom as nom_client, li.nom as nom_livreur " +
                     "FROM livraisons l " +
                     "LEFT JOIN clients c ON l.client_id = c.id " +
                     "LEFT JOIN livreurs li ON l.livreur_id = li.id " +
                     "ORDER BY l.date_creation DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Livraison livraison = mapResultSetToLivraison(rs);
                livraisons.add(livraison);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getToutesLesLivraisons: " + e.getMessage());
            e.printStackTrace();
        }
        return livraisons;
    }

    public boolean ajouterLivraison(Livraison livraison) {
        String sql = "INSERT INTO livraisons (client_id, livreur_id, adresse_livraison, date_livraison, " +
                     "heure_livraison, priorite, articles, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, livraison.getClientId());
            stmt.setInt(2, livraison.getLivreurId());
            stmt.setString(3, livraison.getAdresseLivraison());
            stmt.setDate(4, new java.sql.Date(livraison.getDateLivraison().getTime()));
            stmt.setTime(5, livraison.getHeureLivraison());
            stmt.setString(6, livraison.getPriorite());
            stmt.setString(7, livraison.getArticles());
            stmt.setString(8, livraison.getStatut());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        livraison.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajouterLivraison: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimerLivraison(int id) {
        String sql = "DELETE FROM livraisons WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimerLivraison: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean annulerLivraison(int id) {
        String sql = "UPDATE livraisons SET statut = 'Annulée' WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur annulerLivraison: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getStatutCommande(int idCommande) {
        String sql = "SELECT statut FROM livraisons WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("statut");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getStatutCommande: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean modifierLivraison(Livraison livraison) {
        String sql = "UPDATE livraisons SET client_id = ?, livreur_id = ?, adresse_livraison = ?, " +
                     "date_livraison = ?, heure_livraison = ?, priorite = ?, articles = ?, statut = ? " +
                     "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livraison.getClientId());
            stmt.setInt(2, livraison.getLivreurId());
            stmt.setString(3, livraison.getAdresseLivraison());
            stmt.setDate(4, new java.sql.Date(livraison.getDateLivraison().getTime()));
            stmt.setTime(5, livraison.getHeureLivraison());
            stmt.setString(6, livraison.getPriorite());
            stmt.setString(7, livraison.getArticles());
            stmt.setString(8, livraison.getStatut());
            stmt.setInt(9, livraison.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur modifierLivraison: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Livraison mapResultSetToLivraison(ResultSet rs) throws SQLException {
        Livraison livraison = new Livraison();
        livraison.setId(rs.getInt("id"));
        livraison.setClientId(rs.getInt("client_id"));
        livraison.setLivreurId(rs.getInt("livreur_id"));
        livraison.setNomClient(rs.getString("nom_client"));
        livraison.setNomLivreur(rs.getString("nom_livreur"));
        livraison.setAdresseLivraison(rs.getString("adresse_livraison"));
        livraison.setDateLivraison(rs.getDate("date_livraison"));
        livraison.setHeureLivraison(rs.getTime("heure_livraison"));
        livraison.setPriorite(rs.getString("priorite"));
        livraison.setArticles(rs.getString("articles"));
        livraison.setStatut(rs.getString("statut"));
        return livraison;
    }

    public List<Livraison> getAllLivraisons() {
    return new ArrayList<>(); // temporaire
}

}