package controllers;

import app.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try (Connection conn = Database.getConnection("src/users/users.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT role, approved FROM users WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                boolean approved = rs.getInt("approved") == 1;

                if (!approved) {
                    errorLabel.setText("Account pending admin approval.");
                    return;
                }


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/dashboard.fxml"));
                Scene scene = new Scene(loader.load());

                DashboardController controller = loader.getController();
                controller.setUserRole(role);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);


            } else {
                errorLabel.setText("Invalid username or password.");
            }

        } catch (SQLException | IOException e) {
            errorLabel.setText("Login failed due to an error.");
            e.printStackTrace();
        }
    }
}
