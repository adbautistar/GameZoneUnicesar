package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;

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
        this.filePath = filePath;
    }

    /**
     * Overwrites the CSV file with the given list of returns.
     *
     * @param returns the complete list of returns to persist
     */
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
    public List<ReturnRecord> loadAll() {
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
}
