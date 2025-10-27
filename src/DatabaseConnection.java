import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println(" Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}
