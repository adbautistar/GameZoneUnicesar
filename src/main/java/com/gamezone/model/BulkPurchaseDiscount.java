package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a percentage discount that applies to a sale's entire total
 * only when the sale includes at least a minimum number of products.
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double percentage;

    /**
     * Creates a new bulk purchase discount with the given shared and specific attributes.
     *
     * @param id              the unique identifier of the promotion
     * @param name            the promotion's display name
     * @param startDate       the date the promotion becomes active
     * @param endDate         the date the promotion stops being active
     * @param minimumQuantity the minimum number of products a sale must include
     * @param percentage      the discount percentage, from 0 to 100
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                 int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    /**
     * Returns the minimum number of products a sale must include.
     *
     * @return the minimum quantity
     */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /**
     * Sets the minimum number of products a sale must include.
     *
     * @param minimumQuantity the new minimum quantity
     */
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
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
     * Calculates the discount as a percentage of the sale's total amount,
     * granted only when the sale includes at least the minimum quantity of
     * products.
     *
     * @param sale the sale to evaluate
     * @return the discount amount, in currency, or 0.0 if the sale does not
     *         meet the minimum quantity
     */
    @Override
    public double calculateDiscount(Sale sale) {
        if (sale.getProducts().size() >= minimumQuantity) {
            return sale.getTotalAmount() * percentage / 100;
        }
        return 0.0;
    }
}
