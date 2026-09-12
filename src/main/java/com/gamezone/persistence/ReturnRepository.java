package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnRepository {

    private static final String DEFAULT_FILE_PATH = "data/returns.csv";

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final String filePath;

    public ReturnRepository(SaleService saleService, ProductService productService,
                             AccessoryService accessoryService) {
        this(saleService, productService, accessoryService, DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new repository backed by the given file path. Used by tests
     * to isolate file operations from the real {@code data/returns.csv}.
     */
    public ReturnRepository(SaleService saleService, ProductService productService,
                             AccessoryService accessoryService, String filePath) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.filePath = filePath;
    }

    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Return returnItem : returns) {
                writer.write(toCsvLine(returnItem));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save returns to " + filePath, e);
        }
    }

    private String toCsvLine(Return returnItem) {
        StringBuilder productIds = new StringBuilder();
        List<Product> returnedProducts = returnItem.getReturnedProducts();
        for (int i = 0; i < returnedProducts.size(); i++) {
            if (i > 0) {
                productIds.append("|");
            }
            productIds.append(returnedProducts.get(i).getId());
        }
        return String.join(",",
            returnItem.getId(),
            returnItem.getDate().toString(),
            returnItem.getOriginalSale().getId(),
            productIds.toString(),
            returnItem.getReason(),
            String.valueOf(returnItem.getRefundAmount()));
    }

    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return returns;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Return returnItem = fromCsvLine(line);
                if (returnItem != null) {
                    returns.add(returnItem);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load returns from " + filePath, e);
        }
        return returns;
    }

    private Return fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        String saleId = fields[2];
        String[] productIds = fields[3].isEmpty() ? new String[0] : fields[3].split("\\|");
        String reason = fields[4];

        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            System.err.println("Failed to resolve sale " + saleId + " for return " + id + "; skipping.");
            return null;
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productService.findById(productId);
            if (product == null) {
                product = accessoryService.findById(productId);
            }
            if (product == null) {
                System.err.println("Failed to resolve product or accessory " + productId
                    + " for return " + id + "; skipping item.");
                continue;
            }
            returnedProducts.add(product);
        }

        return new Return(id, date, sale, returnedProducts, reason);
    }
}
