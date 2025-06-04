package org.example.game_library.networking.server.tictactoe_game_logic;

import org.example.game_library.database.dao.TicTacToeDAO;
import org.example.game_library.database.dao.UserDAO; // Importă UserDAO
import org.example.game_library.networking.server.ThreadCreator;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level; // Adaugă import pentru Level
import java.util.logging.Logger; // Adaugă import pentru Logger

public class TicTacToeRequests {
    private static final Logger logger = AppLogger.getLogger(); // Adaugă logger

    public static void handleNewGame(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        try{
            if (request.size() >= 3 && "local".equalsIgnoreCase(request.get(2))) {
                threadCreator.setTicTacToeGame(new TicTacToeGame());
                output.writeObject("SUCCESS");
                logger.log(Level.INFO, "New local game started for thread: {0}", threadCreator.getId());
            } else if(request.size() >= 3 && "player".equalsIgnoreCase(request.get(2))) {
                output.writeObject("SUCCESS");
            } else if (request.size() >= 3 && "ai".equalsIgnoreCase(request.get(2))) {
                output.writeObject("SUCCESS");
            } else {
                output.writeObject("FAILURE: Game mode not supported yet.");
                logger.log(Level.WARNING, "Game mode selection failed! Reason send back to the client!");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to output stream: {0}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void handleLoadGame(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        // Implementează logica pentru încărcarea jocului
        try {
            output.writeObject("FAILURE: Load game not implemented.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending load game response: " + e.getMessage());
        }
    }

    public static void handleScore(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        UserDAO userDAO = new UserDAO();
        try {
            // Vom cere top 3 rank-uri, conform cerinței tale
            List<ScoreEntry> topPlayers = userDAO.getTicTacToeTopRankedPlayers(3); // Obține top 3 rank-uri
            output.writeObject(topPlayers); // Trimite lista de ScoreEntry-uri către client
            logger.log(Level.INFO, "Sent TicTacToe top ranked players to client.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error retrieving TicTacToe scores: " + e.getMessage());
            try {
                output.writeObject("ERROR: Database error retrieving scores."); // Trimite un mesaj de eroare
            } catch (IOException ioException) {
                logger.log(Level.SEVERE, "Error sending database error to client: " + ioException.getMessage());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending scores to client: " + e.getMessage());
        }
    }

    public static void handleForfeit(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        try{
            if(request.size() == 2) {
                output.writeObject("SUCCESS");
            } else {
                output.writeObject("Request didn't have enough arguments!");
                logger.log(Level.WARNING, "Forfeit failed! Reason send back to the client!");
            }
        } catch (IOException e){
            logger.log(Level.SEVERE, "Error writing to output stream: {0}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void handleMove(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        try {
            if(request.size() < 5) {
                output.writeObject("FAILURE: Invalid move request format!");
                return;
            }

            int row = Integer.parseInt(request.get(2));;
            int col = Integer.parseInt(request.get(3));;
            String symbol = request.get(4).toUpperCase();

            TicTacToeGame game = threadCreator.getTicTacToeGame();

            if(game == null) {
                output.writeObject("FAILURE: No active game session!");
                return;
            }

            if(!symbol.equals("X") && !symbol.equals("O")) {
                output.writeObject("FAILURE: Invalid symbol! Must be X or O!");
                return;
            }

            if(!symbol.equals(String.valueOf(game.getCurrentSymbol()))) {
                output.writeObject("FAILURE: Wait for your turn!");
                return;
            }

            boolean moveResult = game.makeMove(row, col, symbol);

            if(!moveResult) {
                output.writeObject("FAILURE: Move failed! Cell already occupied!");
                return;
            }

            if(game.checkWin()){
                try{
                    int winnerId = threadCreator.getCurrentUserId();
                    TicTacToeDAO.incrementWins(winnerId);
                    output.writeObject("WIN: " + symbol);
                    game.resetGame();
                    return;
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL exception met: {0}", e.getMessage());
                }
            }

            if(game.isBoardFull()){
                output.writeObject("DRAW!");
                game.resetGame();
                return;
            }

            game.togglePlayer();
            output.writeObject("SUCCESS");
        } catch (IOException | NumberFormatException e) {
            try {
                output.writeObject("FAILURE: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}