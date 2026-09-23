# Smart Canteen System

A Java desktop application built to modernize college cafeteria operations by automating order placement, meal pre-ordering, queue dispatching, and transaction settlement through a relational database.

---

## 1. Project Overview

The **Smart Canteen System** is a software solution designed to streamline cafeteria dining workflows in educational institutions. Developed using Java, Java Swing for the graphical user interface, and MySQL via JDBC for backend persistence, the application connects students and canteen administrators in a shared, synchronized ordering ecosystem.

Students can browse an interactive menu, pre-order meals for specific 30-minute break slots, maintain a digital balance, and avoid peak-hour counter queues. Canteen administrators gain tools to manage catalog pricing, restock inventory items, view incoming order queues in real time, and audit daily sales reports.

---

## 2. Problem Statement

During scheduled lunch and tea breaks, campus cafeterias face extreme congestion:
- **Long Waiting Times**: Manual order placement and paper-ticket issuance create bottlenecks at the counter.
- **Queue Disorganization**: Unordered counter rushes cause delays and order misplacement.
- **Stock Discrepancies**: Canteen staff cannot easily anticipate meal demand, leading to food waste or mid-break stockouts.
- **Cash Management Overhead**: Manual cash exchanges slow down transaction throughput and increase reconciliation errors by the end of the day.

The Smart Canteen System addresses these issues by introducing slot-based pre-ordering, linked-list order queues, and direct transactional database persistence.

---

## 3. Features

### Student Portal
- **User Authentication**: Secure user registration and login verification.
- **Dynamic Menu Catalog**: Categorized view of available food items, pricing, and live stock statuses.
- **Time-Slot Pre-Ordering**: Select from 16 thirty-minute delivery intervals (from 09:00 AM to 05:00 PM), automatically validated against the current system clock.
- **Cart Management**: Add, modify quantity, or remove items with automatic subtotal and tax calculation.
- **Digital Payment Simulation**: Fast settlement via digital wallet / card payments with automated receipt generation.
- **Order Tracking**: Real-time status tracking from `PLACED` to `PREPARING` and `READY_FOR_PICKUP`.

### Admin Management Portal
- **Administrative Access**: Password-protected administrator console.
- **Catalog & Inventory Control**: Add new items, update unit prices, adjust quantities, and toggle availability.
- **Live Queue Monitoring**: View pending, in-progress, and completed orders in real time.
- **Sales Analytics & History**: Audit transaction logs, revenue breakdown by slot, and popular item trends.

---

## 4. Technical Architecture & Tech Stack

- **Language**: Java (JDK 17+)
- **User Interface**: Java Swing (`JFrame`, `JPanel`, `JTable`, `CardLayout`)
- **Persistence Layer**: JDBC (`java.sql.Connection`, `PreparedStatement`, `ResultSet`)
- **Database**: MySQL 8.0+
- **Core Concepts Applied**:
  - **Object-Oriented Programming (OOP)**: Encapsulation (`MenuItem`, `Order`, `Payment`), Inheritance, Polymorphism.
  - **Data Structures**: Linked-list order queues for FIFO fulfillment and dynamic cart collections.
  - **Relational Integrity**: Foreign key constraints linking user credentials, order records, and line-item details.

---

## 5. Database Schema

The system utilizes a relational MySQL database named `smart_canteen`:

- `users`: Stores customer accounts (`user_id`, `name`, `email`, `password_hash`, `wallet_balance`).
- `admins`: Stores administrator credentials (`admin_id`, `username`, `password_hash`).
- `menu_items`: Maintains the current catalog (`item_id`, `item_name`, `category`, `price`, `stock_quantity`, `is_available`).
- `orders`: Records top-level order transactions (`order_id`, `user_id`, `total_amount`, `time_slot`, `order_status`, `created_at`).
- `order_items`: Maps individual items to orders (`order_item_id`, `order_id`, `item_id`, `quantity`, `unit_price`).
- `payments`: Logs transaction receipts (`payment_id`, `order_id`, `payment_method`, `amount_paid`, `status`, `timestamp`).

---

## 6. Installation & Prerequisites

### Prerequisites
1. **Java Development Kit (JDK 17 or later)** installed and configured in your `PATH`.
2. **MySQL Server 8.0+** running locally on port `3306`.
3. **MySQL Connector/J (JDBC driver)** jar file (e.g. `mysql-connector-j-9.x.jar`).

### Database Setup
1. Launch MySQL CLI or MySQL Workbench:
   ```sql
   CREATE DATABASE smart_canteen;
   USE smart_canteen;
   ```
2. Configure credentials in `src/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/smart_canteen";
   private static final String USER = "your_mysql_username";
   private static final String PASSWORD = "your_mysql_password";
   ```

---

## 7. How to Run

### Option A: Using IntelliJ IDEA / Eclipse
1. Open the project folder in your IDE.
2. Ensure the MySQL Connector `.jar` is added to the project libraries / classpath (`File > Project Structure > Libraries`).
3. Locate `src/Main.java` or `src/SmartCanteenGUI.java`.
4. Right-click and choose **Run 'SmartCanteenGUI.main()'**.

### Option B: Using the Command Line
```bash
# Compile source files with the MySQL JDBC driver in classpath
javac -cp ".;lib/mysql-connector-j-9.x.jar" src/*.java -d bin

# Launch the desktop GUI
java -cp "bin;lib/mysql-connector-j-9.x.jar" SmartCanteenGUI
```

---

## 8. Future Improvements

- **QR Code Pickup Verification**: Dynamic QR code tokens generated upon checkout for contactless verification at the counter.
- **Push & SMS Alerts**: Live notifications when order status moves to preparation and pickup.
- **RESTful Backend Migration**: Spring Boot REST API layer to support companion mobile applications.
- **Automated Inventory Alerts**: Automated warnings to suppliers when raw ingredients cross minimum threshold boundaries.

---

## Author

**Keerthana R**  
*Computer Science and Engineering, Kongu Engineering College*  
[GitHub Profile](https://github.com/keerthana2k7) • [LinkedIn](https://www.linkedin.com/in/keerthana-r/)
