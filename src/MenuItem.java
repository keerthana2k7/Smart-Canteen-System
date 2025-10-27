public class MenuItem {
    private int id;
    private String name;
    private double price;
    private int salesCount;

    // Constructor with ID
    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.salesCount = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getSalesCount() {
        return salesCount;
    }

    // Increase sales count when item is ordered
    public void incrementSales() {
        salesCount++;
    }

    // Used by admin to update price or name
    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Display format for menu
    public void displayItem() {
        System.out.printf("%-3d %-20s Rs %.2f (Sold: %d)\n", id, name, price, salesCount);
    }
}
