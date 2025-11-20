
import java.text.DecimalFormat;
import java.util.List;

public class Order {
    private static int billCounter = 1000;
    private int billNo;
    private String username;
    private List<MenuItem> items;
    private String timeSlot;
    private double total;

    public Order(String username, List<MenuItem> items, String timeSlot, double total) {
        this.username = username;
        this.items = items;
        this.timeSlot = timeSlot;
        this.total = total;
        this.billNo = billCounter++;
    }

    public void showOrder() {
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("\n===============================");
        System.out.println(" BILL NO: " + billNo);
        System.out.println(" USER: " + username);
        System.out.println(" TIME SLOT: " + timeSlot);
        System.out.println("-------------------------------");
        System.out.println(" ITEMS ORDERED:");
        for (MenuItem item : items) {
            System.out.println(" - " + item.getName() + " (Rs " + df.format(item.getPrice()) + ")");
        }
        System.out.println("-------------------------------");
        System.out.println(" TOTAL: Rs " + df.format(total));
        System.out.println("===============================\n");
    }

    public int getBillNo() {
        return billNo;
    }

    public String getUsername() {
        return username;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public double getTotal() {
        return total;
    }

    public List<MenuItem> getItems() {
        return items;
    }
}
