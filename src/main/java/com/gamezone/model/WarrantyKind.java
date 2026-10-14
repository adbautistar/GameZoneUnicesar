package com.gamezone.model;

import java.time.LocalDate;

/**
 * The kinds of warranty the store offers, each one able to build its own
 * {@link Warranty}. This is the single place that lists the kinds: adding a
 * kind means adding a constant here and its subclass, without modifying the
 * classes that store or rebuild warranties.
 */
public enum WarrantyKind {

    /** Six-month warranty with no additional cost. */
    BASIC {
        @Override
        public Warranty create(String id, Product product, Sale sale, LocalDate startDate) {
            return new BasicWarranty(id, product, sale, startDate);
        }
    },

    /** Twelve-month warranty that costs 10% of the product price. */
    EXTENDED {
        @Override
        public Warranty create(String id, Product product, Sale sale, LocalDate startDate) {
            return new ExtendedWarranty(id, product, sale, startDate);
        }
    };

    /**
     * Builds a warranty of this kind.
     *
     * @param id        the warranty id
     * @param product   the covered product
     * @param sale      the sale the product was sold in
     * @param startDate the date the warranty starts
     * @return a new warranty of this kind
     */
    public abstract Warranty create(String id, Product product, Sale sale, LocalDate startDate);
}
