import java.sql.*;
import java.util.*;

public class CanteenSystem {
    private List<MenuItem> menuItems = new ArrayList<>();

    public CanteenSystem() {
        loadMenuFromDatabase();
    }

    public void loadMenuFromDatabase() {
        menuItems.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu")) {

            while (rs.next()) {
                  int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                menuItems.add(new MenuItem(id, name, price));


            }

        if (menuItems.isEmpty()) {
            menuItems.addAll(MenuItem.getDefaultMenu());
        }


        } catch (SQLException e) {
            System.out.println(" Error loading menu: " + e.getMessage());
            menuItems.addAll(MenuItem.getDefaultMenu());
        }
    }
    
public void addMenuItem(String name, double price) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement("INSERT INTO menu (name, price, sales) VALUES (?, ?, 0)")) {

        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.executeUpdate();
        System.out.println(" Item added successfully!");
        loadMenuFromDatabase();

    } catch (SQLException e) {
        System.out.println(" Error adding item: " + e.getMessage());
    }
}


public void updateMenuItem(String oldName, String newName, double newPrice) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement("UPDATE menu SET name = ?, price = ? WHERE name = ?")) {

        ps.setString(1, newName);
        ps.setDouble(2, newPrice);
        ps.setString(3, oldName);
        ps.executeUpdate();
        System.out.println(" Item updated successfully!");
        loadMenuFromDatabase();

    } catch (SQLException e) {
        System.out.println(" Error updating item: " + e.getMessage());
    }
}

public void deleteMenuItem(String name) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement("DELETE FROM menu WHERE name = ?")) {

        ps.setString(1, name);
        ps.executeUpdate();
        System.out.println(" Item deleted successfully!");
        loadMenuFromDatabase();

    } catch (SQLException e) {
        System.out.println(" Error deleting item: " + e.getMessage());
    }
}

public void showAllOrders() {
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {

        System.out.println("\n--- ALL ORDERS ---");
        while (rs.next()) {
            System.out.printf("User: %s | Item: %s | Qty: %d | Time Slot: %s%n",
                    rs.getString("username"),
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    rs.getString("time_slot"));
        }
    } catch (SQLException e) {
        System.out.println(" Error fetching orders: " + e.getMessage());
    }
}

public void showCancelledOrders() {
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM cancelled_orders")) {

        System.out.println("\n--- CANCELLED ORDERS ---");
        while (rs.next()) {
            System.out.printf("User: %s | Item: %s | Time: %s%n",
                    rs.getString("username"),
                    rs.getString("item_name"),
                    rs.getString("cancel_time"));
        }
    } catch (SQLException e) {
        System.out.println(" Error fetching cancelled orders: " + e.getMessage());
    }
}

public void showTopSellingItem() {
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT name, sales FROM menu ORDER BY sales DESC LIMIT 1")) {

        if (rs.next()) {
            System.out.println("\n Top Selling Item ");
            System.out.printf("%s - Sold %d times%n", rs.getString("name"), rs.getInt("sales"));
        } else {
            System.out.println("No sales data yet!");
        }

    } catch (SQLException e) {
        System.out.println(" Error showing top item: " + e.getMessage());
    }
}

    public void showMenu() {
        loadMenuFromDatabase(); 
        System.out.println("\n --- MENU ITEMS ---");
        if (menuItems.isEmpty()) {
            System.out.println("No items available in the menu.");
        } else {
            for (int i = 0; i < menuItems.size(); i++) {
                MenuItem item = menuItems.get(i);
                System.out.printf("%d. %s - ₹%.2f%n", i + 1, item.getName(), item.getPrice());
            }
        }
    }

    public void placeOrder(String username, List<Integer> itemIds, String selectedSlot) {
        if (itemIds.isEmpty()) {
            System.out.println(" No items selected!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO orders (username, items, slot, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement ps = conn.prepareStatement(query);

            List<String> orderedItems = new ArrayList<>();
            for (int id : itemIds) {
                if (id > 0 && id <= menuItems.size()) {
                    orderedItems.add(menuItems.get(id - 1).getName());
                }
            }

            ps.setString(1, username);
            ps.setString(2, String.join(", ", orderedItems));
            ps.setString(3, selectedSlot);
            ps.executeUpdate();
            System.out.println(" Order placed successfully for " + username + "!");

        } catch (SQLException e) {
            System.out.println(" Error placing order: " + e.getMessage());
        }
    }

    public void showOrders(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n --- Your Orders ---");
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.println("Order ID: " + rs.getInt("id"));
                System.out.println("Items: " + rs.getString("items"));
                System.out.println("Slot: " + rs.getString("slot"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("---------------------");
            }
            if (!hasOrders) System.out.println(" No orders found.");

        } catch (SQLException e) {
            System.out.println(" Error fetching orders: " + e.getMessage());
        }
    }

    public void cancelLastOrder(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id FROM orders WHERE username = ? ORDER BY id DESC LIMIT 1")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int orderId = rs.getInt("id");
                PreparedStatement psDelete = conn.prepareStatement("DELETE FROM orders WHERE id = ?");
                psDelete.setInt(1, orderId);
                psDelete.executeUpdate();
                System.out.println(" Last order cancelled successfully!");
            } else {
                System.out.println(" No orders found to cancel.");
            }

        } catch (SQLException e) {
            System.out.println(" Error cancelling order: " + e.getMessage());
        }
    }

    public void addMenuItemAdmin(String name, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO menu (name, price, sales) VALUES (?, ?, 0)")) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.executeUpdate();
            System.out.println(" Item added successfully!");

            loadMenuFromDatabase(); 
        } catch (SQLException e) {
            System.out.println(" Error adding item: " + e.getMessage());
        }
    }

    
    public void updateMenuItemAdmin(String oldName, String newName, double newPrice) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE menu SET name = ?, price = ? WHERE name = ?")) {

            ps.setString(1, newName);
            ps.setDouble(2, newPrice);
            ps.setString(3, oldName);
            ps.executeUpdate();
            System.out.println(" Item updated successfully!");

            loadMenuFromDatabase(); 
        } catch (SQLException e) {
            System.out.println(" Error updating item: " + e.getMessage());
        }
    }

    public void deleteMenuItemAdmin(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM menu WHERE name = ?")) {

            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println(" Item deleted successfully!");

            loadMenuFromDatabase(); 
        } catch (SQLException e) {
            System.out.println(" Error deleting item: " + e.getMessage());
        }
    }
}
