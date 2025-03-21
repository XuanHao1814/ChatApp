package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlserver://localhost:1433;databaseName=ChatApp;encrypt=true;trustServerCertificate=true;";
                String user = "sa"; // Tên đăng nhập SQL Server
                String password = "xhao2004"; // Mật khẩu SQL Server
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to database!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}