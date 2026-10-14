package com.gamezone.model;

/**
 * Lays out the receipt shown to the user, in Spanish, for a {@link Sale},
 * including the promotion breakdown when a discount was applied. Keeping the
 * layout here lets {@code Sale} change only for sale rules and this class
 * change only for presentation.
 */
public class SaleReceipt {

    private final Sale sale;

    /**
     * Creates the receipt of the given sale.
     *
     * @param sale the sale to describe
     */
    public SaleReceipt(Sale sale) {
        this.sale = sale;
    }

    /**
     * Builds the receipt text: sale id, date, customer, seller, purchased
     * products with their prices and the total, preceded by the subtotal and
     * the discount when a promotion was applied.
     *
     * @return the formatted receipt text
     */
    public String render() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Recibo de Venta =====\n");
        receipt.append("ID Venta: ").append(sale.getId()).append("\n");
        receipt.append("Fecha: ").append(sale.getDate()).append("\n");
        receipt.append("Cliente: ").append(sale.getCustomer().getFullName()).append("\n");
        receipt.append("Vendedor: ").append(sale.getSeller().getFullName()).append("\n");
        receipt.append("Productos:\n");
        for (Product product : sale.getProducts()) {
            receipt.append("  - ").append(product.getTitle())
                   .append(" ($").append(String.format("%.2f", product.getPrice())).append(")\n");
        }
        double totalAmount = sale.getTotalAmount();
        double discountAmount = sale.getDiscountAmount();
        if (sale.getAppliedPromotionName() != null && discountAmount > 0) {
            double subtotal = totalAmount + discountAmount;
            receipt.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
            receipt.append("Descuento (").append(sale.getAppliedPromotionName()).append("): -$")
                   .append(String.format("%.2f", discountAmount)).append("\n");
            receipt.append("Total final: $").append(String.format("%.2f", totalAmount)).append("\n");
        } else {
            receipt.append("Total: $").append(String.format("%.2f", totalAmount)).append("\n");
        }
        receipt.append("============================");
        return receipt.toString();
    }
}
