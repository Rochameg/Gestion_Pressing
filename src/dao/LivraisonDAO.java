package dao;

import modele.Livraison;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class LivraisonDAO {
    private Connection connection;
    private String nomClient;     // ✅ nouveau
    private String nomLivreur;  
    
    


    public LivraisonDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Livraison> getToutesLesLivraisons() {
    if (connection == null) {
        throw new IllegalStateException("Connexion non initialisée dans LivraisonDAO !");
    }
    List<Livraison> liste = new ArrayList<>();

    String sql = """
        SELECT l.*, c.nom AS nom_client, lv.nom AS nom_livreur
        FROM livraisons l
        JOIN clients c ON l.client_id = c.id
        JOIN livreurs lv ON l.livreur_id = lv.id
    """;

    try (PreparedStatement stmt = connection.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Livraison l = new Livraison();
            l.setId(rs.getInt("id"));
            l.setClientId(rs.getInt("client_id"));
            l.setLivreurId(rs.getInt("livreur_id"));
            l.setNomClient(rs.getString("nom_client"));       // ✅ récupère le nom client
            l.setNomLivreur(rs.getString("nom_livreur"));     // ✅ récupère le nom livreur
            l.setAdresseLivraison(rs.getString("adresse_livraison"));
            l.setDateLivraison(rs.getDate("date_livraison"));
            l.setHeureLivraison(rs.getTime("heure_livraison"));
            l.setPriorite(rs.getString("priorite"));
            l.setArticles(rs.getString("articles"));
            l.setStatut(rs.getString("statut"));
            liste.add(l);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return liste;
}

   public boolean ajouterLivraison(Livraison l) {
    String sql = "INSERT INTO livraisons (client_id, livreur_id, adresse_livraison, date_livraison, heure_livraison, priorite, articles, statut) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, l.getClientId());
        stmt.setInt(2, l.getLivreurId());
        stmt.setString(3, l.getAdresseLivraison());
        stmt.setDate(4, new java.sql.Date(l.getDateLivraison().getTime()));
        stmt.setTime(5, l.getHeureLivraison());
        stmt.setString(6, l.getPriorite());
        stmt.setString(7, l.getArticles());
        stmt.setString(8, l.getStatut());

        stmt.executeUpdate();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public void annulerLivraison(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
