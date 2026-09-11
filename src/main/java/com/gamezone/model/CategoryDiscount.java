package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a percentage discount that applies only to products of a
 * specific category ("VIDEOGAME" or "CONSOLE") within a sale.
 */
public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    /**
     * Creates a new category discount with the given shared and specific attributes.
     *
     * @param id             the unique identifier of the promotion
     * @param name           the promotion's display name
     * @param startDate      the date the promotion becomes active
     * @param endDate        the date the promotion stops being active
     * @param percentage     the discount percentage, from 0 to 100
     * @param targetCategory the category this discount applies to ("VIDEOGAME" or "CONSOLE")
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                             double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
    }

    /**
     * Returns the discount percentage.
     *
     * @return the discount percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Sets the discount percentage.
     *
     * @param percentage the new discount percentage
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Returns the category this discount applies to.
     *
     * @return the target category
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * Sets the category this discount applies to.
     *
     * @param targetCategory the new target category
     */
    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    /**
     * Calculates the discount as a percentage of the total price of only the
     * products in the sale that match the target category.
     *
     * @param sale the sale to evaluate
     * @return the discount amount, in currency, or 0.0 if no product matches
     */
    @Override
    public double calculateDiscount(Sale sale) {
        double matchingTotal = 0.0;
        for (Product product : sale.getProducts()) {
            if ("VIDEOGAME".equals(targetCategory) && product instanceof VideoGame) {
                matchingTotal += product.getPrice();
            } else if ("CONSOLE".equals(targetCategory) && product instanceof Console) {
                matchingTotal += product.getPrice();
            }
        }
        return matchingTotal * percentage / 100;
    }
}
