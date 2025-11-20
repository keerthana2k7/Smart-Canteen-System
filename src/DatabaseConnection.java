import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
     private static final String URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String USER = "root";
    private static final String PASSWORD = "Keerthu_2007";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found! Check classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed! Check URL, user, or password.");
            e.printStackTrace();
        }
        return null;
    }
}
