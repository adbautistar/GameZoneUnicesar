package com.gamezone.persistence;

import java.time.LocalDate;
import java.util.List;

/**
 * Persisted form of a return: the values stored in {@code data/returns.csv},
 * with the original sale and the returned products kept as ids. The service
 * layer resolves those ids into domain objects.
 */
public class ReturnRecord {

    private final String id;
    private final LocalDate date;
    private final String saleId;
    private final List<String> productIds;
    private final String reason;

    /**
     * Creates a new record with the values read from one CSV line.
     *
     * @param id         the return id
     * @param date       the date the return was registered
     * @param saleId     the id of the original sale
     * @param productIds the ids of the returned products, one entry per unit
     * @param reason     the reason given for the return
     */
    public ReturnRecord(String id, LocalDate date, String saleId, List<String> productIds, String reason) {
        this.id = id;
        this.date = date;
        this.saleId = saleId;
        this.productIds = List.copyOf(productIds);
        this.reason = reason;
    }

    /**
     * Returns the return id.
     *
     * @return the return id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date the return was registered.
     *
     * @return the return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the id of the original sale.
     *
     * @return the sale id
     */
    public String getSaleId() {
        return saleId;
    }

    /**
     * Returns the ids of the returned products, in the order they were stored.
     *
     * @return an unmodifiable list of product ids
     */
    public List<String> getProductIds() {
        return productIds;
    }

    /**
     * Returns the reason given for the return.
     *
     * @return the return reason
     */
    public String getReason() {
        return reason;
    }
}
