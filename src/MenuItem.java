public class MenuItem {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String availableFrom;

    public MenuItem(int id, String name, double price, int quantity, String availableFrom) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.availableFrom = availableFrom;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    @Override
    public String toString() {
        return name + " - ₹" + price + " (" + quantity + " left)";
    }
}

