package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a flat percentage discount applied to a sale's entire total.
 */
public class PercentageDiscount extends Promotion {

    private double percentage;

    /**
     * Creates a new percentage discount with the given shared and specific attributes.
     *
     * @param id         the unique identifier of the promotion
     * @param name       the promotion's display name
     * @param startDate  the date the promotion becomes active
     * @param endDate    the date the promotion stops being active
     * @param percentage the discount percentage, from 0 to 100
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
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
     * Calculates the discount as a flat percentage of the sale's total amount.
     *
     * @param sale the sale to evaluate
     * @return the discount amount, in currency
     */
    @Override
    public double calculateDiscount(Sale sale) {
        return sale.getTotalAmount() * percentage / 100;
    }
}
