package org.example.game_library.networking.server.tictactoe_game_logic;

import java.io.Serializable;

public class ScoreEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private int rank;
    private String username;
    private int totalGames;

    public ScoreEntry(int rank, String username, int totalGames) {
        this.rank = rank;
        this.username = username;
        this.totalGames = totalGames;
    }

    // Getters
    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getTotalGames() { return totalGames; }
}