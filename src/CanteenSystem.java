import java.sql.*;
import java.util.*;

public class CanteenSystem {
    private final Map<Integer, String> menuItems = new LinkedHashMap<>();
    private final Map<Integer, Double> prices = new LinkedHashMap<>();
    private int lastBillNo = -1;

    public CanteenSystem() {
        loadMenuFromDatabase();
    }

    // ✅ Load menu from the database
    private void loadMenuFromDatabase() {
        menuItems.clear();
        prices.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM menu");
             ResultSet rs = ps.executeQuery()) {

            int id = 1;
            while (rs.next()) {
                menuItems.put(id, rs.getString("name"));
                prices.put(id, rs.getDouble("price"));
                id++;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading menu: " + e.getMessage());
        }
    }

    // ✅ Display menu
    public void showMenu() {
        System.out.println("\n===== MENU =====");
        for (int id : menuItems.keySet()) {
            System.out.println(id + ". " + menuItems.get(id) + " - ₹" + prices.get(id));
        }
    }

    // ✅ Place order (user)
    public void placeOrder(String username, List<Integer> itemIds, String timeSlot) {
        double total = 0;
        StringBuilder orderedItems = new StringBuilder();

        for (int id : itemIds) {
            if (menuItems.containsKey(id)) {
                orderedItems.append(menuItems.get(id)).append(", ");
                total += prices.get(id);
                incrementItemSales(menuItems.get(id)); // update sales count
            } else {
                System.out.println("⚠️ Invalid item ID: " + id);
            }
        }

        if (orderedItems.length() > 0)
            orderedItems.setLength(orderedItems.length() - 2); // remove last comma

        int billNo = saveOrderToDatabase(username, orderedItems.toString(), timeSlot, total);
        if (billNo != -1) {
            lastBillNo = billNo;
            System.out.println("\n✅ Order placed successfully!");
            System.out.println("🧾 Bill No: " + billNo);
            System.out.println("Items: " + orderedItems);
            System.out.println("Pickup Time Slot: " + timeSlot);
            System.out.println("Total: ₹" + total);
        } else {
            System.out.println("❌ Failed to place order. Try again.");
        }
    }

    // ✅ Save order to database
    private int saveOrderToDatabase(String username, String items, String timeSlot, double total) {
        int billNo = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO orders (username, items, time_slot, total) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, items);
            ps.setString(3, timeSlot);
            ps.setDouble(4, total);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    billNo = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error saving order: " + e.getMessage());
        }
        return billNo;
    }

    // ✅ Increment item sales in database
    private void incrementItemSales(String itemName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE menu SET sales = sales + 1 WHERE name = ?")) {
            ps.setString(1, itemName);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Optional: ignore if column 'sales' doesn't exist
        }
    }

    // ✅ Cancel order — move to cancelled_orders
    public void cancelOrder(int billNo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSql = "INSERT INTO cancelled_orders (bill_no, username, items, time_slot, total) " +
                    "SELECT bill_no, username, items, time_slot, total FROM orders WHERE bill_no = ?";
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, billNo);
                int inserted = insertPs.executeUpdate();

                if (inserted > 0) {
                    try (PreparedStatement deletePs = conn.prepareStatement("DELETE FROM orders WHERE bill_no = ?")) {
                        deletePs.setInt(1, billNo);
                        deletePs.executeUpdate();
                    }
                    System.out.println("\n❌ Order with Bill No " + billNo + " cancelled successfully!");
                } else {
                    System.out.println("⚠️ No such order found!");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error cancelling order: " + e.getMessage());
        }
    }

    // ✅ Top-selling item (for User/Admin)
    public void showTopSellingItem() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT items FROM orders")) {

            Map<String, Integer> countMap = new HashMap<>();
            while (rs.next()) {
                String[] ordered = rs.getString("items").split(",\\s*");
                for (String item : ordered) {
                    countMap.put(item, countMap.getOrDefault(item, 0) + 1);
                }
            }

            if (countMap.isEmpty()) {
                System.out.println("⚠️ No sales data available yet!");
                return;
            }

            String topItem = Collections.max(countMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            int count = countMap.get(topItem);
            System.out.println("\n🔥 Top-Selling Item: " + topItem + " (" + count + " sold)");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching top item: " + e.getMessage());
        }
    }

    // ✅ Admin: View all active orders
    public void showAllOrders() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM orders")) {

            System.out.println("\n📋 --- All Active Orders ---");
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.println("Bill No: " + rs.getInt("bill_no"));
                System.out.println("User: " + rs.getString("username"));
                System.out.println("Items: " + rs.getString("items"));
                System.out.println("Time Slot: " + rs.getString("time_slot"));
                System.out.println("Total: ₹" + rs.getDouble("total"));
                System.out.println("---------------------------");
            }
            if (!hasOrders) System.out.println("⚠️ No active orders found!");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching orders: " + e.getMessage());
        }
    }

    // ✅ Admin: View cancelled orders
    public void showCancelledOrders() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cancelled_orders")) {

            System.out.println("\n🚫 --- Cancelled Orders ---");
            boolean hasCancelled = false;
            while (rs.next()) {
                hasCancelled = true;
                System.out.println("Bill No: " + rs.getInt("bill_no"));
                System.out.println("User: " + rs.getString("username"));
                System.out.println("Items: " + rs.getString("items"));
                System.out.println("Time Slot: " + rs.getString("time_slot"));
                System.out.println("Total: ₹" + rs.getDouble("total"));
                System.out.println("---------------------------");
            }
            if (!hasCancelled) System.out.println("⚠️ No cancelled orders found!");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching cancelled orders: " + e.getMessage());
        }
    }

    // ✅ Admin: Add, update, delete menu
    public void addMenuItem(String name, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO menu (name, price, sales) VALUES (?, ?, 0)")) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.executeUpdate();
            System.out.println("✅ Item added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding item: " + e.getMessage());
        }
        loadMenuFromDatabase();
    }

    public void updateMenuItem(String oldName, String newName, double newPrice) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE menu SET name = ?, price = ? WHERE name = ?")) {
            ps.setString(1, newName);
            ps.setDouble(2, newPrice);
            ps.setString(3, oldName);
            ps.executeUpdate();
            System.out.println("✅ Item updated successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error updating item: " + e.getMessage());
        }
        loadMenuFromDatabase();
    }

    public void deleteMenuItem(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM menu WHERE name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("✅ Item deleted successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error deleting item: " + e.getMessage());
        }
        loadMenuFromDatabase();
    }
}
