package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.products = new ArrayList<>(repository.loadAll());
    }

    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        repository.saveAll(products);
    }

    public void registerConsole(String id, String title, double price, int stock,
                                 String brand, String model, String generation) {
        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        repository.saveAll(products);
    }

    public List<Product> listAllProducts() {
        return Collections.unmodifiableList(products);
    }
}
