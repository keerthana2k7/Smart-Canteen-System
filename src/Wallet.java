public class Wallet {
    private double balance;

    public Wallet(double balance) {
        this.balance = balance;
    }

    public boolean deduct(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void addMoney(double amount) {
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }
}
