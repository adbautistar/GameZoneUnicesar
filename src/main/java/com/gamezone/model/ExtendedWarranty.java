package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents an optional extended warranty, covering a product for 12
 * months at an additional cost of 10% of the product's price.
 */
public class ExtendedWarranty extends Warranty {

    /**
     * Creates a new extended warranty starting on the given date.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product this warranty covers
     * @param sale      the sale this warranty was issued for
     * @param startDate the date this warranty starts
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    /**
     * Returns the duration of an extended warranty: 12 months.
     *
     * @return 12
     */
    @Override
    public int getDurationInMonths() {
        return 12;
    }

    /**
     * Returns the type label for an extended warranty.
     *
     * @return "Garantia Extendida"
     */
    @Override
    public String getWarrantyType() {
        return "Garantia Extendida";
    }

    /**
     * Returns the additional cost of an extended warranty: 10% of the
     * covered product's price.
     *
     * @return the additional cost, in currency
     */
    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * 0.10;
    }
}
