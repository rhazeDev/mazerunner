package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
	
	private static final String url = "jdbc:mysql://localhost:3306/dbmaze";
    private static final String user = "root";
    private static final String pass = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    
    public Database() {
        try (Connection connection = Database.getConnection()) {
            System.out.println("Connected");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static List<UserData> leaderboardData(String difficulty) {
        List<UserData> leaderboard = new ArrayList<>();
        
        String query = """
            SELECT u.username, u.time_finished, u.difficulty
            FROM tblleaderboard u
            JOIN (
                SELECT username, MIN(time_finished) AS MinTime
                FROM tblleaderboard
                WHERE difficulty = ?
                GROUP BY username
            ) AS min_timee
            ON u.username = min_timee.username AND u.time_finished = min_timee.MinTime
            WHERE u.difficulty = ? ORDER BY MinTime ASC
        """;

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, difficulty);
            ps.setString(2, difficulty);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    double time = rs.getDouble("time_finished");
                    String diff = rs.getString("difficulty");

                    leaderboard.add(new UserData(username, time, diff));
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return leaderboard;
    }
    
    public static double getBestTime(String username, String difficulty) {
        double bestTime = Double.MAX_VALUE;
        
        String query = "SELECT MIN(time_finished) as best_time FROM tblLeaderboard WHERE username = ? AND difficulty = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, difficulty);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bestTime = rs.getDouble("best_time");
                    if (rs.wasNull()) {
                        bestTime = Double.MAX_VALUE;
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error getting best time: " + e.getMessage());
        }
        
        return bestTime;
    }
    
    public static int getUserRank(String username, String difficulty) {
        int rank = 0;
        double bestTime = getBestTime(username, difficulty);
        
        if (bestTime == Double.MAX_VALUE) {
            return 0;
        }
        
        String query = """
            SELECT COUNT(*) + 1 as rank
            FROM (
                SELECT username, MIN(time_finished) as best_time
                FROM tblLeaderboard
                WHERE difficulty = ?
                GROUP BY username
            ) as user_times
            WHERE best_time < ?
        """;
        
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, difficulty);
            ps.setDouble(2, bestTime);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rank = rs.getInt("rank");
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error getting user rank: " + e.getMessage());
        }
        
        return rank;
    }
    
    public static void saveResult(String username, double time_finished, String difficulty) {
        List<UserData> leaderboard = new ArrayList<>();
        
        String query = "INSERT INTO tblLeaderboard(username, time_finished, difficulty) VALUES(?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setDouble(2, time_finished);
            ps.setString(3, difficulty);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}