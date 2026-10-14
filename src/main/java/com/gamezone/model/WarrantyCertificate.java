package com.gamezone.model;

/**
 * Lays out the certificate shown to the user, in Spanish, for a
 * {@link Warranty}. Keeping the layout here lets the warranty hierarchy change
 * only for coverage rules and this class change only for presentation.
 */
public class WarrantyCertificate {

    private final Warranty warranty;

    /**
     * Creates the certificate of the given warranty.
     *
     * @param warranty the warranty to describe
     */
    public WarrantyCertificate(Warranty warranty) {
        this.warranty = warranty;
    }

    /**
     * Builds the certificate text: warranty id, type, covered product title,
     * originating sale id, start and end dates, and additional cost.
     *
     * @return the formatted certificate text
     */
    public String render() {
        StringBuilder certificate = new StringBuilder();
        certificate.append("===== Certificado de Garantia =====\n");
        certificate.append("ID Garantia: ").append(warranty.getId()).append("\n");
        certificate.append("Tipo: ").append(warranty.getWarrantyType()).append("\n");
        certificate.append("Producto: ").append(warranty.getProduct().getTitle()).append("\n");
        certificate.append("Venta: ").append(warranty.getSale().getId()).append("\n");
        certificate.append("Fecha de inicio: ").append(warranty.getStartDate()).append("\n");
        certificate.append("Fecha de fin: ").append(warranty.getEndDate()).append("\n");
        certificate.append("Costo adicional: $").append(String.format("%.2f", warranty.getAdditionalCost())).append("\n");
        certificate.append("====================================");
        return certificate.toString();
    }
}
