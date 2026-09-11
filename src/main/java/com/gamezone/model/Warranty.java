package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a warranty covering a product sold in a sale.
 * Concrete warranty types must extend this class and define their own
 * duration, type label, and additional cost.
 */
public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new warranty starting on the given date. The end date is
     * derived immediately by adding {@link #getDurationInMonths()} months
     * to the start date. This is safe to call from the constructor because
     * every concrete subclass returns a literal constant, independent of
     * any subclass field state.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product this warranty covers
     * @param sale      the sale this warranty was issued for
     * @param startDate the date this warranty starts
     */
    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /**
     * Returns the unique identifier of this warranty.
     *
     * @return the warranty id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the product this warranty covers.
     *
     * @return the covered product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the sale this warranty was issued for.
     *
     * @return the originating sale
     */
    public Sale getSale() {
        return sale;
    }

    /**
     * Returns the date this warranty starts.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the date this warranty ends.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the duration of this warranty type, in months.
     * Each concrete subclass returns its own fixed duration.
     *
     * @return the duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Returns a human-readable label for this warranty type.
     *
     * @return the warranty type label
     */
    public abstract String getWarrantyType();

    /**
     * Returns the additional cost charged for this warranty, in currency.
     *
     * @return the additional cost, or 0.0 if this warranty type has none
     */
    public abstract double getAdditionalCost();

    /**
     * Checks whether this warranty is active on the given date.
     *
     * @param date the date to check
     * @return {@code true} if the date falls within the warranty's vigency,
     *         inclusive of both start and end dates
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Builds a human-readable certificate for this warranty, in Spanish,
     * including the warranty id, type, covered product title, originating
     * sale id, start and end dates, and additional cost.
     *
     * @return the formatted certificate text
     */
    public String generateWarrantyCertificate() {
        StringBuilder certificate = new StringBuilder();
        certificate.append("===== Certificado de Garantia =====\n");
        certificate.append("ID Garantia: ").append(id).append("\n");
        certificate.append("Tipo: ").append(getWarrantyType()).append("\n");
        certificate.append("Producto: ").append(product.getTitle()).append("\n");
        certificate.append("Venta: ").append(sale.getId()).append("\n");
        certificate.append("Fecha de inicio: ").append(startDate).append("\n");
        certificate.append("Fecha de fin: ").append(endDate).append("\n");
        certificate.append("Costo adicional: $").append(String.format("%.2f", getAdditionalCost())).append("\n");
        certificate.append("====================================");
        return certificate.toString();
    }
}
