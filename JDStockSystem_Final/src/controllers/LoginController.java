package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "admin123".equals(password)) {
            try {
                // Load the dashboard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                // Get the current window (stage) and set the new scene
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(dashboardScene);
            } catch (IOException e) {
                errorLabel.setText("Failed to load dashboard.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }
}