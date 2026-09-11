package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the return of one or more products from a previously
 * registered sale.
 */
public class Return {

    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Creates a new return referencing the original sale and the products
     * being returned from it. The refund amount is computed immediately by
     * summing the price of every returned product.
     *
     * @param id               the unique identifier of the return
     * @param date             the date the return was registered
     * @param originalSale     the sale this return refers to
     * @param returnedProducts the products being returned
     * @param reason           the reason given for the return
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    /**
     * Returns the unique identifier of this return.
     *
     * @return the return id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date this return was registered.
     *
     * @return the return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the sale this return refers to.
     *
     * @return the original sale
     */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /**
     * Returns the products being returned.
     *
     * @return the list of returned products
     */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    /**
     * Returns the reason given for this return.
     *
     * @return the return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason given for this return.
     *
     * @param reason the new return reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Returns the refund amount for this return.
     *
     * @return the refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Sets the refund amount for this return.
     *
     * @param refundAmount the new refund amount
     */
    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * Calculates the refund amount by summing the price of every returned
     * product, and stores it as this return's refund amount.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double sum = 0.0;
        for (Product product : returnedProducts) {
            sum += product.getPrice();
        }
        this.refundAmount = sum;
        return refundAmount;
    }

    /**
     * Builds a human-readable receipt for this return, in Spanish, including
     * the return id, date, original sale id, returned products, reason, and
     * refund amount.
     *
     * @return the formatted receipt text
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Recibo de Devolucion =====\n");
        receipt.append("ID Devolucion: ").append(id).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Venta original: ").append(originalSale.getId()).append("\n");
        receipt.append("Productos devueltos:\n");
        for (Product product : returnedProducts) {
            receipt.append("  - ").append(product.getTitle())
                   .append(" ($").append(String.format("%.2f", product.getPrice())).append(")\n");
        }
        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append("Monto a reembolsar: $").append(String.format("%.2f", refundAmount)).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}
