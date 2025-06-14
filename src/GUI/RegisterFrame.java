package GUI;

import service.UserService;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterFrame extends JFrame {
    // Các trường nhập liệu
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JTextField txtEmail = new JTextField(20);
    private JButton btnRegister = new JButton("Register");

    private UserService userService;

    // GradientPanel cho phần header
    class GradientPanel extends JPanel {
        private Color color1 = new Color(70, 130, 180);   // Xanh đậm
        private Color color2 = new Color(135, 206, 235);    // Xanh nhạt

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    public RegisterFrame() {
        userService = new UserService();
        // Kích hoạt FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tùy chỉnh các thuộc tính giao diện
        UIManager.put("Button.arc", 999);
        UIManager.put("TextComponent.arc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.focusColor", new Color(0x007FFF));

        setTitle("Chat App - Register");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính với BorderLayout và border
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Panel Header: dùng GradientPanel để vẽ nền gradient
        GradientPanel headerPanel = new GradientPanel();
        headerPanel.setPreferredSize(new Dimension(400, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel lblTitle = new JLabel("Register Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel Center: chứa form đăng ký, dùng GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Label Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        centerPanel.add(new JLabel("Username:"), gbc);

        // Row 1: txtUsername
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        centerPanel.add(txtUsername, gbc);

        // Row 2: Label Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        centerPanel.add(new JLabel("Password:"), gbc);

        // Row 2: txtPassword
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        centerPanel.add(txtPassword, gbc);

        // Row 3: Label Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        centerPanel.add(new JLabel("Email:"), gbc);

        // Row 3: txtEmail
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        centerPanel.add(txtEmail, gbc);

        // Panel Bottom: chứa nút Register
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(btnRegister);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Hỗ trợ nhấn Enter trong trường Email để đăng ký
        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    doRegister();
                }
            }
        });

        // Sự kiện nút Register
        btnRegister.addActionListener(e -> doRegister());
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String email = txtEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào
        if (!validateInput(username, password, email)) {
            return;
        }

        // Thực hiện đăng ký qua UserService
        boolean isRegistered = userService.registerUser(username, password, email);

        if (isRegistered) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            dispose(); // Đóng cửa sổ đăng ký
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username or email may already exist.");
        }
    }

    private boolean validateInput(String username, String password, String email) {
        if (!username.matches("[a-zA-Z0-9]{3,20}")) {
            showError("Username must be 3-20 alphanumeric characters");
            return false;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return false; 
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
