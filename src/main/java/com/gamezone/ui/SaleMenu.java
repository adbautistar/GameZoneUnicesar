package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.service.SaleRegistrationService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for sales, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class SaleMenu {

    private final Scanner scanner;
    private final SaleService saleService;
    private final SaleRegistrationService saleRegistrationService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param saleService the service used for sale queries
     * @param saleRegistrationService the use case used to register new sales
     */
    public SaleMenu(Scanner scanner, SaleService saleService, SaleRegistrationService saleRegistrationService) {
        this.scanner = scanner;
        this.saleService = saleService;
        this.saleRegistrationService = saleRegistrationService;
    }

    /**
     * Shows the sales submenu (registration and sales history) until the user chooses to go back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de ventas -----");
            System.out.println("1. Registrar venta");
            System.out.println("2. Ver historial completo de ventas");
            System.out.println("3. Ver ventas por cliente");
            System.out.println("4. Ver ventas por vendedor");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerSale();
                    break;
                case "2":
                    viewAllSales();
                    break;
                case "3":
                    viewSalesByCustomer();
                    break;
                case "4":
                    viewSalesBySeller();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerSale() {
        try {
            System.out.print("ID del cliente: ");
            String customerId = scanner.nextLine().trim();
            System.out.print("ID del vendedor: ");
            String sellerId = scanner.nextLine().trim();
            System.out.println("Puede ingresar identificadores de productos o accesorios.");
            System.out.print("Cantidad de productos a vender: ");
            int count = Integer.parseInt(scanner.nextLine().trim());
            List<String> productIds = new ArrayList<>();
            for (int i = 1; i <= count; i++) {
                System.out.print("ID del producto o accesorio " + i + ": ");
                productIds.add(scanner.nextLine().trim());
            }

            List<String> extendedWarrantyIds = new ArrayList<>();
            List<Product> eligibleItems = saleRegistrationService.findWarrantyEligibleItems(productIds);
            for (Product item : eligibleItems) {
                System.out.print("Desea agregar garantia extendida a " + item.getTitle() + "? (S/N): ");
                String answer = scanner.nextLine().trim();
                if ("S".equalsIgnoreCase(answer)) {
                    extendedWarrantyIds.add(item.getId());
                }
            }

            Sale sale = saleRegistrationService.registerSale(customerId, sellerId, productIds, extendedWarrantyIds);
            System.out.println("Venta registrada exitosamente.");
            for (Product item : eligibleItems) {
                System.out.println("Garantia basica asignada automaticamente a " + item.getTitle() + ".");
            }
            System.out.println(sale.generateReceipt());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la venta: " + e.getMessage());
        }
    }

    private void viewAllSales() {
        List<Sale> sales = saleService.viewAllSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        for (Sale sale : sales) {
            System.out.println(sale.generateReceipt());
        }
    }

    private void viewSalesByCustomer() {
        System.out.print("ID del cliente: ");
        String customerId = scanner.nextLine().trim();
        List<Sale> sales = saleService.viewSalesByCustomer(customerId);
        if (sales.isEmpty()) {
            System.out.println("Este cliente no tiene ventas registradas.");
            return;
        }
        for (Sale sale : sales) {
            System.out.println(sale.generateReceipt());
        }
    }

    private void viewSalesBySeller() {
        System.out.print("ID del vendedor: ");
        String sellerId = scanner.nextLine().trim();
        List<Sale> sales = saleService.viewSalesBySeller(sellerId);
        if (sales.isEmpty()) {
            System.out.println("Este vendedor no tiene ventas registradas.");
            return;
        }
        for (Sale sale : sales) {
            System.out.println(sale.generateReceipt());
        }
    }
}
