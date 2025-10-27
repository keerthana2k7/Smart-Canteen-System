import java.util.ArrayList;
import java.util.Scanner;
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
        CanteenSystem canteen = new CanteenSystem();
        Login login = new Login();
        Logout logout = new Logout();

        System.out.println("=== SMART CANTEEN SYSTEM ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        int startChoice = sc.nextInt();
        sc.nextLine(); 

        boolean isLoggedIn = false;
        if (startChoice == 1) {
            isLoggedIn = login.login();
        } else if (startChoice == 2) {
            login.register();
            isLoggedIn = login.login();
        }

        if (!isLoggedIn) {
            System.out.println(" Login failed. Please try again later.");
            return;
        }

        System.out.println("\nHi, welcome " + login.getUsername() + "!");

        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- Smart Canteen Menu ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order (with Time Slot)");
            System.out.println("3. View Orders");
            System.out.println("4. Cancel Last Order");
            System.out.println("5. Show Top Selling Item");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            String inputLine = sc.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(inputLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> canteen.showMenu();

                case 2 -> {
                    System.out.print("Enter item numbers (comma separated): ");
                    String[] itemsInput = sc.nextLine().split(",");
                    ArrayList<Integer> itemIds = new ArrayList<>();

                    for (String s : itemsInput) {
                        try {
                            String trimmedS = s.trim();
                            if (!trimmedS.isEmpty()) {
                                itemIds.add(Integer.parseInt(trimmedS));
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid item ID: " + s);
                        }
                    }

                    System.out.println("\nAvailable Time Slots:");
                    for (String label : SLOT_LABELS) System.out.println(label);
                    System.out.print("Choose preferred time slot (1-" + SLOT_LABELS.length + "): ");

                    int slotChoice;
                    try {
                        slotChoice = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid time slot choice!");
                        break;
                    }

                    if (slotChoice < 1 || slotChoice > SLOT_LABELS.length) {
                        System.out.println("Invalid slot number!");
                        break;
                    }

                    String fullSlotLabel = SLOT_LABELS[slotChoice - 1];
                    String selectedSlot = fullSlotLabel.substring(fullSlotLabel.indexOf('.') + 1).trim();
                    LocalTime now = LocalTime.now();
                    System.out.println(" Current Time: " + now.truncatedTo(ChronoUnit.MINUTES));

                    if (!isValidSlot(slotChoice, now)) {
                        System.out.println(" Please pick a valid *future* time slot!");
                        break;
                    }

                    canteen.placeOrder(itemIds, selectedSlot);
                }

                case 3 -> canteen.showOrders();
                case 4 -> canteen.cancelLastOrder();
                case 5 -> canteen.showTopSellingItem();
                case 6 -> {
                    System.out.println("\nThank you for using Smart Canteen System!");
                    exit = true;
                }
                default -> System.out.println("Invalid choice!");
            }
        }

        System.out.print("\nDo you want to logout? (yes/no): ");
        String logoutChoice = sc.nextLine().trim();
        if (logoutChoice.equalsIgnoreCase("yes")) {
            logout.logout(login.getUsername());
        } else {
            System.out.println("Session kept active.");
        }
    }
}
