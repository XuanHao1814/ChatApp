package service;

import DAO.MessageDAO;
import model.Message;

public class ChatService {
    private MessageDAO messageDAO;

    public ChatService() {
        this.messageDAO = new MessageDAO();
    }

    public int saveMessage(int senderId, int receiverId, String content) {
        Message message = new Message(0, senderId, receiverId, content);
        Message savedMessage = messageDAO.saveMessage(message);
        return savedMessage != null ? savedMessage.getMessageId() : -1;
    }
    
    public void closeConnection() {
        if (messageDAO != null) {
            messageDAO.closeConnection();
        }
    }
}