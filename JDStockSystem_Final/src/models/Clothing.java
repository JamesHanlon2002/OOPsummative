package models;

public class Clothing extends Product {
    public Clothing(String id, String name, double costPrice, double sellPrice, int quantity, String description) {
        super(id, name, costPrice, sellPrice, quantity, description);
    }

    @Override
    public String getType() {
        return "Clothing";
    }
}