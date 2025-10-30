import javax.swing.*;
import java.awt.*;

public class MainUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Smart Canteen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel title = new JLabel("SMART CANTEEN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton userLogin = new JButton("User Login");
        JButton register = new JButton("Register");
        JButton adminLogin = new JButton("Admin Login");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.add(title);
        panel.add(userLogin);
        panel.add(register);
        panel.add(adminLogin);

        frame.add(panel);
        frame.setVisible(true);

        // when you click buttons
        userLogin.addActionListener(e -> {
            frame.dispose();
            UserLoginUI.main(null); // open login page
        });

        register.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Registration Page Coming Soon!");
        });

        adminLogin.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Admin Login Coming Soon!");
        });
    }
}
