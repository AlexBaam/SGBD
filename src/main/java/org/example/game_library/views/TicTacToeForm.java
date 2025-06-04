package org.example.game_library.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.game_library.networking.ClientToServerProxy;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeForm {
    private static final Logger logger = AppLogger.getLogger();

    @FXML
    private Button scoreButton;

    @FXML
    private Button exitButton;


    @FXML
    public void onExitClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed exit button.");
        try {
            ClientToServerProxy.send(List.of("exit"));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not send exit command to server: {0}", e.getMessage());
        } finally {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    public void onScoreClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed score button.");

    }

    private void onBackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/userDashboardForm.fxml"));
            Parent root = loader.load();
           // Stage stage = (Stage) usernameField.getScene().getWindow();
            //stage.setScene(new Scene(root));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
