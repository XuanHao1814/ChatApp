package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static Map<String, PrintWriter> clientWriters = new HashMap<>();
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }

    // Thêm phương thức broadcast
    public static void broadcast(String message, String excludeUser) {
        clientWriters.forEach((username, writer) -> {
            if (!username.equals(excludeUser)) {
                writer.println(message);
            }
        });
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Nhận username từ client
                username = in.readLine();
                clientWriters.put(username, out); // Lưu client vào map
                System.out.println(username + " connected.");

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    if (clientMessage.startsWith("FILE:")) {
                        // Xử lý tin nhắn chứa file
                        String[] parts = clientMessage.split(":", 3);
                        if (parts.length == 3) {
                            String receiverUsername = parts[1];
                            String filePath = parts[2];

                            // Gửi thông báo file đính kèm đến người nhận
                            PrintWriter receiverWriter = clientWriters.get(receiverUsername);
                            if (receiverWriter != null) {
                                receiverWriter.println("FILE:" + username + ":" + filePath);
                            }
                        }
                    } else {
                        // Tin nhắn có định dạng: receiverUsername:message
                        String[] parts = clientMessage.split(":", 2);
                        if (parts.length == 2) {
                            String receiverUsername = parts[0];
                            String message = parts[1];

                            // Gửi tin nhắn đến người nhận
                            PrintWriter receiverWriter = clientWriters.get(receiverUsername);
                            if (receiverWriter != null) {
                                receiverWriter.println(username + ": " + message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
                clientWriters.remove(username);
            }
        }

        public void closeConnection() {
            try {
                if (clientSocket != null) clientSocket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}