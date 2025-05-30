package models;

public class Shoe extends Product {
    public Shoe(String id, String name, double costPrice, double sellPrice, int quantity, String description) {
        super(id, name, costPrice, sellPrice, quantity, description);
    }

    @Override
    public String getType() {
        return "Shoe";
    }
}