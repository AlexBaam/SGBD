package org.example.game_library.views.tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.game_library.networking.client.ClientToServerProxy;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicTacToeBoard {
    private static final Logger logger = AppLogger.getLogger();

    @FXML
    private GridPane boardGrid;

    private String currentSymbol = "X";

    private void togglePlayer() {
        currentSymbol = currentSymbol.equals("X") ? "O" : "X";
    }

    @FXML
    public void handleCellClick(ActionEvent event) throws IOException, ClassNotFoundException {
        Button clicked = (Button) event.getSource();

        Integer row = GridPane.getRowIndex(clicked);
        Integer col = GridPane.getColumnIndex(clicked);

        if (row == null) {
            row = 0;
        }

        if (col == null) {
            col = 0;
        }

        ClientToServerProxy.send(List.of("tictactoe", "move", row.toString(), col.toString(), currentSymbol));

        String response = (String) ClientToServerProxy.receive();

        if (response.startsWith("WIN:")) {
            clicked.setText(currentSymbol);
            clicked.setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Game Over", "Player " + currentSymbol + " wins!");
            returnToNewGameScreen(event);
        } else if ("DRAW!".equalsIgnoreCase(response)) {
            clicked.setText(currentSymbol);
            clicked.setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Game Over", "It's a draw!");
            returnToNewGameScreen(event);
        } else if ("SUCCESS".equalsIgnoreCase(response)) {
            clicked.setText(currentSymbol);
            clicked.setDisable(true);
            togglePlayer();
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid move", "Cell already occupied!");
        }
    }

    @FXML
    public void onSaveClick(ActionEvent event) {
        try {
            ClientToServerProxy.send(List.of("tictactoe", "save"));
            String response = (String) ClientToServerProxy.receive();

            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Game Saved", "Your game was saved successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Save Failed", response);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save game: " + e.getMessage());
        }
    }

    @FXML
    public void onForfeitClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed forfeit button.");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm forfeit...");
        confirm.setHeaderText("Are you sure you want to forfeit?");
        confirm.setContentText("Pressing yes will result in a lose!");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");

        confirm.getButtonTypes().setAll(yes, no);

        confirm.showAndWait().ifPresent(choice -> {
            if (choice == yes) {
                try {
                    logger.log(Level.INFO, "User decided to forfeit the game.");

                    ClientToServerProxy.send(List.of("tictactoe", "forfeit"));

                    String response =  (String) ClientToServerProxy.receive();

                    if ("SUCCESS".equals(response)) {
                        logger.log(Level.INFO, "Successfully forfeited the game.");

                        FXMLLoader loader = new FXMLLoader(
                                getClass().
                                    getResource(
                                        "/org/example/game_library/FXML/tictactoe/tictactoeNewGameScreen.fxml"
                                    )
                        );

                        Parent root = loader.load();
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("TicTacToe - New Game");
                        stage.show();
                    } else {
                        logger.log(Level.WARNING, "Failed to forfeit the game.");
                        logger.log(Level.WARNING, "Server response: {0}", response);
                    }
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Couldn't forfeit the game! Reason: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.log(Level.INFO, "User gave up on the forfeit of the game.");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void returnToNewGameScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeNewGameScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TicTacToe - New Game");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Couldn't go back to new game screen.");
        }
    }
}
