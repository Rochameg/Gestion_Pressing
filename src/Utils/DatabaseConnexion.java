/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnexion {
    // Constantes pour la connexion à la base de données
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pressing_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Instance unique de connexion (pattern Singleton)
    private static Connection connection = null;

    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private DatabaseConnexion() {
        // Constructeur privé pour le pattern singleton
    }

    /**
     * Établit une connexion à la base de données si elle n'existe pas déjà
     * @return Une instance de Connection
     * @throws SQLException En cas d'erreur de connexion
     * @throws ClassNotFoundException Si le driver JDBC n'est pas trouvé
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            try {
                // Chargement du driver JDBC
                Class.forName(JDBC_DRIVER);

                // Création de la connexion
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données établie avec succès");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC non trouvé : " + e.getMessage());
                throw e;
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données
     * @throws SQLException En cas d'erreur lors de la fermeture
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connexion à la base de données fermée");
        }
    }

    /**
     * Exécute une requête SELECT et retourne les résultats sous forme de ResultSet
     * @param query La requête SQL à exécuter
     * @param params Les paramètres à insérer dans la requête
     * @return Le ResultSet contenant les résultats de la requête
     * @throws SQLException En cas d'erreur SQL
     * @throws ClassNotFoundException Si le driver JDBC n'est pas trouvé
     */
    public static ResultSet executeQuery(String query, Object... params) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        // Définition des paramètres
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeQuery();
    }

    /**
     * Exécute une requête INSERT, UPDATE ou DELETE
     * @param query La requête SQL à exécuter
     * @param params Les paramètres à insérer dans la requête
     * @return Le nombre de lignes affectées
     * @throws SQLException En cas d'erreur SQL
     * @throws ClassNotFoundException Si le driver JDBC n'est pas trouvé
     */
    public static int executeUpdate(String query, Object... params) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);

        // Définition des paramètres
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt.executeUpdate();
    }

    /**
     * Exécute une requête INSERT et retourne la clé générée automatiquement
     * @param query La requête SQL à exécuter
     * @param params Les paramètres à insérer dans la requête
     * @return La clé générée automatiquement
     * @throws SQLException En cas d'erreur SQL
     * @throws ClassNotFoundException Si le driver JDBC n'est pas trouvé
     */
    public static long executeInsertWithGeneratedKey(String query, Object... params) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        // Définition des paramètres
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        stmt.executeUpdate();

        // Récupération de la clé générée
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getLong(1);
        }

        return -1; // Aucune clé générée
    }

}