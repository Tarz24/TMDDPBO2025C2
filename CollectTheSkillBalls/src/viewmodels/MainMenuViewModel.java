package viewmodels;

import models.PlayerResult;
import repositories.DatabaseRepository;

import java.util.List;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class MainMenuViewModel extends Observable {

    private DatabaseRepository dbRepository;
    private List<PlayerResult> leaderboard;
    private String currentUsername = "";

    public MainMenuViewModel() {
        this.dbRepository = DatabaseRepository.getInstance();
        this.dbRepository.initialize();
        loadLeaderboard();
    }

    public void setUsername(String username) {
        this.currentUsername = username.trim();
        setChanged();
        notifyObservers("USERNAME_CHANGED");
    }

    public boolean canStartGame() {
        return !currentUsername.isEmpty();
    }

    public PlayerResult createNewPlayer() {
        if (!canStartGame()) {
            return null;
        }
        return new PlayerResult(currentUsername, 0, 0);
    }

    public void refreshLeaderboard() {
        loadLeaderboard();
        setChanged();
        notifyObservers("LEADERBOARD_UPDATED");
    }

    private void loadLeaderboard() {
        this.leaderboard = dbRepository.getAllPlayers();
    }

    public List<PlayerResult> getLeaderboard() {
        return leaderboard;
    }

    public void dispose() {
        // Metode ini sengaja dikosongkan karena repository adalah singleton
    }
}