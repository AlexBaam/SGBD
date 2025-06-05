package org.example.game_library.database.model;

public class GameType {
    private int gameTypeId;
    private String name;

    public GameType() {}

    public GameType(String name) {
        this.name = name;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
