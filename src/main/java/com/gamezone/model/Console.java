package com.gamezone.model;

public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    public Console(String id, String title, double price, int stock,
                    String brand, String model, String generation) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    @Override
    public String getDescription() {
        return String.format(
            "[%s] %s - Brand: %s, Model: %s, Generation: %s, Price: %.2f, Stock: %d",
            getId(), getTitle(), brand, model, generation, getPrice(), getStock());
    }
}
