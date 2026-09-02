package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations available for managing the product
 * inventory, backed by a {@link ProductRepository}.
 */
public class ProductService {

    private final ProductRepository repository;
    private final List<Product> products;

    /**
     * Creates a new service backed by the given repository, loading the
     * current inventory into memory.
     *
     * @param repository the repository used to persist and load products
     */
    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.products = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new video game in the inventory and persists the change.
     *
     * @param id        the unique identifier of the product
     * @param title     the product title
     * @param price     the unit price of the product
     * @param stock     the initial stock quantity
     * @param platform  the platform the game runs on
     * @param genre     the genre of the game
     * @param ageRating the age rating of the game
     */
    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        repository.saveAll(products);
    }

    /**
     * Registers a new console in the inventory and persists the change.
     *
     * @param id         the unique identifier of the product
     * @param title      the product title
     * @param price      the unit price of the product
     * @param stock      the initial stock quantity
     * @param brand      the console's brand
     * @param model      the console's model
     * @param generation the console's generation
     */
    public void registerConsole(String id, String title, double price, int stock,
                                 String brand, String model, String generation) {
        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        repository.saveAll(products);
    }

    /**
     * Returns an unmodifiable view of every product currently in the inventory.
     *
     * @return the list of all products
     */
    public List<Product> listAllProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Adjusts the stock of the product with the given id, if found, and
     * persists the change.
     *
     * @param productId the id of the product to update
     * @param quantity  the amount to add to the current stock; may be negative
     */
    public void updateStock(String productId, int quantity) {
        Product product = findById(productId);
        if (product != null) {
            product.updateStock(quantity);
            repository.saveAll(products);
        }
    }

    /**
     * Finds a product by its id.
     *
     * @param id the id to search for
     * @return the matching product, or {@code null} if none is found
     */
    public Product findById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
}
