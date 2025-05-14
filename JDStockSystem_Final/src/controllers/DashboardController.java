package controllers;

import javafx.fxml.FXML;
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
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

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
//commit test
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
            selected.increaseStock(10);
            productTable.refresh();
        }
    }

    public void handleSell(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.decreaseStock(5);
            productTable.refresh();
        }
    }

    public void handleSellAll(ActionEvent event){

    }

    public void handlePrintProduct(ActionEvent event) {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Printing product: " + selected);
        }
    }

    public void handlePrintAll(ActionEvent event) {
        System.out.println("All stock:");
        for (Product p : products) {
            System.out.println(p);
        }
    }

    public void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
        // Go back to login screen
    }
}