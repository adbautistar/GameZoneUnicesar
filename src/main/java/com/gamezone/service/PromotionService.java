package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromotionService {

    private final PromotionRepository repository;
    private final List<Promotion> promotions;

    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
        this.promotions = new ArrayList<>(repository.loadAll());
    }

    public void registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                            double percentage) {
        validate(percentage, startDate, endDate);
        PercentageDiscount promotion = new PercentageDiscount(id, name, startDate, endDate, percentage);
        promotions.add(promotion);
        repository.saveAll(promotions);
    }

    public void registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                          double percentage, String targetCategory) {
        validate(percentage, startDate, endDate);
        CategoryDiscount promotion = new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        promotions.add(promotion);
        repository.saveAll(promotions);
    }

    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                              int minimumQuantity, double percentage) {
        validate(percentage, startDate, endDate);
        if (minimumQuantity < 1) {
            throw new IllegalArgumentException("La cantidad minima debe ser al menos 1.");
        }
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
        promotions.add(promotion);
        repository.saveAll(promotions);
    }

    private void validate(double percentage, LocalDate startDate, LocalDate endDate) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }
}
