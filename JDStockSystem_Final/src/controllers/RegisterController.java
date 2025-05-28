package controllers;

import app.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label statusLabel;

    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        try (Connection conn = Database.getConnection("src/users/users.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password, role, approved) VALUES (?, ?, 'standard', 1)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            statusLabel.setText("User registered successfully.");

        } catch (SQLException e) {
            statusLabel.setText("Registration failed. Username might be taken.");
            e.printStackTrace();
        }
    }
}