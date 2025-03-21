CREATE DATABASE ChatApp;
GO

USE ChatApp;
GO

-- Tạo bảng Users
CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Status VARCHAR(20) DEFAULT 'offline',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);
GO

-- Tạo bảng Messages
CREATE TABLE Messages (
    MessageID INT IDENTITY(1,1) PRIMARY KEY,
    SenderID INT NOT NULL,
    ReceiverID INT NOT NULL,
    Content TEXT,
    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (SenderID) REFERENCES Users(UserID),
    FOREIGN KEY (ReceiverID) REFERENCES Users(UserID)
);
GO

CREATE TABLE Attachments (
    AttachmentID INT IDENTITY(1,1) PRIMARY KEY,
    MessageID INT NOT NULL,
    FilePath VARCHAR(255) NOT NULL,
    FileType VARCHAR(20),
    FileSize INT,
    FOREIGN KEY (MessageID) REFERENCES Messages(MessageID)
);
GO

SELECT * FROM Messages;
SELECT * FROM Attachments;