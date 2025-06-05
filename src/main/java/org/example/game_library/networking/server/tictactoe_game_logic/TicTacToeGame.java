package org.example.game_library.networking.server.tictactoe_game_logic;

import java.io.Serializable;

public class TicTacToeGame implements Serializable {
    private String[][] board;
    private String currentSymbol;

    public TicTacToeGame() {
        board = new String[3][3];
        currentSymbol = "X";
        initializeBoard();
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public void setCurrentSymbol(String currentSymbol) {
        this.currentSymbol = currentSymbol;
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = " ";
            }
        }
    }

    public String getCurrentSymbol() {
        return currentSymbol;
    }

    public boolean makeMove(int row, int col, String symbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) return false;
        if (!board[row][col].equals(" ")) return false;

        board[row][col] = symbol;
        return true;
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(currentSymbol) && board[i][1].equals(currentSymbol) && board[i][2].equals(currentSymbol)) {
                return true;
            }

            if (board[0][i].equals(currentSymbol) && board[1][i].equals(currentSymbol) && board[2][i].equals(currentSymbol)) {
                return true;
            }
        }

        return (board[0][0].equals(currentSymbol) && board[1][1].equals(currentSymbol) && board[2][2].equals(currentSymbol)) ||
                (board[0][2].equals(currentSymbol) && board[1][1].equals(currentSymbol) && board[2][0].equals(currentSymbol));
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(" ")){
                    return false;
                }
            }
        }
        return true;
    }

    public void togglePlayer() {
        currentSymbol = currentSymbol.equals("X") ? "O" : "X";
    }

    public String[][] getBoardCopy() {
        String[][] copy = new String[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    public void resetGame() {
        initializeBoard();
        currentSymbol = "X";
    }
}