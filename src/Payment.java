import java.util.List;

public class Payment {
    private List<Order> orders;

    public Payment(List<Order> orders) {
        this.orders = orders;
    }

    public void makePayment() {
        double total = 0;
        for (Order order : orders) {
            total += order.getTotal();
        }
        System.out.println("\n Total amount to pay: Rs" + total);
        System.out.println(" Payment successful!");
    }
}
