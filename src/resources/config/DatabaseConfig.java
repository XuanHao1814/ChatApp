package resources.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties props = new Properties();
    
    static {
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/database.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getDbUrl() {
        return props.getProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=ChatApp;encrypt=true;trustServerCertificate=true"); 
    }
    
    public static String getDbUser() {
        return props.getProperty("db.user", "sa");
    }
    
    public static String getDbPassword() {
        return props.getProperty("db.password", "123456"); 
    }
}