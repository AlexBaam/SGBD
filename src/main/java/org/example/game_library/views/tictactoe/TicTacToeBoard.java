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

import java.io.IOException;
import java.util.List;

public class TicTacToeBoard {

    private String currentSymbol = "X";

    private void togglePlayer() {
        currentSymbol = currentSymbol.equals("X") ? "O" : "X";
    }

    @FXML
    public void handleCellClick(ActionEvent event) throws IOException, ClassNotFoundException {
        Button clicked = (Button) event.getSource();

        Integer row = GridPane.getRowIndex(clicked);
        Integer col = GridPane.getColumnIndex(clicked);

        if (row == null) row = 0;
        if (col == null) col = 0;

        ClientToServerProxy.send(List.of("tictactoe", "move", row.toString(), col.toString(), currentSymbol));

        String response = (String) ClientToServerProxy.receive();

        if ("SUCCESS".equals(response)) {
            clicked.setText(currentSymbol);
            clicked.setDisable(true);
            togglePlayer();
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid move", "Cell already occupied!");
        }
    }

    public void onSaveClick(ActionEvent event) {
    }

    @FXML
    public void onForfeitClick(ActionEvent event) {
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
                    ClientToServerProxy.send(List.of("tictactoe", "forfeit"));

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeNewGameScreen.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("TicTacToe - New Game");
                    stage.show();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Couldn't forfeit the game!");
                }
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
}
