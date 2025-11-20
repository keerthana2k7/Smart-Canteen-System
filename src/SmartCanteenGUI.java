import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class SmartCanteenGUI extends JFrame {

    static class MenuItem {
        int id;
        String name;
        double price;

        public MenuItem(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return id + ". " + name + " | Rs " + price;
        }
    }

    static class Order {
        int id;
        String username;
        List<MenuItem> items;
        String slot;
        String status;

        public Order(int id, String username, List<MenuItem> items, String slot) {
            this.id = id;
            this.username = username;
            this.items = items;
            this.slot = slot;
            this.status = "Pending";
        }

        public double getTotal() {
            return items.stream().mapToDouble(m -> m.price).sum();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Bill No: ").append(id).append(" | Items: ");
            for (MenuItem m : items) sb.append(m.name).append(", ");
            if (!items.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append(" | Slot: ").append(slot)
                    .append(" | Total: Rs ").append(getTotal())
                    .append(" | Status: ").append(status);
            return sb.toString();
        }
    }

    static class User {
        String username;
        String password;
        List<Order> orders = new ArrayList<>();

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Map<String, User> users = new HashMap<>();
    private List<MenuItem> menu = new ArrayList<>();
    private List<Order> allOrders = new ArrayList<>();
    private User currentUser;
    private int orderCounter = 1;

    private final String DB_URL = "jdbc:mysql://localhost:3306/";
    private final String DB_NAME = "smart_canteen";
    private final String DB_USER = "root";
    private final String DB_PASS = "Keerthu_2007";

    public SmartCanteenGUI() {
        setTitle("Smart College Canteen System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setupDatabase();
        loadMenuFromDB();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLandingPage(), "Landing");
        mainPanel.add(createUserMenuPanel(), "UserMenu");

        add(mainPanel);
        cardLayout.show(mainPanel, "Landing");
    }

    private void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);

            try (Connection dbConn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
                 Statement dbStmt = dbConn.createStatement()) {

                dbStmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS menu (
                        id INT PRIMARY KEY,
                        name VARCHAR(50),
                        price DOUBLE
                    )
                """);

                dbStmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50),
                        items VARCHAR(255),
                        slot VARCHAR(50),
                        status VARCHAR(20)
                    )
                """);

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database setup failed: " + e.getMessage());
        }
    }

    private void loadMenuFromDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu")) {

            menu.clear();
            while (rs.next()) {
                menu.add(new MenuItem(rs.getInt("id"), rs.getString("name"), rs.getDouble("price")));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading menu from DB. Using default menu.");
            menu.add(new MenuItem(1, "Idli", 10));
            menu.add(new MenuItem(2, "Dosa", 20));
            menu.add(new MenuItem(3, "Pongal", 25));
            menu.add(new MenuItem(4, "Chapathi", 15));
            menu.add(new MenuItem(5, "Tea", 5));
            menu.add(new MenuItem(6, "Coffee", 10));
        }
    }

    private JPanel createLandingPage() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#FFF3E0"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel welcomeLabel = new JLabel("Welcome to Smart College Canteen System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.decode("#BF360C"));
        panel.add(welcomeLabel, gbc);

        JButton userButton = new JButton("User");
        JButton adminButton = new JButton("Admin");
        userButton.setPreferredSize(new Dimension(200, 60));
        adminButton.setPreferredSize(new Dimension(200, 60));
        userButton.setFont(new Font("Arial", Font.BOLD, 18));
        adminButton.setFont(new Font("Arial", Font.BOLD, 18));
        userButton.setBackground(Color.decode("#FF9800"));
        adminButton.setBackground(Color.decode("#FF9800"));
        userButton.setForeground(Color.WHITE);
        adminButton.setForeground(Color.WHITE);

        gbc.gridy = 1; panel.add(userButton, gbc);
        gbc.gridy = 2; panel.add(adminButton, gbc);

        userButton.addActionListener(e -> userLogin());
        adminButton.addActionListener(e -> adminLogin());

        return panel;
    }

    private void userLogin() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(userField);
        loginPanel.add(new JLabel("4-digit Password:"));
        loginPanel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, loginPanel, "User Login", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String uname = userField.getText().trim();
        String pwd = new String(passField.getPassword()).trim();
        if (uname.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter valid credentials!");
            return;
        }

        if (!users.containsKey(uname)) {
            users.put(uname, new User(uname, pwd));
            JOptionPane.showMessageDialog(this, "Registered and logged in!");
        }

        currentUser = users.get(uname);
        if (!currentUser.password.equals(pwd)) {
            JOptionPane.showMessageDialog(this, "Incorrect password!");
            return;
        }

        JOptionPane.showMessageDialog(this, "Login successful! Welcome " + uname);
        cardLayout.show(mainPanel, "UserMenu");
    }

    private void adminLogin() {
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        loginPanel.add(new JLabel("Admin Username:"));
        loginPanel.add(userField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, loginPanel, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String uname = userField.getText().trim();
        String pwd = new String(passField.getPassword()).trim();

        if (uname.equals("Keerthu_357") && pwd.equals("2007")) {
            JOptionPane.showMessageDialog(this, "Admin login successful!");
            showAdminMenu();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid admin credentials!");
        }
    }

    private JPanel createUserMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#E8F5E9"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] buttons = {"View Menu", "Place Order", "View Orders", "Cancel Last Order", "Top Selling Item", "Logout"};
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setPreferredSize(new Dimension(250, 50));
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setBackground(Color.decode("#4CAF50"));
            btn.setForeground(Color.WHITE);
            gbc.gridy = i;
            panel.add(btn, gbc);

            switch (buttons[i]) {
                case "View Menu" -> btn.addActionListener(e -> showMenu());
                case "Place Order" -> btn.addActionListener(e -> placeOrder());
                case "View Orders" -> btn.addActionListener(e -> viewOrders());
                case "Cancel Last Order" -> btn.addActionListener(e -> cancelLastOrder());
                case "Top Selling Item" -> btn.addActionListener(e -> showTopSellingItem());
                case "Logout" -> btn.addActionListener(e -> { currentUser = null; cardLayout.show(mainPanel, "Landing"); });
                case "A" -> btn.add()
            }
        }
        return panel;
    }

    private void showMenu() {
        StringBuilder sb = new StringBuilder("Menu:\n\n");
        for (MenuItem m : menu) sb.append(m.toString()).append("\n");
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void placeOrder() {
        showMenu();
        String input = JOptionPane.showInputDialog(this, "Enter item IDs (comma separated):");
        if (input == null || input.isEmpty()) return;

        List<MenuItem> items = new ArrayList<>();
        for (String p : input.split(",")) {
            try {
                int id = Integer.parseInt(p.trim());
                menu.stream().filter(m -> m.id == id).findFirst().ifPresent(items::add);
            } catch (NumberFormatException ignored) {}
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid items selected!");
            return;
        }

        String slot = JOptionPane.showInputDialog(this, "Enter preferred time slot:");
        if (slot == null || slot.isEmpty()) return;

        Order order = new Order(orderCounter++, currentUser.username, items, slot);
        currentUser.orders.add(order);
        allOrders.add(order);

        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO orders (username, items, slot, status) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, order.username);
            ps.setString(2, items.stream().map(m -> String.valueOf(m.id)).reduce((a, b) -> a + "," + b).orElse(""));
            ps.setString(3, slot);
            ps.setString(4, order.status);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, " Error saving order to DB: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(this, "✅ Order booked successfully!\n" + order.toString());
    }

    private void viewOrders() {
        if (currentUser.orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders found!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Order o : currentUser.orders) sb.append(o.toString()).append("\n\n");
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Your Orders", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelLastOrder() {
        if (currentUser.orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders to cancel!");
            return;
        }
        Order lastOrder = currentUser.orders.get(currentUser.orders.size() - 1);
        lastOrder.status = "Cancelled";

        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement("UPDATE orders SET status='Cancelled' WHERE id=?")) {
            ps.setInt(1, lastOrder.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating order in DB: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Last order (Bill No: " + lastOrder.id + ") cancelled!");
    }

    private void showTopSellingItem() {
        Map<String, Integer> countMap = new HashMap<>();
        for (Order o : allOrders) {
            if (!o.status.equals("Cancelled")) {
                for (MenuItem m : o.items) countMap.put(m.name, countMap.getOrDefault(m.name, 0) + 1);
            }
        }

        if (countMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sales yet!");
            return;
        }

        String topItem = Collections.max(countMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        int sold = countMap.get(topItem);
        JOptionPane.showMessageDialog(this, "🔥 Top Selling Item: " + topItem + " - Sold " + sold + " times");
    }

    private void showAdminMenu() {
        String[] options = {"View Menu", "View All Orders", "View Cancelled Orders",
                "Add Item", "Update Item", "Delete Item", "Top Selling Item", "Exit"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, "Admin Menu", "Admin",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (choice == -1 || choice == 7) break;

            try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASS)) {

                switch (choice) {
                    case 0 -> showMenu();
                    case 1 -> showAllOrders(conn, false);
                    case 2 -> showAllOrders(conn, true);
                    case 3 -> addItem(conn);
                    case 4 -> updateItem(conn);
                    case 5 -> deleteItem(conn);
                    case 6 -> showTopSellingItem();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
            }
        }
    }

    private void showAllOrders(Connection conn, boolean cancelledOnly) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (Order o : allOrders) {
            if (cancelledOnly && !o.status.equals("Cancelled")) continue;
            if (!cancelledOnly && o.status.equals("Cancelled")) continue;
            sb.append(o.toString()).append("\n\n");
        }
        String title = cancelledOnly ? "Cancelled Orders" : "All Orders";
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void addItem(Connection conn) throws SQLException {
        String name = JOptionPane.showInputDialog("Enter item name:");
        if (name == null || name.isEmpty()) return;
        String priceStr = JOptionPane.showInputDialog("Enter price:");
        if (priceStr == null || priceStr.isEmpty()) return;
        double price = Double.parseDouble(priceStr.trim());

        int newId = menu.stream().mapToInt(m -> m.id).max().orElse(0) + 1;
        menu.add(new MenuItem(newId, name, price));

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO menu (id,name,price) VALUES (?,?,?)")) {
            ps.setInt(1, newId);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Item added!");
    }

    private void updateItem(Connection conn) throws SQLException {
        String oldName = JOptionPane.showInputDialog("Enter old item name:");
        if (oldName == null || oldName.isEmpty()) return;
        String newName = JOptionPane.showInputDialog("Enter new item name:");
        if (newName == null || newName.isEmpty()) return;
        String priceStr = JOptionPane.showInputDialog("Enter new price:");
        if (priceStr == null || priceStr.isEmpty()) return;
        double newPrice = Double.parseDouble(priceStr.trim());

        boolean found = false;
        for (MenuItem m : menu) {
            if (m.name.equalsIgnoreCase(oldName)) {
                m.name = newName;
                m.price = newPrice;
                found = true;

                try (PreparedStatement ps = conn.prepareStatement("UPDATE menu SET name=?, price=? WHERE id=?")) {
                    ps.setString(1, newName);
                    ps.setDouble(2, newPrice);
                    ps.setInt(3, m.id);
                    ps.executeUpdate();
                }
            }
        }

        JOptionPane.showMessageDialog(this, found ? "Item updated!" : "Item not found!");
    }

    private void deleteItem(Connection conn) throws SQLException {
        String delName = JOptionPane.showInputDialog("Enter item name to delete:");
        if (delName == null || delName.isEmpty()) return;

        menu.removeIf(m -> m.name.equalsIgnoreCase(delName));
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM menu WHERE name=?")) {
            ps.setString(1, delName);
            ps.executeUpdate();
        }
        JOptionPane.showMessageDialog(this, "Item deleted!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartCanteenGUI().setVisible(true));
    }
}
