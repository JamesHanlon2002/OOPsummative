package models;

public abstract class Product {
    protected String id;
    protected String name;
    protected double costPrice;
    protected double sellPrice;
    protected int quantity;

    public Product(String id, String name, double costPrice, double sellPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getCostPrice() { return costPrice; }
    public double getSellPrice() { return sellPrice; }
    public int getQuantity() { return quantity; }

    public void increaseStock(int qty) {
        this.quantity += qty;
    }

    public void decreaseStock(int qty) {
        if (this.quantity >= qty) this.quantity -= qty;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return String.format("Product[ID=%s, Name=%s, Cost=%.2f, Sell=%.2f, Qty=%d]", id, name, costPrice, sellPrice, quantity);
    }
}