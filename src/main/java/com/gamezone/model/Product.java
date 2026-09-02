package com.gamezone.model;

/**
 * Represents a sellable product in the GameZone inventory.
 * Concrete product types must extend this class and provide their own description.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int stock;

    /**
     * Creates a new product with the given identifying and commercial data.
     *
     * @param id    the unique identifier of the product
     * @param title the product title
     * @param price the unit price of the product
     * @param stock the initial stock quantity
     */
    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Returns the unique identifier of this product.
     *
     * @return the product id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this product.
     *
     * @param id the new product id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the title of this product.
     *
     * @return the product title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this product.
     *
     * @param title the new product title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the unit price of this product.
     *
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the unit price of this product.
     *
     * @param price the new product price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the current stock quantity of this product.
     *
     * @return the product stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the stock quantity of this product.
     *
     * @param stock the new stock quantity
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Adjusts the current stock by the given quantity, which may be negative
     * to represent a decrease (e.g., after a sale) or positive to represent
     * a restock.
     *
     * @param quantity the amount to add to the current stock; may be negative
     */
    public void updateStock(int quantity) {
        this.stock += quantity;
    }

    /**
     * Returns a description of this product that integrates its
     * type-specific characteristics. Each concrete subclass provides
     * its own implementation.
     *
     * @return a human-readable description of the product
     */
    public abstract String getDescription();
}
