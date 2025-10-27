import java.sql.*;

public class Logout {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String DB_USER = "root"; // your MySQL username
    private static final String DB_PASS = "";     // your MySQL password (if any)

    // ✅ When user logs out, delete their record
    public void logout(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String deleteQuery = "DELETE FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setString(1, username);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("👋 Goodbye, " + username + "! Your account has been deleted successfully.");
            } else {
                System.out.println("⚠️ No such user found in database.");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error during logout: " + e.getMessage());
        }
    }
}
