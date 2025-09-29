import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CanteenSystem system = new CanteenSystem();

        while (true) {
            System.out.println("\n--- Smart Canteen System ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View Orders");
            System.out.println("4. Cancel Last Order");
            System.out.println("5. Wallet Balance");
            System.out.println("6. Add Money to Wallet");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> system.showMenu();
                case 2 -> {
                    System.out.print("Enter item numbers (comma separated): ");
                    sc.nextLine();
                    String[] ids = sc.nextLine().split(",");
                    List<Integer> itemIds = new ArrayList<>();
                    for (String id : ids) itemIds.add(Integer.parseInt(id.trim()));
                    system.placeOrder(itemIds);
                }
                case 3 -> system.showOrders();
                case 4 -> system.cancelLastOrder();
                case 5 -> System.out.println("Wallet Balance: ₹" + system.getWallet().getBalance());
                case 6 -> {
                    System.out.print("Enter amount to add: ");
                    double amt = sc.nextDouble();
                    system.getWallet().addMoney(amt);
                    System.out.println("Wallet recharged. Balance: ₹" + system.getWallet().getBalance());
                }
                case 7 -> { 
                    System.out.println("Thank you! Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
