package com.gamezone.persistence;

import java.time.LocalDate;

/**
 * Persisted form of a warranty: the values stored in
 * {@code data/warranties.csv}, with the covered product and the sale kept as
 * ids. The service layer resolves those ids into domain objects.
 */
public class WarrantyRecord {

    private final String type;
    private final String id;
    private final String productId;
    private final String saleId;
    private final LocalDate startDate;

    /**
     * Creates a new record with the values read from one CSV line.
     *
     * @param type      the warranty type tag ("BASIC" or "EXTENDED")
     * @param id        the warranty id
     * @param productId the id of the covered product
     * @param saleId    the id of the sale the product was sold in
     * @param startDate the date the warranty starts
     */
    public WarrantyRecord(String type, String id, String productId, String saleId, LocalDate startDate) {
        this.type = type;
        this.id = id;
        this.productId = productId;
        this.saleId = saleId;
        this.startDate = startDate;
    }

    /**
     * Returns the warranty type tag.
     *
     * @return "BASIC" or "EXTENDED" (any other value is invalid data)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the warranty id.
     *
     * @return the warranty id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the id of the covered product.
     *
     * @return the product id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Returns the id of the sale the product was sold in.
     *
     * @return the sale id
     */
    public String getSaleId() {
        return saleId;
    }

    /**
     * Returns the date the warranty starts.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
}
