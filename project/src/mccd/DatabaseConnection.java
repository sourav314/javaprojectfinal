package mccd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/CarDeX?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // your MySQL username
    private static final String PASSWORD = "karthik2006"; // your MySQL password

    static {
        try {
            // Load MySQL JDBC Driver explicitly (optional for modern MySQL connectors)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add the connector JAR to classpath.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
}
