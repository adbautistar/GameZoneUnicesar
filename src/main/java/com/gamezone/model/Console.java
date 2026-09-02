package com.gamezone.model;

/**
 * Represents a console product, with brand, model, and generation
 * in addition to the attributes shared with every {@link Product}.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Creates a new console with the given shared and specific attributes.
     *
     * @param id         the unique identifier of the product
     * @param title      the product title
     * @param price      the unit price of the product
     * @param stock      the initial stock quantity
     * @param brand      the console's brand
     * @param model      the console's model
     * @param generation the console's generation
     */
    public Console(String id, String title, double price, int stock,
                    String brand, String model, String generation) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Returns the brand of this console.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the brand of this console.
     *
     * @param brand the new brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Returns the model of this console.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of this console.
     *
     * @param model the new model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the generation of this console.
     *
     * @return the generation
     */
    public String getGeneration() {
        return generation;
    }

    /**
     * Sets the generation of this console.
     *
     * @param generation the new generation
     */
    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * Returns a description of this console that includes its shared
     * and brand-specific attributes.
     *
     * @return a human-readable description of the console
     */
    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Brand: %s, Model: %s, Generation: %s, Price: %.2f, Stock: %d",
            getId(), getTitle(), brand, model, generation, getPrice(), getStock());
    }
}
