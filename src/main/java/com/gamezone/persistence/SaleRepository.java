package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;

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
 * Handles file-based persistence of {@link Sale} instances as CSV records in
 * {@code data/sales.csv}. Sales are saved with their customer, seller and
 * items as ids and loaded back as {@link SaleRecord}s; resolving those ids
 * into domain objects is the responsibility of the service layer.
 */
public class SaleRepository {

    private static final String DEFAULT_FILE_PATH = "data/sales.csv";

    private final String filePath;

    /**
     * Creates a new repository backed by the default {@code data/sales.csv} file.
     */
    public SaleRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new repository backed by the given file path. Used by tests
     * to isolate file operations from the real {@code data/sales.csv}.
     *
     * @param filePath the CSV file path to read from and write to
     */
    public SaleRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Overwrites the CSV file with the given list of sales.
     *
     * @param sales the complete list of sales to persist
     */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Sale sale : sales) {
                writer.write(toCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sales to " + filePath, e);
        }
    }

    /**
     * Reads the CSV file and returns one record per stored sale, keeping the
     * customer, seller and items as ids.
     *
     * @return the stored sale records, or an empty list if the file does not exist
     */
    public List<SaleRecord> loadAll() {
        List<SaleRecord> records = new ArrayList<>();
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
            throw new RuntimeException("Failed to load sales from " + filePath, e);
        }
        return records;
    }

    private String toCsvLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds.append(";");
            }
            productIds.append(products.get(i).getId());
        }
        String appliedPromotionName = sale.getAppliedPromotionName() != null ? sale.getAppliedPromotionName() : "";
        return String.join(",",
            sale.getId(),
            sale.getDate().toString(),
            sale.getCustomer().getId(),
            sale.getSeller().getId(),
            productIds.toString(),
            String.valueOf(sale.getTotalAmount()),
            appliedPromotionName,
            String.valueOf(sale.getDiscountAmount()));
    }

    private SaleRecord toRecord(String line) {
        String[] fields = line.split(",", -1);
        List<String> productIds = fields[4].isEmpty() ? List.of() : Arrays.asList(fields[4].split(";"));
        boolean hasPromotion = fields.length > 7 && !fields[6].isEmpty();
        return new SaleRecord(
            fields[0],
            LocalDate.parse(fields[1]),
            fields[2],
            fields[3],
            productIds,
            Double.parseDouble(fields[5]),
            hasPromotion ? fields[6] : null,
            hasPromotion ? Double.parseDouble(fields[7]) : 0.0);
    }
}
