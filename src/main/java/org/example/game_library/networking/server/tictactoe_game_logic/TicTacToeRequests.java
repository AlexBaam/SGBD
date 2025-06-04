package org.example.game_library.networking.server.tictactoe_game_logic;

import org.example.game_library.database.dao.UserDAO; // Importă UserDAO
import org.example.game_library.networking.server.ThreadCreator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level; // Adaugă import pentru Level
import java.util.logging.Logger; // Adaugă import pentru Logger

public class TicTacToeRequests {
    private static final Logger logger = Logger.getLogger(TicTacToeRequests.class.getName()); // Adaugă logger

    public static void handleNewGame(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) throws IOException {
        if (request.size() >= 3 && "local".equalsIgnoreCase(request.get(2))) {
            output.writeObject("SUCCESS");
        } else {
            output.writeObject("FAILURE: Game mode not supported yet.");
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
        // Implementează logica de forfeit
        try {
            output.writeObject("FAILURE: Forfeit not implemented.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending forfeit response: " + e.getMessage());
        }
    }

    public static void handleMove(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
        // Implementează logica de mutare
        try {
            output.writeObject("FAILURE: Move not implemented.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending move response: " + e.getMessage());
        }
    }
}