import java.sql.*;
import java.util.Scanner;

public class Login {
    private String username;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smart_canteen";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; // your MySQL password

    public String getUsername() {
        return username;
    }

    // 🧩 Validate Username
    private boolean isValidUsername(String username) {
        boolean correctLength = username.length() >= 7 && username.length() <= 8;
        boolean hasUppercase = username.matches(".*[A-Z].*");
        boolean hasSpecial = username.matches(".*[@#$%^&+=!_].*");
        boolean hasDigit = username.matches(".*\\d.*");
        return correctLength && hasUppercase && hasSpecial && hasDigit;
    }

    // 🔒 Validate Password (exactly 4 digits)
    private boolean isValidPassword(String password) {
        return password.matches("\\d{4}");
    }

    // 📝 Register New User
    public void register() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username (7-8 chars, 1 uppercase, 1 number, 1 special char): ");
        String uname = sc.nextLine();

        if (!isValidUsername(uname)) {
            System.out.println("❌ Invalid username! It must be 7–8 characters long, include at least one uppercase letter, one number, and one special character.");
            return;
        }

        System.out.print("Enter 4-digit numeric password: ");
        String pwd = sc.nextLine();

        if (!isValidPassword(pwd)) {
            System.out.println("❌ Invalid password! Password must be exactly 4 digits (numbers only).");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ps.executeUpdate();
            System.out.println("✅ Registration successful!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠ Username already exists. Try another one.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // 🔑 Login Existing User
    public boolean login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String uname = sc.nextLine();
        System.out.print("Enter password: ");
        String pwd = sc.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, uname);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                username = uname;
                System.out.println("✅ Login successful!");
                return true;
            } else {
                System.out.println("❌ Invalid credentials. Please register or try again.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
}
