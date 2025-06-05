package org.example.game_library.database.model;

import java.time.LocalDateTime;

public class SavedGames {
    private int saveId;
    private int userId;
    private int gameTypeId;
    private String gameStateJSON;
    private LocalDateTime savedAt;

    public SavedGames() {}

    public SavedGames(int userId, int gameTypeId, String gameStateJSON) {
        this.userId = userId;
        this.gameTypeId = gameTypeId;
        this.gameStateJSON = gameStateJSON;
    }

    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameStateJSON() {
        return gameStateJSON;
    }

    public void setGameStateJSON(String gameStateJSON) {
        this.gameStateJSON = gameStateJSON;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
