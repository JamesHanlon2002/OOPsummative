package models;

public class Equipment extends Product {
    public Equipment(String id, String name, double costPrice, double sellPrice, int quantity) {
        super(id, name, costPrice, sellPrice, quantity);
    }

    @Override
    public String getType() {
        return "Equipment";
    }
}