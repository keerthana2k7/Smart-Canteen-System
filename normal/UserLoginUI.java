import javax.swing.*;
import java.awt.*;

public class UserLoginUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);

        JTextField username = new JTextField(15);
        JPasswordField password = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel(""));
        panel.add(loginBtn);

        frame.add(panel);
        frame.setVisible(true);

        loginBtn.addActionListener(e -> {
            String user = username.getText();
            String pass = new String(password.getPassword());

            Login login = new Login();
            boolean success = login.login(user, pass);

            if (success) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Login Failed!");
            }
        });
    }
}
