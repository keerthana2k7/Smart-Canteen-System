import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CanteenSystem system = new CanteenSystem();
        Payment payment = new Payment(system.getWallet(), system.getOrders());

        while (true) {
            System.out.println("\n--- Smart Canteen System ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order (with Time Slot)");
            System.out.println("3. View Orders");
            System.out.println("4. Cancel Last Order");
            System.out.println("5. Show Top Selling Item");
            System.out.println("6. Make Payment for Last Order");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> system.showMenu();
                case 2 -> {
                    sc.nextLine();
                    System.out.print("Enter item numbers (comma separated): ");
                    String[] ids = sc.nextLine().split(",");
                    List<Integer> itemIds = new ArrayList<>();
                    for (String id : ids) itemIds.add(Integer.parseInt(id.trim()));

                    System.out.println("\nAvailable Time Slots:");
                    System.out.println("1. 10:00 AM - 10:30 AM");
                    System.out.println("2. 10:30 AM - 11:00 AM");
                    System.out.println("3. 11:00 AM - 11:30 AM");
                    System.out.println("4. 11:30 AM - 12:00 PM");
                    System.out.println("5. 12:00 PM - 12:30 PM");
                    System.out.print("Choose preferred time slot (1-5): ");
                    int slotChoice = sc.nextInt();

                    String timeSlot = switch (slotChoice) {
                        case 1 -> "10:00 AM - 10:30 AM";
                        case 2 -> "10:30 AM - 11:00 AM";
                        case 3 -> "11:00 AM - 11:30 AM";
                        case 4 -> "11:30 AM - 12:00 PM";
                        case 5 -> "12:00 PM - 12:30 PM";
                        default -> "Not selected";
                    };

                    system.placeOrder(itemIds, timeSlot);
                }
                case 3 -> system.showOrders();
                case 4 -> system.cancelLastOrder();
                case 5 -> system.showTopSellingItem();
                case 6 -> payment.payForLastOrder();
                case 7 -> {
                    System.out.println("Thank you! Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
