package org.example.game_library.networking.server.tictactoe_game_logic;

public class TicTacToeGame {
    private final char[][] board;
    private char currentSymbol;

    public TicTacToeGame() {
        board = new char[3][3];
        currentSymbol = 'X';
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public char getCurrentSymbol() {
        return currentSymbol;
    }

    public boolean makeMove(int row, int col, String symbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) return false;
        if (board[row][col] != ' ') return false;

        board[row][col] = symbol.charAt(0);
        return true;
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentSymbol && board[i][1] == currentSymbol && board[i][2] == currentSymbol) {
                return true;
            }

            if (board[0][i] == currentSymbol && board[1][i] == currentSymbol && board[2][i] == currentSymbol) {
                return true;
            }
        }

        if (board[0][0] == currentSymbol && board[1][1] == currentSymbol && board[2][2] == currentSymbol){
            return true;
        }

        if (board[0][2] == currentSymbol && board[1][1] == currentSymbol && board[2][0] == currentSymbol){
            return true;
        }

        return false;
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' '){
                    return false;
                }
            }
        }
        return true;
    }

    public void togglePlayer() {
        currentSymbol = (currentSymbol == 'X') ? 'O' : 'X';
    }

    public char[][] getBoardCopy() {
        char[][] copy = new char[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    public void resetGame() {
        initializeBoard();
        currentSymbol = 'X';
    }
}