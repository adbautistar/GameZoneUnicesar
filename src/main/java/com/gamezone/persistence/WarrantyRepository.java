package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WarrantyRepository {

    private static final String FILE_PATH = "data/warranties.csv";
    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    public WarrantyRepository(SaleService saleService, ProductService productService,
                               AccessoryService accessoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Warranty warranty : warranties) {
                writer.write(toCsvLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warranties to " + FILE_PATH, e);
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

    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return warranties;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Warranty warranty = fromCsvLine(line);
                if (warranty != null) {
                    warranties.add(warranty);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warranties from " + FILE_PATH, e);
        }
        return warranties;
    }

    private Warranty fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4], DATE_FORMATTER);
        // fields[5] holds the persisted endDate; the constructor always re-derives
        // it from startDate and the subclass's fixed duration, so it is not read
        // here (matches the same recompute-on-load pattern used by SaleRepository).

        Product product = productService.findById(productId);
        if (product == null) {
            product = accessoryService.findById(productId);
        }
        if (product == null) {
            System.err.println("Failed to resolve product or accessory " + productId
                + " for warranty " + id + "; skipping.");
            return null;
        }

        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            System.err.println("Failed to resolve sale " + saleId + " for warranty " + id + "; skipping.");
            return null;
        }

        if (BASIC_TYPE.equals(type)) {
            return new BasicWarranty(id, product, sale, startDate);
        }
        if (EXTENDED_TYPE.equals(type)) {
            return new ExtendedWarranty(id, product, sale, startDate);
        }
        throw new IllegalArgumentException("Unknown warranty type in CSV: " + type);
    }
}
