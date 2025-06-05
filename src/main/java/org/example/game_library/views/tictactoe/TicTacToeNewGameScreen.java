package org.example.game_library.views.tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.game_library.networking.client.ClientToServerProxy;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeNewGameScreen {
    private static final Logger logger = AppLogger.getLogger();

    @FXML
    public void onAIClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed vs AI button. (TicTacToe - New Game)");
        try {
            List<String> request = List.of("tictactoe", "newgame", "ai");
            ClientToServerProxy.send(request);
            String response = (String) ClientToServerProxy.receive();

            if ("SUCCESS".equalsIgnoreCase(response)) {
                logger.log(Level.INFO, "New local game initialized successfully.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeBoard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("TicTacToe - Local Game");
                stage.show();
            } else {
                logger.log(Level.WARNING, "Server response: {0}", response);
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error during local game initialization: {0}", e.getMessage());
        }
    }

    @FXML
    public void OnLocalClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed vs Local button. (TicTacToe - New Game)");
        try {
            List<String> request = List.of("tictactoe", "newgame", "local");
            ClientToServerProxy.send(request);
            String response = (String) ClientToServerProxy.receive();

            if ("SUCCESS".equalsIgnoreCase(response)) {
                logger.log(Level.INFO, "New local game initialized successfully.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeBoard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("TicTacToe - Local Game");
                stage.show();
            } else {
                logger.log(Level.WARNING, "Server response: {0}", response);
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error during local game initialization: {0}", e.getMessage());
        }
    }

    @FXML
    public void OnPlayerClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed vs Player button. (TicTacToe - New Game)");
        try {
            List<String> request = List.of("tictactoe", "newgame", "player");
            ClientToServerProxy.send(request);
            String response = (String) ClientToServerProxy.receive();

            if ("SUCCESS".equalsIgnoreCase(response)) {
                logger.log(Level.INFO, "New local game initialized successfully.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeBoard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("TicTacToe - Local Game");
                stage.show();
            } else {
                logger.log(Level.WARNING, "Server response: {0}", response);
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error during local game initialization: {0}", e.getMessage());
        }
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed back button. (TicTacToe - New Game)");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeForm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            logger.log(Level.INFO, "Navigated back to main menu.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load back screen: " + e.getMessage());
        }
    }
}
