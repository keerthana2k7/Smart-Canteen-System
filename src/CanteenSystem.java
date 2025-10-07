import java.util.*;

public class CanteenSystem {
    private List<MenuItem> menu = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Wallet wallet = new Wallet(200); // default balance

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
        for (int id : itemIds) {
            if (id > 0 && id <= menu.size()) {
                selectedItems.add(menu.get(id - 1));
            } else {
                System.out.println("Invalid item ID: " + id);
            }
        }

        Order order = new Order(selectedItems, timeSlot);
        orders.add(order);
        System.out.println("\n✅ Order placed successfully for time slot: " + timeSlot);
        System.out.println("Total amount: Rs" + order.getTotal());
    }

    public void showOrders() {
        if (orders.isEmpty()) {
            System.out.println("\nNo orders placed yet.");
            return;
        }
        System.out.println("\n--- All Orders ---");
        for (int i = 0; i < orders.size(); i++) {
            System.out.println("Order " + (i + 1) + ":");
            orders.get(i).showOrder();
            System.out.println("----------------------");
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

    // Top selling items
    public void showTopSellingItems() {
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        Map<String, Integer> countMap = new HashMap<>();
        for (Order order : orders) {
            for (MenuItem item : order.getItems()) {
                countMap.put(item.getName(), countMap.getOrDefault(item.getName(), 0) + 1);
            }
        }
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
            (a, b) -> b.getValue() - a.getValue()
        );
        pq.addAll(countMap.entrySet());
        System.out.println("\n--- Top Selling Items ---");
        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> e = pq.poll();
