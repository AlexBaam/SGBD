package org.example.game_library.views.tictactoe;

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

public class TicTacToeScoreForm {
    private static final Logger logger = AppLogger.getLogger();

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
