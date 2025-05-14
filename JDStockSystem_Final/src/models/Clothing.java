package models;

public class Clothing extends Product {
    public Clothing(String id, String name, double costPrice, double sellPrice, int quantity) {
        super(id, name, costPrice, sellPrice, quantity);
    }

    @Override
    public String getType() {
        return "Clothing";
    }
}