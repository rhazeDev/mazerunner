package Database;

public class UserData {
	private static String currentUser;
    private String username;
    private double time_finished;
    private String difficulty;

    public UserData(String username, double time_finished, String difficulty) {
        this.username = username;
        this.time_finished = time_finished;
        this.difficulty = difficulty;
    }
    
    public String getUsername() {
        return username;
    }

    public double getTime() {
        return time_finished;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
