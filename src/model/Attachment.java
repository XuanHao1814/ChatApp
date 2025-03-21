package model;

public class Attachment {
    private int attachmentId;
    private int messageId;
    private String filePath;
    private String fileType;
    private int fileSize;

    // Constructor, Getters và Setters
    public Attachment(int messageId, String filePath, String fileType, int fileSize) {
        this.messageId = messageId;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public int getAttachmentId() { return attachmentId; }
    public int getMessageId() { return messageId; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public int getFileSize() { return fileSize; }
}