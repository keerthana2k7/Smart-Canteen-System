import java.util.*;

public class MenuItem {
    private int id;
    private String name;
    private double price;
    private int salesCount;

    public MenuItem(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.salesCount = 0;
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

    public int getSalesCount() {
        return salesCount;
    }

    public void incrementSales() {
        salesCount++;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void displayItem() {
        System.out.printf("%-3d %-20s Rs %.2f (Sold: %d)%n", id, name, price, salesCount);
    }

    public static List<MenuItem> getDefaultMenu() {
        List<MenuItem> defaultMenu = new ArrayList<>();
        defaultMenu.add(new MenuItem(1, "Idly", 20.0));
        defaultMenu.add(new MenuItem(2, "Dosa", 30.0));
        defaultMenu.add(new MenuItem(3, "Vada", 10.0));
        defaultMenu.add(new MenuItem(4, "Poori", 35.0));
        defaultMenu.add(new MenuItem(6, "Coffee", 20.0));
        return defaultMenu;
    }
}
