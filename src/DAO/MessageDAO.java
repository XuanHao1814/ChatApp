package DAO;

import database.DatabaseConnection;
import model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection connection;

    public MessageDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public Message saveMessage(Message message) {
        String sql = "INSERT INTO Messages (SenderID, ReceiverID, Content, Timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getContent());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        message = new Message(
                                rs.getInt(1),
                                message.getSenderId(),
                                message.getReceiverId(),
                                message.getContent()
                        );
                        return message;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getChatHistory(int senderId, int receiverId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT MessageID, SenderID, ReceiverID, Content, Timestamp " +
                "FROM Messages " +
                "WHERE (SenderID = ? AND ReceiverID = ?) OR (SenderID = ? AND ReceiverID = ?) " +
                "ORDER BY Timestamp";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, receiverId);
            stmt.setInt(4, senderId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("MessageID"),
                        rs.getInt("SenderID"),
                        rs.getInt("ReceiverID"),
                        rs.getString("Content")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}