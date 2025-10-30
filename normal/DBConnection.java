import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database details
    private static final String URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // add password if you have one

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            con = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found! Please check your classpath.");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed! Please check URL, username, or password.");
            System.out.println("Error: " + e.getMessage());
        }
        return con;
    }
}
