package DAO;

import database.DatabaseConnection;
import model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDAO {
    private Connection connection;

    public MessageDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void saveMessage(Message message) {
        String sql = "INSERT INTO Messages (SenderID, ReceiverID, Content) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getContent());
            stmt.executeUpdate();
            System.out.println("Message saved to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}