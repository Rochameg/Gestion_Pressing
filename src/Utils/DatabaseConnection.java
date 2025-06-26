package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pressing_base"; // modifie ici
    private static final String UTILISATEUR = "root"; // ou ton utilisateur
    private static final String MOT_DE_PASSE = ""; // ou ton mot de passe
    private static Connection connexion;

    // Méthode pour obtenir la connexion à la base
    public static Connection getConnection() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // important pour certains IDE
                connexion = DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);

                // Optionnel : désactiver autoCommit pour un contrôle manuel
                // connexion.setAutoCommit(false);

                System.out.println("Connexion à la base de données établie avec succès");
            } catch (ClassNotFoundException e) {
                System.err.println("Pilote JDBC introuvable : " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
                throw e;
            }
        }
        return connexion;
    }

    // Méthode pour fermer la connexion
   

    public static PreparedStatement prepareStatement(String sql) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static PreparedStatement prepareStatement(String sql, int RETURN_GENERATED_KEYS) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static Statement createStatement() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

// package Utils;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// public class DatabaseConnection {
// // Remplacez par votre URL de base de données, nom d'utilisateur et mot de
// passe réels
// private static final String URL =
// "jdbc:mysql://localhost:3306/pressing_base";
// private static final String USER = "root";
// private static final String PASSWORD = "";

// public static Connection getConnection() {
// Connection connection = null;
// try {
// // Charger le pilote JDBC (pour MySQL, ajustez si vous utilisez une autre
// BDD)
// Class.forName("com.mysql.cj.jdbc.Driver");
// connection = DriverManager.getConnection(URL, USER, PASSWORD);
// System.out.println("Connexion à la base de données établie avec succès.");
// } catch (ClassNotFoundException e) {
// System.err.println("Erreur : Pilote JDBC introuvable. Assurez-vous que le JAR
// du pilote JDBC est dans votre classpath.");
// e.printStackTrace();
// } catch (SQLException e) {
// System.err.println("Erreur de connexion à la base de données : " +
// e.getMessage());
// e.printStackTrace();
// }
// return connection;
// }

// public static void closeConnection(Connection connection) {
// if (connection != null) {
// try {
// connection.close();
// System.out.println("Connexion à la base de données fermée.");
// } catch (SQLException e) {
// System.err.println("Erreur lors de la fermeture de la connexion à la base de
// données : " + e.getMessage());
// e.printStackTrace();
// }
// }
// }
// }
