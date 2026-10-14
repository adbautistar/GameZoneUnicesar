package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for returns, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class ReturnMenu implements MenuOption {

    private final Scanner scanner;
    private final ReturnService returnService;
    private final SaleService saleService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param returnService the service used for return operations
     * @param saleService the service used for sale queries
     */
    public ReturnMenu(Scanner scanner, ReturnService returnService, SaleService saleService) {
        this.scanner = scanner;
        this.returnService = returnService;
        this.saleService = saleService;
    }

    /**
     * Shows the returns submenu and the monthly balance query until the user chooses to go back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de devoluciones -----");
            System.out.println("1. Registrar una nueva devolucion");
            System.out.println("2. Ver todas las devoluciones");
            System.out.println("3. Ver devoluciones por cliente");
            System.out.println("4. Ver devoluciones por venta");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerReturn();
                    break;
                case "2":
                    viewAllReturns();
                    break;
                case "3":
                    viewReturnsByCustomer();
                    break;
                case "4":
                    viewReturnsBySale();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerReturn() {
        try {
            System.out.print("ID de la venta: ");
            String saleId = scanner.nextLine().trim();
            Sale sale = saleService.findById(saleId);
            if (sale == null) {
                System.out.println("Venta no encontrada.");
                return;
            }
            System.out.println("Productos de la venta:");
            for (Product product : sale.getProducts()) {
                System.out.println("  " + product.getId() + " - " + product.getTitle());
            }
            System.out.print("IDs de productos a devolver (separados por coma): ");
            String[] productIdsInput = scanner.nextLine().trim().split(",");
            List<String> productIds = new ArrayList<>();
            for (String productId : productIdsInput) {
                productIds.add(productId.trim());
            }
            System.out.print("Motivo: ");
            String reason = scanner.nextLine().trim();
            Return returnItem = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolucion registrada exitosamente.");
            System.out.println(returnItem.generateReturnReceipt());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la devolucion: " + e.getMessage());
        }
    }

    private void viewAllReturns() {
        List<Return> returns = returnService.viewAllReturns();
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones registradas.");
            return;
        }
        for (Return returnItem : returns) {
            System.out.println(returnItem.generateReturnReceipt());
        }
    }

    private void viewReturnsByCustomer() {
        System.out.print("ID del cliente: ");
        String customerId = scanner.nextLine().trim();
        List<Return> returns = returnService.viewReturnsByCustomer(customerId);
        if (returns.isEmpty()) {
            System.out.println("Este cliente no tiene devoluciones registradas.");
            return;
        }
        for (Return returnItem : returns) {
            System.out.println(returnItem.generateReturnReceipt());
        }
    }

    private void viewReturnsBySale() {
        System.out.print("ID de la venta: ");
        String saleId = scanner.nextLine().trim();
        List<Return> returns = returnService.viewReturnsBySale(saleId);
        if (returns.isEmpty()) {
            System.out.println("Esta venta no tiene devoluciones registradas.");
            return;
        }
        for (Return returnItem : returns) {
            System.out.println(returnItem.generateReturnReceipt());
        }
    }


    /**
     * Returns the label of this submenu in the main menu.
     *
     * @return "Gestion de devoluciones"
     */
    @Override
    public String getLabel() {
        return "Gestion de devoluciones";
    }

    /**
     * Shows this submenu until the user goes back to the main menu.
     */
    @Override
    public void run() {
        show();
    }
}
