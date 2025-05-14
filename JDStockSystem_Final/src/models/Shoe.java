package models;

public class Shoe extends Product {
    public Shoe(String id, String name, double costPrice, double sellPrice, int quantity) {
        super(id, name, costPrice, sellPrice, quantity);
    }

    @Override
    public String getType() {
        return "Shoe";
    }
}