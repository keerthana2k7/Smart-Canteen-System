import java.sql.*;
import java.util.Scanner;

public class Login {
    private String username;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public String getUsername() {
        return username;
    }

    // ✅ Username validation (≥8 chars, at least 1 uppercase, 1 digit, 1 special)
    private boolean isValidUsername(String username) {
        boolean correctLength = username.length() >= 8;
        boolean hasUppercase = username.matches(".*[A-Z].*");
        boolean hasSpecial = username.matches(".*[@#$%^&+=!_].*");
        boolean hasDigit = username.matches(".*\\d.*");
        return correctLength && hasUppercase && hasSpecial && hasDigit;
    }

    // ✅ Password validation (only 4 digits)
    private boolean isValidPassword(String password) {
        return password.matches("\\d{4}");
    }

    // ✅ User registration
    public void register() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n🧾 Enter username (≥8 chars, 1 uppercase, 1 number, 1 special char): ");
        String uname = sc.nextLine().trim();

        if (!isValidUsername(uname)) {
            System.out.println("⚠️ Invalid username! Must be at least 8 characters, include one uppercase letter, one number, and one special character.");
            return;
        }

        System.out.print("🔑 Enter 4-digit numeric password: ");
        String pwd = sc.nextLine().trim();

        if (!isValidPassword(pwd)) {
            System.out.println("⚠️ Invalid password! Only 4 digits (0–9) are allowed — no letters or symbols.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // ensure table exists
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) PRIMARY KEY, password VARCHAR(10))");

            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ps.executeUpdate();
            System.out.println("✅ Registration successful!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Username already exists. Try another one.");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    // ✅ User login
    public boolean login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n👤 Enter username: ");
        String uname = sc.nextLine().trim();
        System.out.print("🔒 Enter password: ");
        String pwd = sc.nextLine().trim();

        if (!isValidPassword(pwd)) {
            System.out.println("⚠️ Invalid password format! Only 4 digits are allowed.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                username = uname;
                System.out.println("✅ Login successful! Welcome, " + username + "!");
                return true;
            } else {
                System.out.println("❌ Invalid credentials. Please register or try again.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
            return false;
        }
    }
}
