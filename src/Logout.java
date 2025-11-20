import java.sql.*;

public class Logout {
    public void logout(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println(" No user is currently logged in.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS logout_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "logout_time DATETIME)");

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO logout_history (username, logout_time) VALUES (?, NOW())");
            ps.setString(1, username);
            ps.executeUpdate();

            System.out.println("\n Goodbye, " + username + "! You have been logged out successfully.");
        } catch (SQLException e) {
            System.out.println(" Error during logout: " + e.getMessage());
        }
    }
}
