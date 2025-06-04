package org.example.game_library.networking;

import org.example.game_library.utils.loggers.AppLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMain extends Application {

    private static final Logger logger = AppLogger.getLogger();

    @Override
    public void start(Stage stage) {
        try {
            // Attempt to connect to the server
            ClientToServerProxy.init();
            logger.log(Level.INFO, "Client connected");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client connection error: {0}", e.getMessage());
            return; // stop if connection failed
        }

        // Load and display the main menu
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/mainMenuForm.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);

            scene.setFill(Color.TRANSPARENT);

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Interface loading error: {0}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}