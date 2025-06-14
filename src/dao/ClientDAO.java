package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modele.Client;
// import Utils.DatabaseConnection; // Plus besoin d'importer directement ici si vous passez la connexion au constructeur

public class ClientDAO {
    private Connection connexion;

    public ClientDAO(Connection connexion) {
        if (connexion == null) {
            throw new IllegalArgumentException("La connexion à la base de données ne peut pas être nulle.");
        }
        this.connexion = connexion;
    }

    /**
     * Ajoute un nouveau client à la base de données.
     * @param client L'objet Client à ajouter.
     * @return L'ID généré du client si l'ajout est réussi, -1 sinon.
     */
    public int ajouterClient(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, telephone, email, adresse) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getTelephone());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getAdresse());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        client.setId(id); // Mettre à jour l'objet client avec l'ID généré
                        System.out.println("Client ajouté avec succès. ID : " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du client : " + e.getMessage());
            // Pour le débogage, il est souvent utile de laisser la trace complète
            // e.printStackTrace();
        }
        return -1;
    }

    /**
     * Récupère tous les clients de la base de données.
     * @return Une liste de tous les clients.
     */
    public List<Client> obtenirTousLesClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, telephone, email, adresse FROM clients";
        try (Statement statement = connexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Client client = new Client(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("prenom"),
                    resultSet.getString("telephone"),
                    resultSet.getString("email"),
                    resultSet.getString("adresse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les clients : " + e.getMessage());
        }
        return clients;
    }

    /**
     * Récupère un client par son ID.
     * @param id L'ID du client à récupérer.
     * @return L'objet Client correspondant à l'ID, ou null si non trouvé.
     */
    public Client obtenirClientParId(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Client client = new Client(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("adresse")
                    );
                    return client;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client par ID : " + e.getMessage());
        }
        return null;
    }

    /**
     * Modifie les informations d'un client existant.
     * @param client L'objet Client avec les informations mises à jour (doit avoir un ID valide).
     * @return true si la modification est réussie, false sinon.
     */
    public boolean modifierClient(Client client) {
        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getTelephone());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getAdresse());
            statement.setInt(6, client.getId());

            int rowsUpdated = statement.executeUpdate();
            System.out.println("Client avec ID " + client.getId() + " mis à jour. Lignes affectées : " + rowsUpdated);
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du client : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un client de la base de données par son ID.
     * @param id L'ID du client à supprimer.
     * @return true si la suppression est réussie, false sinon.
     */
    public boolean supprimerClient(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            System.out.println("Client avec ID " + id + " supprimé. Lignes affectées : " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            return false;
        }
    }
}