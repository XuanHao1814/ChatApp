package GUI;

import service.UserService;
import model.User;
import com.formdev.flatlaf.FlatLightLaf;
import database.DatabaseConnection;  // Add this import

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginFrame extends JFrame {
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        // Khởi tạo các components trước
        userService = new UserService();
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Style buttons
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(135, 206, 235));
        registerButton.setForeground(Color.WHITE);

        // Kích hoạt FlatLaf (Light)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tùy chỉnh bo góc, focus, v.v.
        UIManager.put("Button.arc", 999);
        UIManager.put("TextComponent.arc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.focusColor", new Color(0x007FFF));

        setTitle("Chat App - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính dùng BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Panel Header: dùng GradientPanel để vẽ nền gradient
        GradientPanel headerPanel = new GradientPanel();
        headerPanel.setPreferredSize(new Dimension(400, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JLabel lblTitle = new JLabel("Welcome to Chat App");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel Center: chứa form đăng nhập
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // để thấy nền của mainPanel (màu trắng)
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Label Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        centerPanel.add(new JLabel("Username:"), gbc);

        // Row 1: TextField Username
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        centerPanel.add(usernameField, gbc);

        // Row 2: Label Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        centerPanel.add(new JLabel("Password:"), gbc);

        // Row 2: PasswordField
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        centerPanel.add(passwordField, gbc);

        // Panel Bottom: chứa nút Login và Register
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(loginButton);
        bottomPanel.add(registerButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Sự kiện nhấn Enter trong PasswordField để đăng nhập
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    e.consume();
                    doLogin();
                }
            }
        });

        // Sự kiện cho nút Login
        loginButton.addActionListener(e -> doLogin());

        // Sự kiện cho nút Register
        registerButton.addActionListener(e -> new RegisterFrame().setVisible(true));

        // Đóng kết nối khi cửa sổ đăng xuất
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
                dispose();
            }
        });
    }

    // GradientPanel: vẽ nền gradient cho header
    class GradientPanel extends JPanel {
        private Color color1 = new Color(70, 130, 180);   // xanh đậm
        private Color color2 = new Color(135, 206, 235);   // xanh nhạt

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

    private void doLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userService.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Login successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new ChatFrame(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}
