package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conn {
	private static final String url = "jdbc:mysql://localhost:3306/dbampayag";
    private static final String user = "root";
    private static final String pass = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    
    public conn() {
        try (Connection connection = conn.getConnection()) {
            System.out.println("Connected");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}