import java.util.List;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Payment {
    private List<Order> orders;  // Changed src.Order → Order

    public Payment(List<Order> orders) {
        this.orders = orders;
    }

    public void makePayment() {
        if (orders == null || orders.isEmpty()) {
            System.out.println("\n No orders found. Please place an order before proceeding to payment.");
            return;
        }

        double total = 0;
        for (Order order : orders) {  // Changed src.Order → Order
            total += order.getTotal();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("\n Total amount to pay: Rs " + df.format(total));

        Scanner sc = new Scanner(System.in);
        System.out.print("Select payment method (1 - Cash / 2 - Card / 3 - UPI): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                System.out.println(" Payment successful via Cash!");
                break;
            case 2:
                System.out.println(" Payment successful via Card!");
                break;
            case 3:
                System.out.println(" Payment successful via UPI!");
                break;
            default:
                System.out.println(" Invalid option. Payment failed!");
                return;
        }

        System.out.println(" Thank you for your purchase!");
    }
}
