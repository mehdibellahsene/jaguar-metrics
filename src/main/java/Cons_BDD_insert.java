import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Cons_BDD_insert {

    // Méthode pour insérer les données dans la base de données
    public static void insertIntoDatabase(String data) {
        // Informations de connexion à la base de données MySQL
        String url = "http://localhost:8888/index.php?route=/database/structure&db=phpmyadmin";
        String user = "root";
        String password = "root";

        try {
            // Établir une connexion à la base de données
            Connection connection = DriverManager.getConnection(url, user, password);

            // Requête SQL pour insérer les données dans une table
            String sql = "INSERT INTO data (Cons_urgence) VALUES (generateSpO2)";

            // Création d'une instruction préparée pour exécuter la requête SQL
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, data);

            // Exécution de la requête SQL
            statement.executeUpdate();

            // Fermeture des ressources
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}
