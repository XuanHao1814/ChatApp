package GUI;

import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JButton btnLogin = new JButton("Login");
    private JButton btnRegister = new JButton("Register");

    public LoginFrame() {
        setTitle("Chat App - Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);
        panel.add(new JLabel());
        panel.add(btnLogin);
        panel.add(new JLabel());
        panel.add(btnRegister);

        btnLogin.addActionListener(e -> {
            UserService userService = new UserService();
            User user = userService.login(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword())
            );

            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                new ChatFrame(user).setVisible(true); // Mở giao diện chat
                dispose(); // Đóng cửa sổ đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true); // Mở cửa sổ đăng ký
        });

        add(panel);
    }
}