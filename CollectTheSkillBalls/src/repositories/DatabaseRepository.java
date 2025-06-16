package repositories;

import models.PlayerResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseRepository {
    private static final String DB_URL = "jdbc:sqlite:resources/database/skillballs.db";
    private static DatabaseRepository instance;
    private Connection connection;
    private boolean isInitialized = false;

    private DatabaseRepository() {
    }

    public static DatabaseRepository getInstance() {
        if (instance == null) {
            synchronized (DatabaseRepository.class) {
                if (instance == null) {
                    instance = new DatabaseRepository();
                }
            }
        }
        return instance;
    }

    public CompletableFuture<Boolean> initializeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isInitialized) {
                    connection = DriverManager.getConnection(DB_URL);
                    createTableIfNotExists();
                    isInitialized = true;
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public boolean initialize() {
        try {
            if (!isInitialized) {
                connection = DriverManager.getConnection(DB_URL);
                createTableIfNotExists();
                isInitialized = true;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS thasil (
                username TEXT PRIMARY KEY,
                score INTEGER NOT NULL DEFAULT 0,
                count INTEGER NOT NULL DEFAULT 0,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public CompletableFuture<List<PlayerResult>> getAllPlayersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerResult> players = new ArrayList<>();
            if (!ensureConnection()) {
                return players;
            }

            String query = "SELECT username, score, count FROM thasil ORDER BY score DESC, count DESC";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    players.add(new PlayerResult(
                            rs.getString("username"),
                            rs.getInt("score"),
                            rs.getInt("count")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return players;
        });
    }

    public List<PlayerResult> getAllPlayers() {
        List<PlayerResult> players = new ArrayList<>();
        if (!ensureConnection()) {
            return players;
        }

        String query = "SELECT username, score, count FROM thasil ORDER BY score DESC, count DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                players.add(new PlayerResult(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public CompletableFuture<List<PlayerResult>> getTopPlayersAsync(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerResult> players = new ArrayList<>();
            if (!ensureConnection()) {
                return players;
            }

            String query = "SELECT username, score, count FROM thasil ORDER BY score DESC, count DESC LIMIT ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, limit);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        players.add(new PlayerResult(
                                rs.getString("username"),
                                rs.getInt("score"),
                                rs.getInt("count")
                        ));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return players;
        });
    }

    public CompletableFuture<Boolean> saveOrUpdatePlayerAsync(PlayerResult player) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ensureConnection() || player == null) {
                return false;
            }
            return performSaveOrUpdate(player);
        });
    }

    public boolean saveOrUpdatePlayer(PlayerResult player) {
        if (!ensureConnection() || player == null) {
            return false;
        }
        return performSaveOrUpdate(player);
    }

    private boolean performSaveOrUpdate(PlayerResult player) {
        String queryCheck = "SELECT COUNT(*) FROM thasil WHERE username = ?";
        try (PreparedStatement pstmtCheck = connection.prepareStatement(queryCheck)) {
            pstmtCheck.setString(1, player.getUsername());
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return updateExistingPlayer(player);
                } else {
                    return insertNewPlayer(player);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateExistingPlayer(PlayerResult player) {
        String queryUpdate = """
            UPDATE thasil
            SET score = score + ?, count = count + ?, updated_at = CURRENT_TIMESTAMP
            WHERE username = ?""";

        try (PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdate)) {
            pstmtUpdate.setInt(1, player.getScore());
            pstmtUpdate.setInt(2, player.getCount());
            pstmtUpdate.setString(3, player.getUsername());
            return pstmtUpdate.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean insertNewPlayer(PlayerResult player) {
        String queryInsert = "INSERT INTO thasil (username, score, count) VALUES (?, ?, ?)";
        try (PreparedStatement pstmtInsert = connection.prepareStatement(queryInsert)) {
            pstmtInsert.setString(1, player.getUsername());
            pstmtInsert.setInt(2, player.getScore());
            pstmtInsert.setInt(3, player.getCount());
            return pstmtInsert.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CompletableFuture<PlayerResult> getPlayerByUsernameAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ensureConnection() || username == null || username.trim().isEmpty()) {
                return null;
            }

            String query = "SELECT username, score, count FROM thasil WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username.trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerResult(
                                rs.getString("username"),
                                rs.getInt("score"),
                                rs.getInt("count")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public CompletableFuture<Boolean> deletePlayerAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            if (!ensureConnection() || username == null || username.trim().isEmpty()) {
                return false;
            }

            String query = "DELETE FROM thasil WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username.trim());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Integer> getPlayerCountAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (!ensureConnection()) {
                return 0;
            }

            String query = "SELECT COUNT(*) FROM thasil";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    private boolean ensureConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return initialize();
            }
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");
            }
            return true;
        } catch (SQLException e) {
            return initialize();
        }
    }

    public boolean isInitialized() {
        return isInitialized && connection != null;
    }

    public CompletableFuture<Void> closeAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    isInitialized = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isInitialized = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dispose() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}