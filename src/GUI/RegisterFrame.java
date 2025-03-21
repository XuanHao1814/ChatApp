package GUI;

import DAO.UserDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JTextField txtEmail = new JTextField(20);
    private JButton btnRegister = new JButton("Register");

    public RegisterFrame() {
        setTitle("Chat App - Register");
        setSize(350, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel());
        panel.add(btnRegister);

        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String email = txtEmail.getText();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            UserDAO userDAO = new UserDAO();
            boolean isRegistered = userDAO.registerUser(username, password, email);

            if (isRegistered) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
                dispose(); // Đóng cửa sổ đăng ký
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username or email may already exist.");
            }
        });

        add(panel);
    }
}