package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

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

public class PromotionRepository {

    private static final String FILE_PATH = "data/promotions.csv";
    private static final String PERCENTAGE_TYPE = "PERCENTAGE";
    private static final String CATEGORY_TYPE = "CATEGORY";
    private static final String BULK_TYPE = "BULK";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Promotion promotion : promotions) {
                writer.write(toCsvLine(promotion));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save promotions to " + FILE_PATH, e);
        }
    }

    private String toCsvLine(Promotion promotion) {
        String startDate = promotion.getStartDate().format(DATE_FORMATTER);
        String endDate = promotion.getEndDate().format(DATE_FORMATTER);
        if (promotion instanceof PercentageDiscount percentageDiscount) {
            return String.join(",",
                PERCENTAGE_TYPE,
                percentageDiscount.getId(),
                percentageDiscount.getName(),
                startDate,
                endDate,
                String.valueOf(percentageDiscount.getPercentage()));
        }
        if (promotion instanceof CategoryDiscount categoryDiscount) {
            return String.join(",",
                CATEGORY_TYPE,
                categoryDiscount.getId(),
                categoryDiscount.getName(),
                startDate,
                endDate,
                String.valueOf(categoryDiscount.getPercentage()),
                categoryDiscount.getTargetCategory());
        }
        if (promotion instanceof BulkPurchaseDiscount bulkPurchaseDiscount) {
            return String.join(",",
                BULK_TYPE,
                bulkPurchaseDiscount.getId(),
                bulkPurchaseDiscount.getName(),
                startDate,
                endDate,
                String.valueOf(bulkPurchaseDiscount.getMinimumQuantity()),
                String.valueOf(bulkPurchaseDiscount.getPercentage()));
        }
        throw new IllegalArgumentException("Unsupported promotion type: " + promotion.getClass());
    }

    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return promotions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                promotions.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load promotions from " + FILE_PATH, e);
        }
        return promotions;
    }

    private Promotion fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String name = fields[2];
        LocalDate startDate = LocalDate.parse(fields[3], DATE_FORMATTER);
        LocalDate endDate = LocalDate.parse(fields[4], DATE_FORMATTER);
        if (PERCENTAGE_TYPE.equals(type)) {
            return new PercentageDiscount(id, name, startDate, endDate, Double.parseDouble(fields[5]));
        }
        if (CATEGORY_TYPE.equals(type)) {
            return new CategoryDiscount(id, name, startDate, endDate, Double.parseDouble(fields[5]), fields[6]);
        }
        if (BULK_TYPE.equals(type)) {
            return new BulkPurchaseDiscount(id, name, startDate, endDate,
                Integer.parseInt(fields[5]), Double.parseDouble(fields[6]));
        }
        throw new IllegalArgumentException("Unknown promotion type in CSV: " + type);
    }
}
