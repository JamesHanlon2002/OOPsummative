package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Path to your SQLite DB file
    private final String DB_URL = "jdbc:sqlite:./src/users/users.db";

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());

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

    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // login success if a result is found

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error.");
        }

        return false;
    }
}
