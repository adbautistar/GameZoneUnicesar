package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents the basic warranty automatically granted to every console sold,
 * with no additional cost.
 */
public class BasicWarranty extends Warranty {

    /**
     * Creates a new basic warranty starting on the given date.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product this warranty covers
     * @param sale      the sale this warranty was issued for
     * @param startDate the date this warranty starts
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    /**
     * Returns the duration of a basic warranty: 6 months.
     *
     * @return 6
     */
    @Override
    public int getDurationInMonths() {
        return 6;
    }

    /**
     * Returns the type label for a basic warranty.
     *
     * @return "Garantia Basica"
     */
    @Override
    public String getWarrantyType() {
        return "Garantia Basica";
    }

    /**
     * Returns the additional cost of a basic warranty, which is free.
     *
     * @return 0.0
     */
    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
