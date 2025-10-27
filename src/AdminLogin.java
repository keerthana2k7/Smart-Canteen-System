import java.util.Scanner;

public class AdminLogin {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static boolean validateAdmin() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n👨‍💼 Enter Admin Username: ");
        String user = sc.nextLine();
        System.out.print("🔒 Enter Admin Password: ");
        String pass = sc.nextLine();

        if (user.equals(ADMIN_USERNAME) && pass.equals(ADMIN_PASSWORD)) {
            System.out.println("\n✅ Admin Login Successful!");
            return true;
        } else {
            System.out.println("\n❌ Invalid admin credentials!");
            return false;
        }
    }
}
