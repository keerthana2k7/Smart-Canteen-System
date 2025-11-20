import java.sql.*;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class CanteenSystem {
    private List<MenuItem> menuItems = new ArrayList<>();

    public CanteenSystem() {
        loadMenuFromDatabase();
    }

    public void loadMenuFromDatabase() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("Cannot load menu because database connection is null.");
            return;
        }

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu");
            while (rs.next()) {

            }
        } catch (SQLException e) {
            System.out.println("Error loading menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showMenu() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu")) {

            System.out.println("\n--- Available Menu ---");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                String status;

                if (quantity <= 0) {
                    LocalTime nextAvailable = LocalTime.now().plusHours(1).plusMinutes(30);
                    String nextTime = nextAvailable.format(formatter);
                    status = "SOLD OUT (Available at " + nextTime + ")";
                } else {
                    status = "Available (" + quantity + " left)";
                }

                System.out.printf("%-3d %-20s Rs %.2f | %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        status);
            }

        } catch (Exception e) {
            System.out.println("Error loading menu: " + e.getMessage());
        }
    }

    public void placeOrder(String username, ArrayList<Integer> itemIds, String selectedSlot) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            double total = 0;
            StringBuilder itemNames = new StringBuilder();

            for (int itemId : itemIds) {
                PreparedStatement ps = conn.prepareStatement("SELECT name, price, quantity FROM menu WHERE id = ?");
                ps.setInt(1, itemId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String itemName = rs.getString("name");
                    double price = rs.getDouble("price");
                    int qty = rs.getInt("quantity");

                    if (qty > 0) {
                        total += price;
                        itemNames.append(itemName).append(", ");
                        updateQuantityAfterOrder(itemId, 1);
                    } else {
                        System.out.println("❌ Item '" + itemName + "' is sold out.");
                        markItemSoldOut(itemId);
                        return;
                    }
                }
                ps.close();
            }

            if (itemNames.length() > 0) {
                itemNames.setLength(itemNames.length() - 2);
            }

            PreparedStatement psOrder = conn.prepareStatement(
                    "INSERT INTO orders (username, items, total, time_slot, order_time) VALUES (?, ?, ?, ?, NOW())",
                    Statement.RETURN_GENERATED_KEYS);

            psOrder.setString(1, username);
            psOrder.setString(2, itemNames.toString());
            psOrder.setDouble(3, total);
            psOrder.setString(4, selectedSlot);

            psOrder.executeUpdate();

            ResultSet rsKeys = psOrder.getGeneratedKeys();
            if (rsKeys.next()) {
                int billNo = rsKeys.getInt(1);
                System.out.println("✅ Order booked successfully! Your Bill No: " + billNo + " | Total: Rs " + total);
            } else {
                System.out.println("✅ Order booked successfully! Total: Rs " + total);
            }

            psOrder.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuantityAfterOrder(int itemId, int orderedQty) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE menu SET quantity = quantity - ? WHERE id = ? AND quantity >= ?")) {

            pstmt.setInt(1, orderedQty);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, orderedQty);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated <= 0) {

                System.out.println("Not enough stock available.");
            }

            PreparedStatement check = conn.prepareStatement("SELECT quantity FROM menu WHERE id = ?");
            check.setInt(1, itemId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getInt("quantity") == 0) {
                markItemSoldOut(itemId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markItemSoldOut(int itemId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE menu SET available_from = ADDTIME(CURTIME(), '02:00:00') WHERE id = ?")) {
            pstmt.setInt(1, itemId);
            pstmt.executeUpdate();
            System.out.println("⚠️ Item sold out. It will be available again after 2 hours.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showOrders(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT bill_no, items, time_slot, total, order_time, status " +
                             "FROM orders WHERE username = ? ORDER BY bill_no DESC")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Your Orders ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                String status = rs.getString("status");
                System.out.println("Bill No: " + rs.getInt("bill_no") +
                        " | Items: " + rs.getString("items") +
                        " | Slot: " + rs.getString("time_slot") +
                        " | Total: Rs " + rs.getDouble("total") +
                        " | Time: " + rs.getTimestamp("order_time") +
                        " | Status: " + status);
            }

            if (!found) {
                System.out.println("No orders found yet.");
            }

        } catch (Exception e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }

    public void cancelLastOrder(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT bill_no FROM orders WHERE username = ? ORDER BY bill_no DESC LIMIT 1")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int lastBillNo = rs.getInt("bill_no");

                try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE orders SET status = 'Cancelled' WHERE bill_no = ?")) {
                    update.setInt(1, lastBillNo);
                    int rows = update.executeUpdate();

                    if (rows > 0)
                        System.out.println("? Last order (Bill No: " + lastBillNo + ") marked as Cancelled.");
                    else
                        System.out.println("No orders found to cancel.");
                }
            } else {
                System.out.println("No previous orders found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTopSellingItem() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT items FROM orders WHERE items IS NOT NULL AND TRIM(items) <> ''")) {

            ResultSet rs = ps.executeQuery();

            HashMap<String, Integer> itemSales = new HashMap<>();

            while (rs.next()) {
                String itemsStr = rs.getString("items");
                if (itemsStr != null && !itemsStr.isEmpty()) {
                    String[] items = itemsStr.split(",");
                    for (String item : items) {
                        String cleanItem = item.trim();
                        if (!cleanItem.isEmpty()) {
                            itemSales.put(cleanItem, itemSales.getOrDefault(cleanItem, 0) + 1);
                        }
                    }
                }
            }

            if (itemSales.isEmpty()) {
                System.out.println("\n⚠️ No sales data found yet!");
                return;
            }

            // Find top-selling item
            String topItem = null;
            int maxSales = 0;

            for (Map.Entry<String, Integer> entry : itemSales.entrySet()) {
                if (entry.getValue() > maxSales) {
                    maxSales = entry.getValue();
                    topItem = entry.getKey();
                }
            }

            System.out.println("\n🔥 Top Selling Item 🔥");
            System.out.println(topItem + " - Sold " + maxSales + " times");

        } catch (SQLException e) {
            System.out.println("Error calculating top selling item: " + e.getMessage());
        }
    }

    public void addMenuItem(String name, double price) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO menu (name, price, quantity) VALUES (?, ?, 10)")) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.executeUpdate();
            System.out.println("✅ New item added: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMenuItem(String oldName, String newName, double newPrice) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE menu SET name = ?, price = ? WHERE name = ?")) {

            ps.setString(1, newName);
            ps.setDouble(2, newPrice);
            ps.setString(3, oldName);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println(" Menu item updated successfully: " + oldName + " → " + newName);
            } else {
                System.out.println(" The item '" + oldName + "' was not found in the menu.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating menu item: " + e.getMessage());
        }
    }


    public void deleteMenuItem(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM menu WHERE name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("✅ Menu item deleted: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showAllOrders() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM orders")) {

            System.out.println("\n--- All Orders ---");
            while (rs.next()) {
                System.out.println("Bill No: " + rs.getInt("bill_no") +
                        " | User: " + rs.getString("username") +
                        " | Items: " + rs.getString("items") +
                        " | Total: Rs " + rs.getDouble("total") +
                        " | Slot: " + rs.getString("time_slot") +
                        " | Time: " + rs.getTimestamp("order_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void showCancelledOrders() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT bill_no, username, items, time_slot, total FROM orders WHERE status = 'Cancelled'")) {

            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Cancelled Orders ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Bill No: " + rs.getInt("bill_no") +
                        " | User: " + rs.getString("username") +
                        " | Items: " + rs.getString("items") +
                        " | Slot: " + rs.getString("time_slot") +
                        " | Total: Rs " + rs.getDouble("total"));
            }

            if (!found) {
                System.out.println("No cancelled orders found.");
            }

        } catch (Exception e) {
            System.out.println("Error loading cancelled orders: " + e.getMessage());
        }
    }

}

