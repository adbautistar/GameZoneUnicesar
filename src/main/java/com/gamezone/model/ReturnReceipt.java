package com.gamezone.model;

/**
 * Lays out the receipt shown to the user, in Spanish, for a registered
 * {@link Return}. Keeping the layout here lets {@code Return} change only for
 * return rules and this class change only for presentation.
 */
public class ReturnReceipt {

    private final Return returnItem;

    /**
     * Creates the receipt of the given return.
     *
     * @param returnItem the return to describe
     */
    public ReturnReceipt(Return returnItem) {
        this.returnItem = returnItem;
    }

    /**
     * Builds the receipt text: return id, date, original sale id, returned
     * products with their prices, reason and refund amount.
     *
     * @return the formatted receipt text
     */
    public String render() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Recibo de Devolucion =====\n");
        receipt.append("ID Devolucion: ").append(returnItem.getId()).append("\n");
        receipt.append("Fecha: ").append(returnItem.getDate()).append("\n");
        receipt.append("Venta original: ").append(returnItem.getOriginalSale().getId()).append("\n");
        receipt.append("Productos devueltos:\n");
        for (Product product : returnItem.getReturnedProducts()) {
            receipt.append("  - ").append(product.getTitle())
                   .append(" ($").append(String.format("%.2f", product.getPrice())).append(")\n");
        }
        receipt.append("Motivo: ").append(returnItem.getReason()).append("\n");
        receipt.append("Monto a reembolsar: $").append(String.format("%.2f", returnItem.getRefundAmount())).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}
