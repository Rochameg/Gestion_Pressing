import View.LoginView;
import java.sql.*;

public class Main {
    public static Connection connection;

    public static void main(String[] args) {
        connectToDatabase();
        LoginView login = new LoginView();
        login.afficher();
    }

    private static void connectToDatabase() {
        try {
            // 1. Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Établir la connexion
            String url = "jdbc:mysql://localhost:3306/pressing_base";
            String user = "root";
            String password = ""; // 
            connection = DriverManager.getConnection(url, user, password);

            System.out.println("Connexion à la base de données établie avec succès");

        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trouvé");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données");
            System.exit(1);
        }
    }
}
