package org.example.game_library.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
    private TextField usernameField;

    @FXML
    private Button scoreButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button backButton;

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

    public void onScoreClick(ActionEvent eventn) {
        logger.log(Level.INFO, "User pressed score button.");
        try{
            // Trimite comanda de ștergere către server
            List<String> parameters = List.of("tictactoe", "score");
            ClientToServerProxy.send(parameters);

            // Așteaptă răspunsul de la server
            String response = ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received scoreboard response from server: {0} (TicTacToe)", response);

            if("SUCCESS".equals(response)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/scoreForm.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) eventn.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                logger.log(Level.WARNING, "Scoreboard loading failed: {0}", response);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed back button. (TicTacToe)");
        try {
            // Asigură-te că /org/example/game_library/FXML/mainMenuForm.fxml este calea corectă către ecranul la care vrei să te întorci
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/userDashboardForm.fxml"));
            Parent root = loader.load();

            // Utilizează evenimentul pentru a obține stage-ul curent, o practică mai bună
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            logger.log(Level.INFO, "Navigated back to main menu.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load back screen: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onNewGameClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed new game button. (TicTacToe)");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoeNewGameScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TicTacToe - New Game");
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut încărca jocul TicTacToe.");
        }
    }
}
