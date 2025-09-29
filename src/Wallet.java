public class Wallet {
    private double balance;

    public Wallet(double balance) {
        this.balance = balance;
    }

    public void addMoney(double amount) {
        balance += amount;
    }

    public boolean deduct(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance;
    }
}
