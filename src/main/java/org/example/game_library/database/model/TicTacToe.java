package org.example.game_library.database.model;

public class TicTacToe {
    private int userId;
    private int totalWins;

    public TicTacToe() {}

    public TicTacToe(int userId, int totalWins) {
        this.userId = userId;
        this.totalWins = totalWins;
    }

    public TicTacToe(int userId) {
        this(userId, 0);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    @Override
    public String toString() {
        return "TicTacToe [userId=" + userId + ", totalWins=" + totalWins + "]";
    }
}
