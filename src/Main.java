import java.util.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Main {

    private static final String[] SLOT_LABELS = {
            "1. 09:00 AM - 09:30 AM [09:00 - 09:30]",
            "2. 09:30 AM - 10:00 AM [09:30 - 10:00]",
            "3. 10:00 AM - 10:30 AM [10:00 - 10:30]",
            "4. 10:30 AM - 11:00 AM [10:30 - 11:00]",
            "5. 11:00 AM - 11:30 AM [11:00 - 11:30]",
            "6. 11:30 AM - 12:00 PM [11:30 - 12:00]",
            "7. 12:00 PM - 12:30 PM [12:00 - 12:30]",
            "8. 12:30 PM - 01:00 PM [12:30 - 13:00]",
            "9. 01:00 PM - 01:30 PM [13:00 - 13:30]",
            "10. 01:30 PM - 02:00 PM [13:30 - 14:00]",
            "11. 02:00 PM - 02:30 PM [14:00 - 14:30]",
            "12. 02:30 PM - 03:00 PM [14:30 - 15:00]",
            "13. 03:00 PM - 03:30 PM [15:00 - 15:30]",
            "14. 03:30 PM - 04:00 PM [15:30 - 16:00]",
            "15. 04:00 PM - 04:30 PM [16:00 - 16:30]",
            "16. 04:30 PM - 05:00 PM [16:30 - 17:00]"
    };

    private static final LocalTime[] SLOT_END_TIMES = {
            LocalTime.of(9, 30), LocalTime.of(10, 0),
            LocalTime.of(10, 30), LocalTime.of(11, 0),
            LocalTime.of(11, 30), LocalTime.of(12, 0),
            LocalTime.of(12, 30), LocalTime.of(13, 0),
            LocalTime.of(13, 30), LocalTime.of(14, 0),
            LocalTime.of(14, 30), LocalTime.of(15, 0),
            LocalTime.of(15, 30), LocalTime.of(16, 0),
            LocalTime.of(16, 30), LocalTime.of(17, 0)
    };

    public static boolean isValidSlot(int slot, LocalTime now) {
        if (slot < 1 || slot > SLOT_END_TIMES.length) {
            return false;
        }
        return now.isBefore(SLOT_END_TIMES[slot - 1]);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CanteenSystem canteen = new CanteenSystem();  // removed ui.
        Login login = new Login();                     // removed ui.
        Logout logout = new Logout();
        String username = null;

        while (true) {
            System.out.println("\n=== SMART CANTEEN SYSTEM ===");
            System.out.println("1. User Login");
            System.out.println("2. Register User");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int startChoice = sc.nextInt();
            sc.nextLine();

            switch (startChoice) {
                case 1 -> {
                    if (login.login()) {
                        username = login.getUsername();
                        userMenu(canteen, username, logout, login);
                    }
                }
                case 2 -> {
                    login.register();
                    if (login.login()) {
                        username = login.getUsername();
                        userMenu(canteen, username, logout, login);
                    }
                }
                case 3 -> {
                    if (AdminLogin.validateAdmin()) {
                        adminMenu(canteen);
                    } else {
                        System.out.println("Returning to main menu...");
                    }
                }
                case 4 -> {
                    System.out.println("Exiting... Thank you for using Smart Canteen System!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void userMenu(CanteenSystem canteen, String username, Logout logout, Login login) {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nHi, welcome " + username + "!");
            System.out.println("\n--- Smart Canteen Menu ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order (with Time Slot)");
            System.out.println("3. View Orders");
            System.out.println("4. Cancel Last Order");
            System.out.println("5. Show Top Selling Item");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> canteen.showMenu();
                case 2 -> {
                    canteen.showMenu();
                    System.out.print("Enter item numbers (comma separated): ");
                    String[] itemsInput = sc.nextLine().split(",");
                    ArrayList<Integer> itemIds = new ArrayList<>();
                    for (String s : itemsInput) {
                        try {
                            itemIds.add(Integer.parseInt(s.trim()));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid item ID: " + s);
                        }
                    }

                    System.out.println("\nAvailable Time Slots:");
                    for (String label : SLOT_LABELS) System.out.println(label);
                    System.out.print("Choose preferred time slot (1-" + SLOT_LABELS.length + "): ");
                    int slotChoice = sc.nextInt();
                    sc.nextLine();

                    if (slotChoice < 1 || slotChoice > SLOT_LABELS.length) {
                        System.out.println("Invalid slot number!");
                        break;
                    }

                    String selectedSlot = SLOT_LABELS[slotChoice - 1]
                            .substring(SLOT_LABELS[slotChoice - 1].indexOf('.') + 1).trim();
                    LocalTime now = LocalTime.now();

                    if (!isValidSlot(slotChoice, now)) {
                        System.out.println("❌ Invalid time slot!");
                        System.out.println("Current Time: " + now.truncatedTo(ChronoUnit.MINUTES));
                        System.out.println("Please pick a *future* time slot after " + now.plusMinutes(5).truncatedTo(ChronoUnit.MINUTES));
                        break;
                    }

                    canteen.placeOrder(username, itemIds, selectedSlot);
                }
                case 3 -> canteen.showOrders(username);
                case 4 -> canteen.cancelLastOrder(username);
                case 5 -> canteen.showTopSellingItem();
                case 6 -> {
                    System.out.println("\nLogging out...");
                    logout.logout(username);
                    exit = true;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void adminMenu(CanteenSystem canteen) {
        Scanner sc = new Scanner(System.in);
        boolean exitAdmin = false;

        while (!exitAdmin) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View Menu");
            System.out.println("2. View All Orders");
            System.out.println("3. View Cancelled Orders");
            System.out.println("4. Add Menu Item");
            System.out.println("5. Update Menu Item");
            System.out.println("6. Delete Menu Item");
            System.out.println("7. Show Top Selling Item");
            System.out.println("8. Exit Admin");
            System.out.print("Enter your choice: ");

            int adminChoice = sc.nextInt();
            sc.nextLine();

            switch (adminChoice) {
                case 1 -> canteen.showMenu();
                case 2 -> canteen.showAllOrders();
                case 3 -> canteen.showCancelledOrders();
                case 4 -> {
                    System.out.print("Enter new item name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();
                    canteen.addMenuItem(name, price);
                }
                case 5 -> {
                    System.out.print("Enter old item name: ");
                    String oldName = sc.nextLine();
                    System.out.print("Enter new item name: ");
                    String newName = sc.nextLine();
                    System.out.print("Enter new price: ");
                    double newPrice = sc.nextDouble();
                    sc.nextLine();
                    canteen.updateMenuItem(oldName, newName, newPrice);
                }
                case 6 -> {
                    System.out.print("Enter item name to delete: ");
                    String name = sc.nextLine();
                    canteen.deleteMenuItem(name);
                }
                case 7 -> canteen.showTopSellingItem();
                case 8 -> {
                    System.out.println("Admin logged out successfully!");
                    exitAdmin = true;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
