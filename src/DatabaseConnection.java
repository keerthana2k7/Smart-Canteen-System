import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println(" MySQL JDBC Driver not found! Please check your classpath.");
        } catch (SQLException e) {
            System.out.println(" Database connection failed! Please check URL, user, or password.");
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
