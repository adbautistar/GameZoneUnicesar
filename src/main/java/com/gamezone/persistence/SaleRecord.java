package com.gamezone.persistence;

import java.time.LocalDate;
import java.util.List;

/**
 * Persisted form of a sale: the values stored in {@code data/sales.csv}, with
 * the customer, the seller and the sold items kept as ids. The service layer
 * resolves those ids into domain objects.
 */
public class SaleRecord {

    private final String id;
    private final LocalDate date;
    private final String customerId;
    private final String sellerId;
    private final List<String> productIds;
    private final double totalAmount;
    private final String appliedPromotionName;
    private final double discountAmount;

    /**
     * Creates a new record with the values read from one CSV line.
     *
     * @param id                   the sale id
     * @param date                 the sale date
     * @param customerId           the id of the customer
     * @param sellerId             the id of the seller
     * @param productIds           the ids of the sold items, one entry per unit
     * @param totalAmount          the total stored in the file
     * @param appliedPromotionName the name of the applied promotion, or {@code null} if none
     * @param discountAmount       the stored discount amount (0.0 if no promotion)
     */
    public SaleRecord(String id, LocalDate date, String customerId, String sellerId, List<String> productIds,
                      double totalAmount, String appliedPromotionName, double discountAmount) {
        this.id = id;
        this.date = date;
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.productIds = List.copyOf(productIds);
        this.totalAmount = totalAmount;
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
    }

    /**
     * Returns the sale id.
     *
     * @return the sale id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the sale date.
     *
     * @return the sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the id of the customer.
     *
     * @return the customer id
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Returns the id of the seller.
     *
     * @return the seller id
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * Returns the ids of the sold items, in the order they were stored.
     *
     * @return an unmodifiable list of item ids
     */
    public List<String> getProductIds() {
        return productIds;
    }

    /**
     * Returns the total stored in the file.
     *
     * @return the stored total
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Returns the name of the applied promotion.
     *
     * @return the promotion name, or {@code null} if no promotion was applied
     */
    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    /**
     * Returns the stored discount amount.
     *
     * @return the discount amount, or 0.0 if no promotion was applied
     */
    public double getDiscountAmount() {
        return discountAmount;
    }
}
