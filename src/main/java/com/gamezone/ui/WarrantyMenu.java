package com.gamezone.ui;

import com.gamezone.model.Warranty;
import com.gamezone.service.WarrantyService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for warranty queries, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class WarrantyMenu {

    private final Scanner scanner;
    private final WarrantyService warrantyService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param warrantyService the service used for warranty operations
     */
    public WarrantyMenu(Scanner scanner, WarrantyService warrantyService) {
        this.scanner = scanner;
        this.warrantyService = warrantyService;
    }

    /**
     * Shows the warranty queries submenu until the user chooses to go back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de garantias -----");
            System.out.println("1. Consultar garantia por producto y venta");
            System.out.println("2. Listar todas las garantias");
            System.out.println("3. Listar garantias vigentes");
            System.out.println("4. Listar garantias proximas a vencer");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    findWarrantyByProduct();
                    break;
                case "2":
                    listAllWarranties();
                    break;
                case "3":
                    listActiveWarranties();
                    break;
                case "4":
                    listWarrantiesExpiringSoon();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void findWarrantyByProduct() {
        System.out.print("ID del producto: ");
        String productId = scanner.nextLine().trim();
        System.out.print("ID de la venta: ");
        String saleId = scanner.nextLine().trim();
        Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
        if (warranty == null) {
            System.out.println("No se encontro garantia.");
            return;
        }
        System.out.println(warranty.generateWarrantyCertificate());
    }

    private void listAllWarranties() {
        List<Warranty> warranties = warrantyService.listAllWarranties();
        if (warranties.isEmpty()) {
            System.out.println("No hay garantias registradas.");
            return;
        }
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    private void listActiveWarranties() {
        List<Warranty> warranties = warrantyService.listActiveWarranties();
        if (warranties.isEmpty()) {
            System.out.println("No hay garantias vigentes.");
            return;
        }
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }

    private void listWarrantiesExpiringSoon() {
        System.out.print("Dias hacia adelante (sugerido: 30): ");
        int daysAhead = Integer.parseInt(scanner.nextLine().trim());
        List<Warranty> warranties = warrantyService.listWarrantiesExpiringSoon(daysAhead);
        if (warranties.isEmpty()) {
            System.out.println("No hay garantias proximas a vencer en ese rango.");
            return;
        }
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
        }
    }
}
