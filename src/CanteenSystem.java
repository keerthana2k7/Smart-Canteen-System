import java.util.*;

public class CanteenSystem {
    private List<MenuItem> menu = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private Wallet wallet = new Wallet(200); // default ₹200
    private int orderCounter = 1;

    public CanteenSystem() {
        menu.add(new MenuItem(1, "Pizza", 120));
        menu.add(new MenuItem(2, "Burger", 80));
        menu.add(new MenuItem(3, "Sandwich", 60));
    }

    public void showMenu() {
        menu.forEach(System.out::println);
    }

    public void placeOrder(List<Integer> itemIds) {
        List<MenuItem> selected = new ArrayList<>();
        for (int id : itemIds) {
            selected.add(menu.get(id - 1));
        }
        Order order = new Order(orderCounter++, selected);
        if (wallet.deduct(order.getTotal())) {
            orders.add(order);
            System.out.println("✅ Order placed: " + order);
        } else {
            System.out.println("❌ Not enough balance. Recharge wallet.");
        }
    }

    public void showOrders() {
        if (orders.isEmpty()) {
            System.out.println("No orders yet.");
        } else {
            orders.forEach(System.out::println);
        }
    }

    public void cancelLastOrder() {
        if (!orders.isEmpty()) {
            Order last = orders.get(orders.size() - 1);
            last.cancel();
            System.out.println("❌ Order cancelled: " + last);
        } else {
            System.out.println("No orders to cancel.");
        }
    }

    public Wallet getWallet() {
        return wallet;
    }
}
