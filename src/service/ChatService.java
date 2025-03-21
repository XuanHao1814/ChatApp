package service;

import DAO.MessageDAO;
import database.DatabaseConnection;
import model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChatService {
    private MessageDAO messageDAO;
    private Connection connection;

    public ChatService() {
        this.messageDAO = new MessageDAO();
        this.connection = DatabaseConnection.getConnection(); // Lấy kết nối từ DatabaseConnection
    }

    public int saveMessage(int senderId, int receiverId, String content) {
        String sql = "INSERT INTO Messages (SenderID, ReceiverID, Content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();

            // Lấy MessageID vừa được tạo
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu có lỗi
    }
}