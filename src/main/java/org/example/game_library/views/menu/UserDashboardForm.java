package org.example.game_library.views.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;

import org.example.game_library.networking.client.ClientToServerProxy;
import java.util.List;

public class UserDashboardForm {
    private static final Logger logger = AppLogger.getLogger();

    @FXML
    private Button deleteButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button exitButton;

    @FXML
    public void onLogoutClick(ActionEvent event) {
        logger.log(Level.INFO, "User pressed logout button.");
        try {
            List<String> parameters = List.of("logout");
            ClientToServerProxy.send(parameters);

            String response = (String) ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received logout response from server: {0}", response);

            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Deconectare reusita", "V-ati deconectat cu succes.");
                logger.log(Level.INFO, "Logout successful! Navigating to login form.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/menu/loginForm.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Library - Login");
                stage.show();
            } else {

                showAlert(Alert.AlertType.ERROR, "Eroare la deconectare", response);
                logger.log(Level.WARNING, "Logout failed. Server response: {0}", response);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de comunicare", "Nu s-a putut comunica cu serverul la deconectare.");
            logger.log(Level.SEVERE, "IO Error during logout: {0}", e.getMessage());
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de protocol", "Eroare la citirea raspunsului de la server.");
            logger.log(Level.SEVERE, "ClassNotFoundException during logout: {0}", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Eroare necunoscuta", "A aparut o eroare neasteptata la deconectare.");
            logger.log(Level.SEVERE, "Unexpected error during logout: {0}", e.getMessage());
        }
    }

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

    public void onDeleteAccClick(ActionEvent actionEvent) {
        logger.log(Level.INFO, "User pressed delete account button");
        try {
            List<String> parameters = List.of("delete");
            ClientToServerProxy.send(parameters);

            String response = (String) ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received delete account response from server: {0}", response);

            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Stergere cont reusita", "Contul a fost sters cu succes.");
                logger.log(Level.INFO, "Account deletion successful! Navigating to login form.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/menu/loginForm.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Library - Login");
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare la stergerea contului", response);
                logger.log(Level.WARNING, "Account deletion failed. Server response: {0}", response);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de comunicare", "Nu s-a putut comunica cu serverul la stergerea contului.");
            logger.log(Level.SEVERE, "IO Error during account deletion: {0}", e.getMessage());
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de protocol", "Eroare la citirea raspunsului de la server.");
            logger.log(Level.SEVERE, "ClassNotFoundException during account deletion: {0}", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Eroare necunoscuta", "A aparut o eroare neasteptata la stergerea contului.");
            logger.log(Level.SEVERE, "Unexpected error during account deletion: {0}", e.getMessage());
        }
    }

    public void onMinesweeperClick(MouseEvent mouseEvent) {
        logger.log(Level.INFO, "User pressed Minesweeper button!");
    }

    public void onTicTacToeClick(MouseEvent mouseEvent) {
        logger.log(Level.INFO, "User pressed TicTacToe button!");
        try{
            List<String> parameters = List.of("tictactoe");
            ClientToServerProxy.send(parameters);

            String response = (String) ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received delete account response from server: {0}", response);

            if("SUCCESS".equals(response)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/tictactoe/tictactoeForm.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                logger.log(Level.WARNING, "Login failed for user: {0}. Response: {1}", response);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

