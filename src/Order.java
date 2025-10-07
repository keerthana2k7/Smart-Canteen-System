import java.util.*;

public class Order {
    private List<MenuItem> items;
    private String timeSlot;
    private double total;

    public Order(List<MenuItem> items, String timeSlot, double total) {
        this.items = items;
        this.timeSlot = timeSlot;
        this.total = total;
    }

    public void showOrder() {
        System.out.println("Time Slot: " + timeSlot);
        System.out.println("Items:");
        for (MenuItem item : items) {
            System.out.println("- " + item.getName() + " (Rs" + item.getPrice() + ")");
        }
        System.out.println("Total: Rs" + total);
    }

    public String getTimeSlot() {
        return timeSlot;
    }
}
