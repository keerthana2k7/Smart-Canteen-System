import java.util.*;

public class Payment {
    private Wallet wallet;
    private List<Order> orders;

    public Payment(Wallet wallet, List<Order> orders) {
        this.wallet = wallet;
        this.orders = orders;
    }

    public void payForLastOrder() {
        if (orders.isEmpty()) {
            System.out.println("No orders to pay for.");
            return;
        }
        Order lastOrder = orders.get(orders.size() - 1);
        double total = lastOrder.getTotal();
        if (wallet.deduct(total)) {
            System.out.println("✅ Payment successful! Paid: Rs" + total);
            ///System.out.println("Remaining Wallet Balance: Rs" + wallet.getBalance());
        } else {
            System.out.println("❌ Not enough balance.");
        }
    }
}
payment 