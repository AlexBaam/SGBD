package org.example.game_library.networking.server.tictactoe_game_logic;

import org.example.game_library.networking.server.ThreadCreator;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeRequests {
    private static final Logger logger = AppLogger.getLogger();

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
    }

    public static void handleScore(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        if(request.size() == 2) {
            try {
                output.writeObject("SUCCESS");
            } catch (IOException e) {
                System.err.println("Error writing to output stream: " + e.getMessage());
            }
        } else {
            try {
                output.writeObject("FALS");
            } catch (IOException e) {
                System.err.println("Error writing to output stream: " + e.getMessage());
            }
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
                output.writeObject("WIN: " + symbol);
                game.resetGame();
                return;
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
