package org.example.game_library.networking.server.tictactoe_game_logic;

import org.example.game_library.networking.server.ThreadCreator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class TicTacToeRequests {
    public static void handleNewGame(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) throws IOException {
        if (request.size() >= 3 && "local".equalsIgnoreCase(request.get(2))) {
            output.writeObject("SUCCESS");
        } else {
            output.writeObject("FAILURE: Game mode not supported yet.");
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
    }

    public static void handleMove(List<String> request, ThreadCreator threadCreator, ObjectOutputStream output, ObjectInputStream input) {
    }
}
