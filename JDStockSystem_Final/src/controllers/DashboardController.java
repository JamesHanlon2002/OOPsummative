package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import app.Database;
import java.sql.PreparedStatement;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.ButtonType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;




public class DashboardController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> typeColumn;
    @FXML private TableColumn<Product, Double> costColumn;
    @FXML private TableColumn<Product, Double> sellColumn;
    @FXML private TableColumn<Product, Integer> quantityColumn;

    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try (Connection conn = Database.getConnection("database/stock.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM stock")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                double cost = rs.getDouble("cost_price");
                double sell = rs.getDouble("sell_price");
                int qty = rs.getInt("quantity");

                Product product;
                switch (type) {
                    case "Clothing" -> product = new Clothing(id, name, cost, sell, qty);
                    case "Shoe" -> product = new Shoe(id, name, cost, sell, qty);
                    case "Equipment" -> product = new Equipment(id, name, cost, sell, qty);
                    default -> {
                        System.out.println("Unknown product type: " + type);
                        continue;
                    }
                }
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        productTable.setItems(products);

        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        costColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getCostPrice()));
        sellColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSellPrice()));
        quantityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
    }

    public void handleBuy(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Buy Stock");
            dialog.setHeaderText("Add Stock for: " + selected.getName());
            dialog.setContentText("Enter quantity to buy:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> {
                try {
                    int qty = Integer.parseInt(input);
                    if (qty <= 0) throw new NumberFormatException();

                    selected.increaseStock(qty);

                    try (Connection conn = Database.getConnection("database/stock.db");
                         PreparedStatement stmt = conn.prepareStatement(
                                 "UPDATE stock SET quantity = ? WHERE id = ?")) {
                        stmt.setInt(1, selected.getQuantity());
                        stmt.setString(2, selected.getId());
                        stmt.executeUpdate();
                    }

                    productTable.refresh();

                } catch (NumberFormatException e) {
                    showError("Invalid input", "Please enter a valid positive number.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public void handleSell(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getQuantity() <= 0) {
                showError("Out of Stock", "This item is out of stock and cannot be sold.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Sell Stock");
            dialog.setHeaderText("Sell Stock for: " + selected.getName());
            dialog.setContentText("Enter quantity to sell:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> {
                try {
                    int qty = Integer.parseInt(input);
                    if (qty <= 0 || qty > selected.getQuantity()) {
                        showError("Invalid quantity", "Quantity must be positive and not exceed current stock.");
                        return;
                    }

                    selected.decreaseStock(qty);

                    try (Connection conn = Database.getConnection("database/stock.db");
                         PreparedStatement stmt = conn.prepareStatement(
                                 "UPDATE stock SET quantity = ? WHERE id = ?")) {
                        stmt.setInt(1, selected.getQuantity());
                        stmt.setString(2, selected.getId());
                        stmt.executeUpdate();
                    }

                    productTable.refresh();

                } catch (NumberFormatException e) {
                    showError("Invalid input", "Please enter a valid positive number.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }



    public void handleSellAll(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int currentQty = selected.getQuantity();

            if (currentQty <= 0) {
                showError("Out of Stock", "This item is already out of stock.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Sell All");
            confirm.setHeaderText("Sell all units of: " + selected.getName());
            confirm.setContentText("This will set quantity to 0. Continue?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selected.decreaseStock(currentQty);

                try (Connection conn = Database.getConnection("database/stock.db");
                     PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE stock SET quantity = 0 WHERE id = ?")) {

                    stmt.setString(1, selected.getId());
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                productTable.refresh();
            }
        }
    }


    public void handlePrintProduct(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy__HH-mm-ss"));
            String basePath = System.getProperty("user.dir");
            File folder = new File(basePath + "/exports");
            File file = new File(folder,"product_" + selected.getId() + "  " + timestamp + ".txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Product Information:\n");
                writer.write("ID: " + selected.getId() + "\n");
                writer.write("Name: " + selected.getName() + "\n");
                writer.write("Type: " + selected.getType() + "\n");
                writer.write("Cost Price: " + selected.getCostPrice() + "\n");
                writer.write("Sell Price: " + selected.getSellPrice() + "\n");
                writer.write("Quantity: " + selected.getQuantity() + "\n");
                writer.write("Total Stock Value (Cost): " + (selected.getCostPrice() * selected.getQuantity()) + "\n");
                writer.write("Total Stock Value (Selling): " + (selected.getSellPrice() * selected.getQuantity()) + "\n");

                showConfirmation("Product info saved to: " + file.getAbsolutePath());
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }

            } catch (IOException e) {
                showError("File Error", "Could not write to file.");
                e.printStackTrace();
            }
        }
    }

    public void handlePrintAll(ActionEvent event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy__HH-mm-ss"));
        String basePath = System.getProperty("user.dir");
        File folder = new File(basePath + "/exports");
        File file = new File(folder,"all_stock" + timestamp + ".txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("All Stock Information:\n\n");

            double totalCostValue = 0;
            double totalSellValue = 0;

            for (Product p : products) {
                writer.write("ID: " + p.getId() + "\n");
                writer.write("Name: " + p.getName() + "\n");
                writer.write("Type: " + p.getType() + "\n");
                writer.write("Cost Price: " + p.getCostPrice() + "\n");
                writer.write("Sell Price: " + p.getSellPrice() + "\n");
                writer.write("Quantity: " + p.getQuantity() + "\n");
                writer.write("Total Stock Value (Cost): " + (p.getCostPrice() * p.getQuantity()) + "\n");
                writer.write("Total Stock Value (Selling): " + (p.getSellPrice() * p.getQuantity()) + "\n");
                writer.write("--------------\n");

                totalCostValue += p.getCostPrice() * p.getQuantity();
                totalSellValue += p.getSellPrice() * p.getQuantity();
            }

            writer.write("\nTotal Stock Value (Cost): " + totalCostValue + "\n");
            writer.write("Total Stock Value (Selling): " + totalSellValue + "\n");

            showConfirmation("All stock info saved to: " + file.getAbsolutePath());
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (IOException e) {
            showError("File Error", "Could not write to file.");
            e.printStackTrace();
        }
    }


    public void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
        // Go back to login screen
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleOpenRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/register.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Register New User");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private Button manageUsersButton; // Make sure this matches the fx:id in your FXML

    private String userRole;

    public void setUserRole(String role) {
        this.userRole = role;

        // Hide the button if not admin
        if (!"admin".equalsIgnoreCase(role)) {
            manageUsersButton.setVisible(false);
        }
    }


}
