package org.example.game_library.views.tictactoe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.game_library.networking.client.ClientToServerProxy;
import org.example.game_library.utils.loggers.AppLogger;
import org.example.game_library.networking.server.tictactoe_game_logic.ScoreEntry;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreForm {
    private static final Logger logger = AppLogger.getLogger();

    @FXML
    private TableView<ScoreEntry> scoreTable;

    @FXML
    private TableColumn<ScoreEntry, Integer> rankColumn;

    @FXML
    private TableColumn<ScoreEntry, String> usernameColumn;

    @FXML
    private TableColumn<ScoreEntry, Integer> gamesPlayedColumn;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        gamesPlayedColumn.setCellValueFactory(new PropertyValueFactory<>("totalGames"));

        loadScores();
    }

    private void loadScores() {
        try {
            ClientToServerProxy.send(List.of("tictactoe", "score"));

            Object response = ClientToServerProxy.receive();

            if (response instanceof List<?> scoreList) {
                ObservableList<ScoreEntry> data = FXCollections.observableArrayList();
                for (Object item : scoreList) {
                    if (item instanceof ScoreEntry) {
                        data.add((ScoreEntry) item);
                    } else {
                        logger.log(Level.WARNING, "Received unexpected object type in score list: " + item.getClass().getName());
                        showAlert(Alert.AlertType.ERROR, "Eroare de date", "A aparut o eroare la interpretarea datelor de scor.");
                        return;
                    }
                }
                scoreTable.setItems(data);
                logger.log(Level.INFO, "TicTacToe scores loaded successfully. Number of entries: " + data.size());
            } else if (response instanceof String errorMessage) {
                showAlert(Alert.AlertType.ERROR, "Eroare Server", errorMessage);
                logger.log(Level.WARNING, "Server error when loading scores: " + errorMessage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare comunicare", "Raspuns neasteptat de la server: " + response);
                logger.log(Level.WARNING, "Unexpected response from server: " + (response != null ? response.getClass().getName() : "null"));
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare retea", "Nu s-a putut conecta la server pentru a obtine scorurile.");
            logger.log(Level.SEVERE, "Network error loading scores: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare protocol", "Probleme la deserializarea datelor de scor de la server.");
            logger.log(Level.SEVERE, "ClassNotFoundException loading scores: " + e.getMessage());
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeForm.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            logger.log(Level.INFO, "Navigated back to TicTacToe main menu from scoreboard.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load TicTacToe main menu: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Eroare de navigare", "Nu s-a putut intoarce la meniul TicTacToe.");
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