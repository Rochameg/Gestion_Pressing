
import java.util.*; // ✅ correct
import View.LoginView;
import java.sql.*;

public class Main {
    public static Connection connection;

    public static void main(String[] args) {
        connectToDatabase(); // Connexion d’abord
        if (connection == null) {
            System.err.println("Connexion à la base de données échouée. Fin du programme.");
            return;
        }

        // Démarrer directement avec la fenêtre de connexion
        LoginView login = new LoginView();
        login.afficher();
    }

    private static void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/pressing_base";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données établie avec succès");
        } catch (Exception e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            connection = null;
        }
    }
}
