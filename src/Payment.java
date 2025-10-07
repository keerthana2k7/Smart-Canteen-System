import java.util.List;

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

        Order last = orders.get(orders.size() - 1);
        if (wallet.deduct(last.getTotal())) {
            System.out.println("✅ Payment successful for last order. Amount: Rs" + last.getTotal());
        } else {
            System.out.println("❌ Not enough balance in wallet. Please recharge.");
        }
    }
}
