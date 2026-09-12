package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Product} instances as CSV records
 * in {@code data/products.csv}, using a discriminator column to distinguish
 * between video games and consoles.
 */
public class ProductRepository {

    private static final String DEFAULT_FILE_PATH = "data/products.csv";
    private static final String VIDEOGAME_TYPE = "VIDEOGAME";
    private static final String CONSOLE_TYPE = "CONSOLE";

    private final String filePath;

    /**
     * Creates a repository backed by the default {@code data/products.csv} file.
     */
    public ProductRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a repository backed by the given file path. Used by tests to
     * isolate file operations from the real {@code data/products.csv}.
     *
     * @param filePath the CSV file path to read from and write to
     */
    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Overwrites the CSV file with the given list of products.
     *
     * @param products the complete list of products to persist
     */
    public void saveAll(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Product product : products) {
                writer.write(toCsvLine(product));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products to " + filePath, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of products, choosing the
     * concrete subclass based on the discriminator column.
     *
     * @return the list of products found in the file, or an empty list if
     *         the file does not exist
     */
    public List<Product> loadAll() {
        List<Product> products = new ArrayList<>();
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return products;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                products.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load products from " + filePath, e);
        }
        return products;
    }

    private String toCsvLine(Product product) {
        if (product instanceof VideoGame videoGame) {
            return String.join(",",
                VIDEOGAME_TYPE,
                videoGame.getId(),
                videoGame.getTitle(),
                String.valueOf(videoGame.getPrice()),
                String.valueOf(videoGame.getStock()),
                videoGame.getPlatform(),
                videoGame.getGenre(),
                videoGame.getAgeRating());
        }
        if (product instanceof Console console) {
            return String.join(",",
                CONSOLE_TYPE,
                console.getId(),
                console.getTitle(),
                String.valueOf(console.getPrice()),
                String.valueOf(console.getStock()),
                console.getBrand(),
                console.getModel(),
                console.getGeneration());
        }
        throw new IllegalArgumentException("Unsupported product type: " + product.getClass());
    }

    private Product fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);
        if (VIDEOGAME_TYPE.equals(type)) {
            return new VideoGame(id, title, price, stock, fields[5], fields[6], fields[7]);
        }
        if (CONSOLE_TYPE.equals(type)) {
            return new Console(id, title, price, stock, fields[5], fields[6], fields[7]);
        }
        throw new IllegalArgumentException("Unknown product type in CSV: " + type);
    }
}
