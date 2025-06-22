package dao;

import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modele.Commande;
import modele.Client;

public class CommandeDAO {
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(CommandeDAO.class.getName());
    
    // Requêtes préparées pour de meilleures performances
    private static final String SELECT_ALL_COMMANDES = 
        "SELECT c.id, c.client_id, c.date_reception, c.date_livraison, c.statut, c.article, c.total, c.priorite, " +
        "cl.nom, cl.prenom, cl.email, cl.telephone, cl.adresse " +
        "FROM commandes c " +
        "LEFT JOIN clients cl ON c.client_id = cl.id " +
        "ORDER BY c.id ASC";
    
    private static final String SELECT_COMMANDE_BY_ID = 
        "SELECT c.id, c.client_id, c.date_reception, c.date_livraison, c.statut, c.article, c.total, c.priorite, " +
        "cl.nom, cl.prenom, cl.email, cl.telephone, cl.adresse " +
        "FROM commandes c " +
        "LEFT JOIN clients cl ON c.client_id = cl.id " +
        "WHERE c.id = ?";
    
    private static final String SEARCH_COMMANDES = 
        "SELECT c.id, c.client_id, c.date_reception, c.date_livraison, c.statut, c.article, c.total, c.priorite, " +
        "cl.nom, cl.prenom, cl.email, cl.telephone, cl.adresse " +
        "FROM commandes c " +
        "LEFT JOIN clients cl ON c.client_id = cl.id " +
        "WHERE LOWER(c.article) LIKE LOWER(?) " +
        "OR LOWER(c.statut) LIKE LOWER(?) " +
        "OR CAST(c.id AS CHAR) LIKE ? " +
        "OR LOWER(cl.nom) LIKE LOWER(?) " +
        "OR LOWER(cl.prenom) LIKE LOWER(?) " +
        "OR LOWER(cl.email) LIKE LOWER(?) " +
        "OR LOWER(cl.telephone) LIKE LOWER(?) " +
        "OR LOWER(CONCAT(cl.prenom, ' ', cl.nom)) LIKE LOWER(?) " +
        "ORDER BY c.id ASC";
    
    public CommandeDAO(Connection connection) {
        this.connection = connection;
        validateConnection();
    }
    
    /**
     * Valide la connexion à la base de données
     */
    private void validateConnection() {
        if (connection == null) {
            throw new IllegalArgumentException("La connexion à la base de données ne peut pas être null");
        }
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("La connexion à la base de données est fermée");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la validation de la connexion", e);
        }
    }
    
    /**
     * Récupère toutes les commandes avec les informations complètes du client
     * @return Liste de toutes les commandes avec les données client complètes
     * @throws SQLException
     */
    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_COMMANDES)) {
            
            while (rs.next()) {
                Commande commande = createCommandeFromResultSet(rs);
                commandes.add(commande);
            }
            
            LOGGER.info("Récupération de " + commandes.size() + " commandes");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les commandes", e);
            throw e;
        }
        
        return commandes;
    }
    
    /**
     * Récupère une commande spécifique par son ID avec toutes les informations client
     * @param id L'ID de la commande à récupérer
     * @return La commande correspondante ou null si non trouvée
     * @throws SQLException
     */
    public Commande getCommandeById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID de la commande doit être positif");
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_COMMANDE_BY_ID)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = createCommandeFromResultSet(rs);
                    LOGGER.info("Commande trouvée avec l'ID: " + id);
                    return commande;
                } else {
                    LOGGER.warning("Aucune commande trouvée avec l'ID: " + id);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la commande avec l'ID: " + id, e);
            throw e;
        }
    }
    
    /**
     * Crée une nouvelle commande dans la base de données
     * @param commande La commande à créer
     * @return L'ID de la commande créée ou -1 en cas d'erreur
     * @throws SQLException
     */
    public int createCommande(Commande commande) throws SQLException {
    if (commande == null) {
        throw new IllegalArgumentException("La commande ne peut pas être null");
    }

    validateCommande(commande);

    String query = "INSERT INTO commandes (client_id, date_reception, date_livraison, statut, article, total, priorite) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection(); // Remplace ici
         PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        setCommandeParameters(pstmt, commande, false);

        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    LOGGER.info("Commande créée avec succès - ID: " + newId);
                    return newId;
                }
            }
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Erreur lors de la création de la commande", e);
        throw e;
    }

    return -1;
}
    
    /**
     * Met à jour une commande existante
     * @param commande La commande avec les nouvelles données
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException
     */
    public boolean updateCommande(Commande commande) throws SQLException {
        if (commande == null) {
            throw new IllegalArgumentException("La commande ne peut pas être null");
        }
        if (commande.getId() <= 0) {
            throw new IllegalArgumentException("L'ID de la commande doit être positif");
        }
        
        validateCommande(commande);
        
        
        String query = "UPDATE commandes SET client_id = ?, date_reception = ?, date_livraison = ?, " +
                      "statut = ?, article = ?, total = ?, priorite = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            setCommandeParameters(pstmt, commande, true);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("Commande mise à jour avec succès - ID: " + commande.getId());
                return true;
            } else {
                LOGGER.warning("Aucune commande trouvée avec l'ID: " + commande.getId());
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la commande avec l'ID: " + commande.getId(), e);
            throw e;
        }
    }
    
    /**
     * Supprime une commande de la base de données
     * @param id L'ID de la commande à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException
     */
    public boolean deleteCommande(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID de la commande doit être positif");
        }
        
        String query = "DELETE FROM commandes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.info("Commande supprimée avec succès - ID: " + id);
                return true;
            } else {
                LOGGER.warning("Aucune commande trouvée avec l'ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la commande avec l'ID: " + id, e);
            throw e;
        }
    }
    
    /**
     * Recherche des commandes par critères (inclut la recherche par toutes les informations client)
     * @param searchTerm Terme de recherche
     * @return Liste des commandes correspondantes
     * @throws SQLException
     */
    public List<Commande> searchCommandes(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCommandes();
        }
        
        List<Commande> commandes = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(SEARCH_COMMANDES)) {
            String searchPattern = "%" + searchTerm.trim() + "%";
            
            // Définir tous les paramètres de recherche
            for (int i = 1; i <= 8; i++) {
                pstmt.setString(i, searchPattern);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = createCommandeFromResultSet(rs);
                    commandes.add(commande);
                }
            }
            
            LOGGER.info("Recherche '" + searchTerm + "' - " + commandes.size() + " résultats trouvés");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de commandes avec le terme: " + searchTerm, e);
            throw e;
        }
        
        return commandes;
    }
    
    /**
     * Filtre les commandes par statut avec informations client
     * @param statut Le statut à filtrer
     * @return Liste des commandes avec le statut spécifié
     * @throws SQLException
     */
    public List<Commande> getCommandesByStatut(String statut) throws SQLException {
        if (statut == null || statut.trim().isEmpty()) {
            throw new IllegalArgumentException("Le statut ne peut pas être vide");
        }
        
        List<Commande> commandes = new ArrayList<>();
        String query = SELECT_ALL_COMMANDES.replace("ORDER BY c.id ASC", "WHERE c.statut = ? ORDER BY c.id ASC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, statut.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = createCommandeFromResultSet(rs);
                    commandes.add(commande);
                }
            }
            
            LOGGER.info("Filtrage par statut '" + statut + "' - " + commandes.size() + " résultats");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du filtrage par statut: " + statut, e);
            throw e;
        }
        
        return commandes;
    }
    
    /**
     * Filtre les commandes par priorité avec informations client
     * @param priorite La priorité à filtrer
     * @return Liste des commandes avec la priorité spécifiée
     * @throws SQLException
     */
    public List<Commande> getCommandesByPriorite(String priorite) throws SQLException {
        if (priorite == null || priorite.trim().isEmpty()) {
            throw new IllegalArgumentException("La priorité ne peut pas être vide");
        }
        
        List<Commande> commandes = new ArrayList<>();
        String query = SELECT_ALL_COMMANDES.replace("ORDER BY c.id ASC", "WHERE c.priorite = ? ORDER BY c.id ASC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, priorite.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = createCommandeFromResultSet(rs);
                    commandes.add(commande);
                }
            }
            
            LOGGER.info("Filtrage par priorité '" + priorite + "' - " + commandes.size() + " résultats");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du filtrage par priorité: " + priorite, e);
            throw e;
        }
        
        return commandes;
    }
    
    /**
     * Filtre les commandes par client
     * @param clientId L'ID du client
     * @return Liste des commandes du client spécifié
     * @throws SQLException
     */
    public List<Commande> getCommandesByClient(int clientId) throws SQLException {
        if (clientId <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être positif");
        }
        
        List<Commande> commandes = new ArrayList<>();
        String query = SELECT_ALL_COMMANDES.replace("ORDER BY c.id ASC", "WHERE c.client_id = ? ORDER BY c.id ASC");
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, clientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = createCommandeFromResultSet(rs);
                    commandes.add(commande);
                }
            }
            
            LOGGER.info("Commandes du client " + clientId + " - " + commandes.size() + " résultats");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des commandes du client: " + clientId, e);
            throw e;
        }
        
        return commandes;
    }
    
    /**
     * Compte le nombre total de commandes
     * @return Le nombre total de commandes
     * @throws SQLException
     */
    public int getCommandeCount() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM commandes";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int count = rs.getInt("total");
                LOGGER.info("Nombre total de commandes: " + count);
                return count;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des commandes", e);
            throw e;
        }
        return 0;
    }
    
    /**
     * Compte les commandes par statut
     * @return Statistiques des commandes par statut
     * @throws SQLException
     */
    public java.util.Map<String, Integer> getCommandeCountByStatut() throws SQLException {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        String query = "SELECT statut, COUNT(*) as count FROM commandes GROUP BY statut";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                stats.put(rs.getString("statut"), rs.getInt("count"));
            }
            
            LOGGER.info("Statistiques par statut calculées: " + stats.size() + " statuts différents");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul des statistiques par statut", e);
            throw e;
        }
        
        return stats;
    }
    
    /**
     * Calcule le total des ventes
     * @return Le montant total de toutes les commandes
     * @throws SQLException
     */
    public double getTotalVentes() throws SQLException {
        String query = "SELECT COALESCE(SUM(total), 0) as total_ventes FROM commandes";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                double total = rs.getDouble("total_ventes");
                LOGGER.info("Total des ventes: " + total + "€");
                return total;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du calcul du total des ventes", e);
            throw e;
        }
        return 0.0;
    }
    
    /**
     * Récupère tous les clients pour les listes déroulantes
     * @return Liste des clients avec ID, nom et prénom
     * @throws SQLException
     */
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT id, nom, prenom FROM clients ORDER BY nom ASC, prenom ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Client client = new Client(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom")
                );
                clients.add(client);
            }
            
            LOGGER.info("Récupération de " + clients.size() + " clients");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des clients", e);
            throw e;
        }
        return clients;
    }
    
    /**
     * Vérifie si une commande existe avec l'ID spécifié
     * @param id L'ID à vérifier
     * @return true si la commande existe, false sinon
     * @throws SQLException
     */
    public boolean commandeExists(int id) throws SQLException {
        if (id <= 0) {
            return false;
        }
        
        String query = "SELECT 1 FROM commandes WHERE id = ? LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification d'existence de la commande avec l'ID: " + id, e);
            throw e;
        }
    }
    
    /**
     * Méthode utilitaire pour créer un objet Commande depuis un ResultSet
     */
    private Commande createCommandeFromResultSet(ResultSet rs) throws SQLException {
        Commande commande = new Commande(
            rs.getInt("id"),
            rs.getInt("client_id"),
            rs.getDate("date_reception"),
            rs.getDate("date_livraison"),
            rs.getString("statut"),
            rs.getString("article"),
            rs.getDouble("total"),
            rs.getString("priorite")
        );
        
        // Ajouter les informations complètes du client
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String email = rs.getString("email");
        String telephone = rs.getString("telephone");
        String adresse = rs.getString("adresse");
        
        if (nom != null && prenom != null) {
            commande.setClientNomComplet(prenom + " " + nom);
            commande.setClientEmail(email != null ? email : "Non renseigné");
            commande.setClientTelephone(telephone != null ? telephone : "Non renseigné");
            commande.setClientAdresse(adresse != null ? adresse : "Non renseignée");
        } else {
            commande.setClientNomComplet("Client CMD-000" + rs.getInt("client_id"));
            commande.setClientEmail("Non disponible");
            commande.setClientTelephone("Non disponible");
            commande.setClientAdresse("Non disponible");
        }
        
        return commande;
    }
    
    /**
     * Méthode utilitaire pour définir les paramètres d'une commande dans un PreparedStatement
     */
    private void setCommandeParameters(PreparedStatement pstmt, Commande commande, boolean includeId) throws SQLException {
        pstmt.setInt(1, commande.getClient_id());
        pstmt.setDate(2, new java.sql.Date(commande.getDate_reception().getTime()));
        pstmt.setDate(3, commande.getDate_livraison() != null ? 
            new java.sql.Date(commande.getDate_livraison().getTime()) : null);
        pstmt.setString(4, commande.getStatut());
        pstmt.setString(5, commande.getArticle());
        pstmt.setDouble(6, commande.getTotal());
        pstmt.setString(7, commande.getPriorite());
        
        if (includeId) {
            pstmt.setInt(8, commande.getId());
        }
    }
    
    /**
     * Valide les données d'une commande
     */
    private void validateCommande(Commande commande) {
        if (commande.getClient_id() <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être positif");
        }
        if (commande.getDate_reception() == null) {
            throw new IllegalArgumentException("La date de réception ne peut pas être null");
        }
        if (commande.getStatut() == null || commande.getStatut().trim().isEmpty()) {
            throw new IllegalArgumentException("Le statut ne peut pas être vide");
        }
        if (commande.getArticle() == null || commande.getArticle().trim().isEmpty()) {
            throw new IllegalArgumentException("L'article ne peut pas être vide");
        }
        if (commande.getTotal() < 0) {
            throw new IllegalArgumentException("Le total ne peut pas être négatif");
        }
        if (commande.getPriorite() == null || commande.getPriorite().trim().isEmpty()) {
            throw new IllegalArgumentException("La priorité ne peut pas être vide");
        }
    }

    public int createClient(Client client) throws SQLException {
        String sql = "INSERT INTO clients (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();  // nouvelle connexion
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getTelephone());
            stmt.setString(5, client.getAdresse());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du client, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // retourne l'ID généré
                } else {
                    throw new SQLException("Échec de la création du client, aucun ID généré.");
                }
            }
        }
    }



    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients WHERE email = ?";
        try (Connection conn = connection;
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Client getClientByEmail(String email) throws SQLException {
    String sql = "SELECT * FROM clients WHERE email = ?";
    try (Connection conn = connection;
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
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
    }
    return null;
}



    // Classe interne pour représenter un client simplifié
   
    public static class Client {
        private int id;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String adresse;

        public Client(int id, String nom, String prenom, String email, String telephone, String adresse) {
            this.id = id;
            this.nom = nom != null ? nom : "";
            this.prenom = prenom != null ? prenom : "";
            this.email = email != null ? email : "";
            this.telephone = telephone != null ? telephone : "";
            this.adresse = adresse != null ? adresse : "";
        }


        public Client(String nom, String prenom, String email, String telephone, String adresse) {
            this.id = id;
            this.nom = nom != null ? nom : "";
            this.prenom = prenom != null ? prenom : "";
            this.email = email != null ? email : "";
            this.telephone = telephone != null ? telephone : "";
            this.adresse = adresse != null ? adresse : "";
        }

        public Client(int id, String nom, String prenom) {
            this(id, nom, prenom, "", "", "");
        }

        // Getters
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getEmail() { return email; }
        public String getTelephone() { return telephone; }
        public String getAdresse() { return adresse; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setNom(String nom) { this.nom = nom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setEmail(String email) { this.email = email; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
        public void setAdresse(String adresse) { this.adresse = adresse; }

        public String getNomComplet() {
            return prenom + " " + nom;
        }

        @Override
        public String toString() {
            return getNomComplet() + " (ID: " + id + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Client client = (Client) obj;
            return id == client.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

}