package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modele.Client;

public class ClientDAO {
    private Connection connexion;
    private static final String URL = "jdbc:mysql://localhost:3306/royalpressing_base";
    private static final String USER = "username";  // à remplacer par ton nom d'utilisateur
    private static final String PASSWORD = "password";  // à remplacer par ton mot de passe

    public ClientDAO() {
        try {
            connexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    // ✅ Méthode unique pour ajouter un client et récupérer son ID
    public int ajouterClient(Client client) {
        if (connexion == null) return -1;

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
                        client.setId(id); // si ton modèle Client a un setId
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du client : " + e.getMessage());
        }
        return -1; // Échec
    }

    public List<Client> obtenirTousLesClients() {
        List<Client> clients = new ArrayList<>();
        if (connexion == null) return clients;

        String sql = "SELECT id, nom, prenom, telephone, email, adresse FROM clients";
        try (Statement statement = connexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Client client = new Client(
                                        resultSet.getString("nom"),
                    resultSet.getString("prenom"),
                    resultSet.getString("telephone"),
                    resultSet.getString("email"),
                    resultSet.getString("adresse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des clients : " + e.getMessage());
        }
        return clients;
    }

    public Client obtenirClientParId(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Client(
                                                resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("telephone"),
                        resultSet.getString("email"),
                        resultSet.getString("adresse")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client : " + e.getMessage());
        }
        return null;
    }

    public boolean modifierClient(Client client) {
        if (connexion == null) return false;

        String sql = "UPDATE clients SET nom = ?, prenom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setString(1, client.getNom());
            statement.setString(2, client.getPrenom());
            statement.setString(3, client.getTelephone());
            statement.setString(4, client.getEmail());
            statement.setString(5, client.getAdresse());
            statement.setInt(6, client.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du client : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerClient(int id) {
        if (connexion == null) return false;

        String sql = "DELETE FROM clients WHERE id = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            return false;
        }
    }

    public void fermerConnexion() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
