package org.example.game_library.views;

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

import org.example.game_library.networking.ClientToServerProxy;
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
            // 1. Trimite comanda de logout către server
            List<String> parameters = List.of("logout");
            ClientToServerProxy.send(parameters);

            // 2. Așteaptă răspunsul de la server
            String response = ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received logout response from server: {0}", response);

            // 3. Gestionează răspunsul
            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Deconectare reușită", "V-ați deconectat cu succes.");
                logger.log(Level.INFO, "Logout successful! Navigating to login form.");

                // 4. Navighează înapoi la ecranul de login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/loginForm.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Library - Login");
                stage.show();
            } else {
                // Dacă serverul a trimis un mesaj de eroare
                showAlert(Alert.AlertType.ERROR, "Eroare la deconectare", response);
                logger.log(Level.WARNING, "Logout failed. Server response: {0}", response);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de comunicare", "Nu s-a putut comunica cu serverul la deconectare.");
            logger.log(Level.SEVERE, "IO Error during logout: {0}", e.getMessage());
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de protocol", "Eroare la citirea răspunsului de la server.");
            logger.log(Level.SEVERE, "ClassNotFoundException during logout: {0}", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Eroare necunoscută", "A apărut o eroare neașteptată la deconectare.");
            logger.log(Level.SEVERE, "Unexpected error during logout: {0}", e.getMessage());
        }
    }

    @FXML
    public void onExitClick(ActionEvent event) {
        // La închiderea aplicației, ar trebui să trimiți și o comandă de EXIT către server
        // pentru a închide thread-ul corespunzător și a deconecta utilizatorul.
        logger.log(Level.INFO, "User pressed exit button.");
        try {
            ClientToServerProxy.send(List.of("exit"));
            // Nu așteptăm un răspuns aici, deoarece aplicația se închide.
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
            // Trimite comanda de ștergere către server
            List<String> parameters = List.of("delete");
            ClientToServerProxy.send(parameters);

            // Așteaptă răspunsul de la server
            String response = ClientToServerProxy.receive();

            logger.log(Level.INFO, "Received delete account response from server: {0}", response);

            if ("SUCCESS".equals(response)) {
                showAlert(Alert.AlertType.INFORMATION, "Ștergere cont reușită", "Contul a fost șters cu succes.");
                logger.log(Level.INFO, "Account deletion successful! Navigating to login form.");

                // După ștergerea contului, navighează înapoi la ecranul de login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/game_library/FXML/loginForm.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Game Library - Login");
                stage.show();
            } else {
                // Dacă serverul a trimis un mesaj de eroare
                showAlert(Alert.AlertType.ERROR, "Eroare la ștergerea contului", response);
                logger.log(Level.WARNING, "Account deletion failed. Server response: {0}", response);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de comunicare", "Nu s-a putut comunica cu serverul la ștergerea contului.");
            logger.log(Level.SEVERE, "IO Error during account deletion: {0}", e.getMessage());
        } catch (ClassNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare de protocol", "Eroare la citirea răspunsului de la server.");
            logger.log(Level.SEVERE, "ClassNotFoundException during account deletion: {0}", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Eroare necunoscută", "A apărut o eroare neașteptată la ștergerea contului.");
            logger.log(Level.SEVERE, "Unexpected error during account deletion: {0}", e.getMessage());
        }
    }

    // Metodă helper pentru afișarea alertelor
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onMinesweeperClick(MouseEvent mouseEvent) {
    }

    public void onTicTacToeClick(MouseEvent mouseEvent) {
    }
}
