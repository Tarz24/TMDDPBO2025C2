package models;

public class PlayerResult {
    private String username;
    private int score;
    private int count;

    public PlayerResult(String username, int score, int count) {
        this.username = username;
        this.score = score;
        this.count = count;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getCount() { return count; }

    public void addScore(int points) {
        this.score += points;
        this.count++;
    }
}