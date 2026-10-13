package com.gamezone.persistence;

import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of warranties as CSV records in
 * {@code data/warranties.csv}. Warranties are saved with their product and
 * sale as ids and loaded back as {@link WarrantyRecord}s; resolving those ids
 * into domain objects is the responsibility of the service layer.
 */
public class WarrantyRepository {

    private static final String DEFAULT_FILE_PATH = "data/warranties.csv";
    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String filePath;

    /**
     * Creates a new repository backed by the default {@code data/warranties.csv} file.
     */
    public WarrantyRepository() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new repository backed by the given file path. Used by tests
     * to isolate file operations from the real {@code data/warranties.csv}.
     *
     * @param filePath the CSV file path to read from and write to
     */
    public WarrantyRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Overwrites the CSV file with the given list of warranties.
     *
     * @param warranties the complete list of warranties to persist
     */
    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Warranty warranty : warranties) {
                writer.write(toCsvLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warranties to " + filePath, e);
        }
    }

    private String toCsvLine(Warranty warranty) {
        String type = warranty instanceof ExtendedWarranty ? EXTENDED_TYPE : BASIC_TYPE;
        return String.join(",",
            type,
            warranty.getId(),
            warranty.getProduct().getId(),
            warranty.getSale().getId(),
            warranty.getStartDate().format(DATE_FORMATTER),
            warranty.getEndDate().format(DATE_FORMATTER));
    }

    /**
     * Reads the CSV file and returns one record per stored warranty, keeping
     * the product and sale as ids.
     *
     * @return the stored warranty records, or an empty list if the file does not exist
     */
    public List<WarrantyRecord> loadAll() {
        List<WarrantyRecord> records = new ArrayList<>();
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
                String[] fields = line.split(",", -1);
                // fields[5] holds the persisted endDate; warranties always re-derive
                // it from startDate and their fixed duration, so it is not read.
                records.add(new WarrantyRecord(fields[0], fields[1], fields[2], fields[3],
                    LocalDate.parse(fields[4], DATE_FORMATTER)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warranties from " + filePath, e);
        }
        return records;
    }
}
