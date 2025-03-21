package DAO;

import database.DatabaseConnection;
import model.Attachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AttachmentDAO {
    private Connection connection;

    public AttachmentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void saveAttachment(Attachment attachment) {
        String sql = "INSERT INTO Attachments (MessageID, FilePath, FileType, FileSize) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attachment.getMessageId());
            stmt.setString(2, attachment.getFilePath());
            stmt.setString(3, attachment.getFileType());
            stmt.setInt(4, attachment.getFileSize());
            stmt.executeUpdate();
            System.out.println("Attachment saved to database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}