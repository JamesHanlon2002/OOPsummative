package models;

public abstract class Product {
    protected String id;
    protected String name;
    protected double costPrice;
    protected double sellPrice;
    protected int quantity;
    protected String description;

    public Product(String id, String name, double costPrice, double sellPrice, int quantity, String description) {
        this.id = id;
        this.name = name;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return "/images/" + id + ".png";
    }

    public javafx.scene.image.ImageView getImageView() {
        javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream(getImagePath()));
        javafx.scene.image.ImageView view = new javafx.scene.image.ImageView(img);
        view.setFitWidth(70);
        view.setFitHeight(70);
        view.setPreserveRatio(true);
        return view;
    }

    public void increaseStock(int qty) {
        this.quantity += qty;
    }

    public void decreaseStock(int qty) {
        if (this.quantity >= qty) this.quantity -= qty;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return String.format(
                "Product[ID=%s, Name=%s, Cost=%.2f, Sell=%.2f, Qty=%d, Description=%s]", id, name, costPrice, sellPrice, quantity, description != null ? description : "N/A");
    }
}