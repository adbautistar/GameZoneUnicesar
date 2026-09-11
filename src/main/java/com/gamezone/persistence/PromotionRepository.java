package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
}
