package org.example.game_library.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuForm {

    @FXML private AnchorPane rootPane_MainMenu;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button exitButton;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        System.out.println("Main menu initialized.");

        // Make window draggable
        rootPane_MainMenu.setOnMousePressed(this::handleMousePressed);
        rootPane_MainMenu.setOnMouseDragged(this::handleMouseDragged);
    }

    @FXML
    private void onLoginClick() throws IOException {
        switchTo("loginForm.fxml");
    }

    @FXML
    private void onRegisterClick() throws IOException {
        switchTo("registerForm.fxml");
    }

    @FXML
    private void onExitClick() {
        System.exit(0);
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) rootPane_MainMenu.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    private void switchTo(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/" + fxml));
        Parent root = loader.load();
        Stage stage = (Stage) rootPane_MainMenu.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}