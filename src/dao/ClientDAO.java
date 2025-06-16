package dao;

import modele.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    public ClientDAO(Connection connection) {
        this.connection = connection;
    }

    // Obtenir tous les clients
    public List<Client> obtenirTousLesClients() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("telephone"),
                    rs.getString("email"),
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
        String query = "INSERT INTO clients(nom, prenom, telephone, email, adresse) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getEmail());
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
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
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
        String query = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getAdresse());
            stmt.setInt(6, client.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du client", e);
            return false;
        }
    }
}

    // Supprimer un client
//     public boolean supprimerClient(int id) {
//         String query = "DELETE FROM clients WHERE id = ?";

//         try (PreparedStatement stmt = connection.prepareStatement(query)) {
//             stmt.setInt(1, id);
//             int rowsDeleted = stmt.executeUpdate();
//             return rowsDeleted > 0;

//         } catch (SQLException e) {
//             LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du client", e);
//             return false;
//         }
//     }
// }
