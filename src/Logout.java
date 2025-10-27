import java.sql.*;

public class Logout {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public void logout(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("⚠️ No user is currently logged in.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Optional: Record logout event (if you want to track sessions)
            String logQuery = "INSERT INTO logout_history (username, logout_time) VALUES (?, NOW())";
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS logout_history (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), logout_time DATETIME)");

            PreparedStatement ps = conn.prepareStatement(logQuery);
            ps.setString(1, username);
            ps.executeUpdate();

            System.out.println("\n👋 Goodbye, " + username + "! You have been logged out successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Error during logout: " + e.getMessage());
        }
    }
}
