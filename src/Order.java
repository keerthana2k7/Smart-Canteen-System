import java.util.*;

public class Order {
    private int orderId;
    private List<MenuItem> items;
    private double total;
    private String status;

    public Order(int orderId, List<MenuItem> items) {
        this.orderId = orderId;
        this.items = items;
        this.total = items.stream().mapToDouble(MenuItem::getPrice).sum();
        this.status = "Placed";
    }

    public void cancel() {
        this.status = "Cancelled";
    }

    public String getStatus() { return status; }
    public double getTotal() { return total; }

    @Override
    public String toString() {
        return "Order#" + orderId + " " + items + " | Total: ₹" + total + " | Status: " + status;
    }
}
