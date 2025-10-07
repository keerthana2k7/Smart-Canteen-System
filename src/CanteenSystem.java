import java.util.*;

public class CanteenSystem {
    private List<MenuItem> menu = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    public CanteenSystem() {
        menu.add(new MenuItem("Idli", 30));
        menu.add(new MenuItem("Dosa", 40));
        menu.add(new MenuItem("Vada", 20));
        menu.add(new MenuItem("Pongal", 35));
        menu.add(new MenuItem("Tea", 15));
    }

    public void showMenu() {
        System.out.println("\n--- Menu ---");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.get(i);
            System.out.println((i + 1) + ". " + item.getName() + " - Rs" + item.getPrice());
        }
    }

    public void placeOrder(List<Integer> itemIds, String timeSlot) {
        List<MenuItem> selectedItems = new ArrayList<>();
        double total = 0;

        for (int id : itemIds) {
            if (id > 0 && id <= menu.size()) {
                MenuItem item = menu.get(id - 1);
                selectedItems.add(item);
                total += item.getPrice();
            } else {
                System.out.println("Invalid item ID: " + id);
            }
        }

        Order order = new Order(selectedItems, timeSlot, total);
        orders.add(order);
        System.out.println("\n✅ Order placed successfully for time slot: " + timeSlot);
        System.out.println("Total amount: Rs" + total);
    }

    public void showOrders() {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders placed yet.");
        } else {
            System.out.println("\n--- All Orders ---");
            for (int i = 0; i < orders.size(); i++) {
                System.out.println("Order " + (i + 1) + ":");
                orders.get(i).showOrder();
                System.out.println("----------------------");
            }
        }
    }

    public void cancelLastOrder() {
        if (!orders.isEmpty()) {
            Order cancelled = orders.remove(orders.size() - 1);
            System.out.println("\n❌ Last order canceled for time slot: " + cancelled.getTimeSlot());
        } else {
            System.out.println("\nNo orders to cancel.");
        }
    }
}
