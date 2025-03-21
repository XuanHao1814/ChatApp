package GUI;

import DAO.AttachmentDAO;
import model.Attachment;
import model.User;
import service.ChatService;
import service.UserService;

// Sử dụng FlatLaf
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ChatFrame extends JFrame {
    private JTextPane chatPane = new JTextPane();
    private JTextField txtMessage = new JTextField(20);
    private JButton btnSend = new JButton("Send");
    private JButton btnAttach = new JButton("Attach File");
    private JComboBox<String> userComboBox = new JComboBox<>();

    private User currentUser;
    private PrintWriter out;
    private BufferedReader in;
    private ChatService chatService;
    private UserService userService;
    private int lastMessageId = -1;

    // Style cho tin nhắn
    private Style leftStyle;
    private Style rightStyle;
    private Style centerStyle;

    // Panel gradient (dành cho phần top)
    class GradientPanel extends JPanel {
        private Color color1 = new Color(70, 130, 180);   // Xanh đậm
        private Color color2 = new Color(135, 206, 235);  // Xanh nhạt

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Vẽ gradient từ trái sang phải (hoặc trên xuống dưới)
            GradientPaint gp = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
            );
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public ChatFrame(User user) {
        this.currentUser = user;
        this.chatService = new ChatService();
        this.userService = new UserService();
        setTitle("Chat - " + user.getUsername());
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 1) Kích hoạt FlatLaf (Light)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2) Tùy chỉnh bo góc, focus, v.v.
        UIManager.put("Button.arc", 999);
        UIManager.put("TextComponent.arc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.focusColor", new Color(0x007FFF));

        // Thêm border cho content pane để tạo khoảng cách
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Thiết lập style cho tin nhắn
        setupChatStyles();

        // Load danh sách người dùng vào dropdown
        loadUsers();

        // Cài đặt chatPane
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // Panel dưới chứa textfield và các nút
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        txtMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomPanel.add(txtMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        bottomPanel.add(btnAttach, BorderLayout.WEST);

        // Panel trên: dùng GradientPanel để vẽ gradient
        GradientPanel topPanel = new GradientPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 10));
        // Thêm label + combo box lên topPanel
        JLabel lblChatWith = new JLabel("Chat with:");
        lblChatWith.setForeground(Color.WHITE); // text màu trắng trên nền xanh
        topPanel.add(lblChatWith);
        topPanel.add(userComboBox);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Hỗ trợ nhấn Enter để gửi tin nhắn
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();  // ngăn âm thanh beep mặc định
                    sendMessage();
                }
            }
        });

        btnSend.addActionListener(e -> sendMessage());
        btnAttach.addActionListener(e -> attachFile());
        connectToServer();
    }

    private void setupChatStyles() {
        StyledDocument doc = chatPane.getStyledDocument();

        // Style tin nhắn của người khác (bên trái)
        leftStyle = doc.addStyle("LeftStyle", null);
        StyleConstants.setAlignment(leftStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(leftStyle, Color.BLACK);
        StyleConstants.setBackground(leftStyle, new Color(230, 230, 230));
        StyleConstants.setSpaceBelow(leftStyle, 10);
        StyleConstants.setFontFamily(leftStyle, "Arial");
        StyleConstants.setFontSize(leftStyle, 14);

        // Style tin nhắn của người dùng hiện tại (bên phải)
        rightStyle = doc.addStyle("RightStyle", null);
        StyleConstants.setAlignment(rightStyle, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(rightStyle, Color.WHITE);
        StyleConstants.setBackground(rightStyle, new Color(0, 132, 255));
        StyleConstants.setSpaceBelow(rightStyle, 10);
        StyleConstants.setFontFamily(rightStyle, "Arial");
        StyleConstants.setFontSize(rightStyle, 14);

        // Style cho thông báo hệ thống (ở giữa)
        centerStyle = doc.addStyle("CenterStyle", null);
        StyleConstants.setAlignment(centerStyle, StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(centerStyle, Color.GRAY);
        StyleConstants.setItalic(centerStyle, true);
        StyleConstants.setSpaceBelow(centerStyle, 5);
        StyleConstants.setFontFamily(centerStyle, "Arial");
        StyleConstants.setFontSize(centerStyle, 12);
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getUserId() != currentUser.getUserId()) {
                userComboBox.addItem(user.getUsername());
            }
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi tên người dùng hiện tại lên server
            out.println(currentUser.getUsername());

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        if (serverMessage.startsWith("FILE:")) {
                            String[] parts = serverMessage.split(":", 3);
                            if (parts.length == 3) {
                                String senderUsername = parts[1];
                                String filePath = parts[2];
                                boolean isFromCurrentUser = senderUsername.equals(currentUser.getUsername());

                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        if (isImageFile(filePath)) {
                                            displayImage(senderUsername, filePath, isFromCurrentUser);
                                        } else {
                                            displayDownloadButton(senderUsername, filePath, isFromCurrentUser);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        appendMessage("Error displaying file: " + ex.getMessage(), true, centerStyle);
                                    }
                                });
                            }
                        } else {
                            final String msg = serverMessage;
                            SwingUtilities.invokeLater(() -> {
                                if (msg.startsWith(currentUser.getUsername() + ": ")) {
                                    appendMessage(msg.substring((currentUser.getUsername() + ": ").length()), true, rightStyle);
                                } else {
                                    appendMessage(msg, false, leftStyle);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isImageFile(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") ||
                lowerPath.endsWith(".png") || lowerPath.endsWith(".gif") ||
                lowerPath.endsWith(".bmp");
    }

    private void displayImage(String senderUsername, String filePath, boolean isFromCurrentUser) {
        try {
            String messageHeader = isFromCurrentUser ? "You sent an image:" : senderUsername + " sent an image:";
            appendMessage(messageHeader, isFromCurrentUser, isFromCurrentUser ? rightStyle : leftStyle);

            ImageIcon originalIcon = new ImageIcon(filePath);
            Image img = originalIcon.getImage();

            int maxWidth = 250;
            int width = Math.min(originalIcon.getIconWidth(), maxWidth);
            int height = (width * originalIcon.getIconHeight()) / originalIcon.getIconWidth();

            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            insertComponent(new JLabel(scaledIcon), isFromCurrentUser);
            appendMessage("", isFromCurrentUser, centerStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
            appendMessage("Error displaying image: " + ex.getMessage(), isFromCurrentUser, centerStyle);
        }
    }

    private void displayDownloadButton(String senderUsername, String filePath, boolean isFromCurrentUser) {
        try {
            String fileName = new File(filePath).getName();
            String messageHeader = isFromCurrentUser ?
                    "You sent a file: " + fileName :
                    senderUsername + " sent a file: " + fileName;
            appendMessage(messageHeader, isFromCurrentUser, isFromCurrentUser ? rightStyle : leftStyle);

            JButton downloadButton = new JButton("Download");
            downloadButton.setFocusPainted(false);
            downloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            final String finalFilePath = filePath;
            downloadButton.addActionListener(e -> downloadFile(finalFilePath, fileName));

            insertComponent(downloadButton, isFromCurrentUser);
            appendMessage("", isFromCurrentUser, centerStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
            appendMessage("Error displaying download button: " + ex.getMessage(), isFromCurrentUser, centerStyle);
        }
    }

    private void downloadFile(String serverFilePath, String originalFileName) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(originalFileName));
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File targetFile = fileChooser.getSelectedFile();
                Files.copy(Paths.get(serverFilePath), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "File downloaded successfully!", "Download Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error downloading file: " + ex.getMessage(), "Download Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void appendMessage(String message, boolean isFromCurrentUser, Style style) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            int length = doc.getLength();
            doc.insertString(length, message + "\n", style);
            doc.setParagraphAttributes(length, message.length() + 1, style, false);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertComponent(JComponent component, boolean isFromCurrentUser) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            JPanel wrapperPanel = new JPanel(new BorderLayout());
            wrapperPanel.setBackground(isFromCurrentUser ? new Color(0, 132, 255) : new Color(230, 230, 230));
            wrapperPanel.add(component, BorderLayout.CENTER);

            if (isFromCurrentUser) {
                wrapperPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            } else {
                wrapperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            }

            int position = doc.getLength();
            chatPane.setCaretPosition(position);
            chatPane.insertComponent(wrapperPanel);
            doc.insertString(doc.getLength(), "\n", null);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = txtMessage.getText().trim();
        if (!message.isEmpty()) {
            String receiverUsername = (String) userComboBox.getSelectedItem();
            out.println(receiverUsername + ":" + message);
            User receiver = userService.getUserByUsername(receiverUsername);
            if (receiver != null) {
                lastMessageId = chatService.saveMessage(currentUser.getUserId(), receiver.getUserId(), message);
            }
            txtMessage.setText("");
        }
    }

    private void attachFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                String filePath = selectedFile.getAbsolutePath();
                String fileType = getFileExtension(selectedFile);
                int fileSize = (int) selectedFile.length();

                String receiverUsername = (String) userComboBox.getSelectedItem();
                User receiver = userService.getUserByUsername(receiverUsername);

                if (receiver == null) {
                    JOptionPane.showMessageDialog(this, "Receiver not found!");
                    return;
                }

                String placeholder = "[File Attachment]";
                lastMessageId = chatService.saveMessage(currentUser.getUserId(), receiver.getUserId(), placeholder);

                if (lastMessageId == -1) {
                    JOptionPane.showMessageDialog(this, "Failed to create message for attachment!");
                    return;
                }

                File serverFilesDir = new File("server_files");
                if (!serverFilesDir.exists()) {
                    serverFilesDir.mkdir();
                }

                String serverFilePath = "server_files/" + selectedFile.getName();
                saveFileToServer(selectedFile, serverFilePath);

                Attachment attachment = new Attachment(lastMessageId, serverFilePath, fileType, fileSize);
                new AttachmentDAO().saveAttachment(attachment);

                out.println("FILE:" + receiverUsername + ":" + serverFilePath);
            } else {
                JOptionPane.showMessageDialog(this, "No file selected!");
            }
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private void saveFileToServer(File file, String serverFilePath) {
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(serverFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
