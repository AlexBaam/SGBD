package org.example.game_library.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeNewGameScreen {
    private static final Logger logger = AppLogger.getLogger();

    public void onAIClick(ActionEvent actionEvent) {
        logger.log(Level.INFO, "User pressed vs AI button. (TicTacToe - New Game)");
    }

    public void OnLocalClick(ActionEvent actionEvent) {
        logger.log(Level.INFO, "User pressed vs Local button. (TicTacToe - New Game)");
    }

    public void OnPlayerClick(ActionEvent actionEvent) {
        logger.log(Level.INFO, "User pressed vs Player button. (TicTacToe - New Game)");
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed back button. (TicTacToe - New Game)");
        try {
            // Asigură-te că /org/example/game_library/FXML/mainMenuForm.fxml este calea corectă către ecranul la care vrei să te întorci
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoeForm.fxml"));
            Parent root = loader.load();

            // Utilizează evenimentul pentru a obține stage-ul curent, o practică mai bună
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            logger.log(Level.INFO, "Navigated back to main menu.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load back screen: " + e.getMessage());
        }
    }
}
