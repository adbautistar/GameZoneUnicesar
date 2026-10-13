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
import java.util.Arrays;
import java.util.List;

/**
 * Handles file-based persistence of returns as CSV records in
 * {@code data/returns.csv}. Returns are saved with their sale and products as
 * ids and loaded back as {@link ReturnRecord}s; resolving those ids into
 * domain objects is the responsibility of the service layer.
 */
public class ReturnRepository {

    private static final String DEFAULT_FILE_PATH = "data/returns.csv";

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final String filePath;

    /**
     * Creates a new repository backed by the default {@code data/returns.csv} file.
     */
    public ReturnRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new repository backed by the given file path. Used by tests
     * to isolate file operations from the real {@code data/returns.csv}.
     *
     * @param filePath the CSV file path to read from and write to
     */
    public ReturnRepository(String filePath) {
        this(null, null, null, filePath);
    }

    /**
     * Creates a repository that can still resolve references itself.
     *
     * @deprecated transitional constructor kept only until {@code Main} is
     *             rewired; use {@link #ReturnRepository()}
     */
    @Deprecated
    public ReturnRepository(SaleService saleService, ProductService productService,
                             AccessoryService accessoryService) {
        this(saleService, productService, accessoryService, DEFAULT_FILE_PATH);
    }

    /**
     * Creates a repository that can still resolve references itself.
     *
     * @deprecated transitional constructor kept only until the tests are
     *             adapted; use {@link #ReturnRepository(String)}
     */
    @Deprecated
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

    /**
     * Reads the CSV file and returns one record per stored return, keeping the
     * sale and products as ids.
     *
     * @return the stored return records, or an empty list if the file does not exist
     */
    public List<ReturnRecord> loadRecords() {
        List<ReturnRecord> records = new ArrayList<>();
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return records;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                records.add(toRecord(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load returns from " + filePath, e);
        }
        return records;
    }

    private ReturnRecord toRecord(String line) {
        String[] fields = line.split(",", -1);
        List<String> productIds = fields[3].isEmpty() ? List.of() : Arrays.asList(fields[3].split("\\|"));
        return new ReturnRecord(fields[0], LocalDate.parse(fields[1]), fields[2], productIds, fields[4]);
    }

    /**
     * Reads the CSV file and resolves every return through the services.
     *
     * @return the resolved returns
     * @deprecated transitional method kept only until the tests are adapted;
     *             use {@link #loadRecords()}
     */
    @Deprecated
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
