package model;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String content;
    private Timestamp timestamp;

    // Constructor đầy đủ với messageId
    public Message(int messageId, int senderId, int receiverId, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // Getter cho messageId
    public int getMessageId() {
        return messageId;
    }

    // Getters and Setters
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }
}
