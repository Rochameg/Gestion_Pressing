package dao;

import Utils.DatabaseConnection;
import modele.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {
    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    // Supprimer le constructeur avec Connection et la variable d'instance
    // Plus besoin de stocker la connexion

    // Méthode pour obtenir une nouvelle connexion à chaque fois
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection(); // Remplacez par votre méthode de connexion
    }

    // Obtenir tous les clients
    public List<Client> obtenirTousLesClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("adresse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des clients", e);
        }

        return clients;
    }

    // Ajouter un client
    public boolean ajouterClient(Client client) {
        String query = "INSERT INTO clients(nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(4, client.getEmail());
            stmt.setString(3, client.getTelephone());
            stmt.setString(5, client.getAdresse());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du client", e);
            return false;
        }
    }

    // Obtenir un client par ID
    public Client obtenirClientParId(int id) {
        String query = "SELECT * FROM clients WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du client par ID", e);
        }

        return null;
    }

    // Modifier un client
    public boolean modifierClient(Client client) {
        String query = "UPDATE clients SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(4, client.getEmail());
            stmt.setString(3, client.getTelephone());
            stmt.setString(5, client.getAdresse());
            stmt.setInt(6, client.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du client", e);
            return false;
        }
    }

    // Supprimer un client
    public boolean supprimerClient(int id) {
        String query = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du client", e);
            return false;
        }
    }

    // Rechercher des clients par nom ou prénom
    public List<Client> rechercherClients(String searchTerm) {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients WHERE nom LIKE ? OR prenom LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                    );
                    clients.add(client);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de clients", e);
        }

        return clients;
    }

    // Vérifier si un email existe déjà
    public boolean emailExiste(String email) {
        String query = "SELECT COUNT(*) FROM clients WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'email", e);
        }
        
        return false;
    }
}