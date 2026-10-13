package com.gamezone.ui;

import com.gamezone.model.Promotion;
import com.gamezone.service.PromotionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for promotion management, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class PromotionMenu {

    private final Scanner scanner;
    private final PromotionService promotionService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param promotionService the service used for promotion operations
     */
    public PromotionMenu(Scanner scanner, PromotionService promotionService) {
        this.scanner = scanner;
        this.promotionService = promotionService;
    }

    /**
     * Shows the promotion management submenu until the user chooses to go back.
     */
    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de promociones -----");
            System.out.println("1. Registrar una nueva promocion por porcentaje");
            System.out.println("2. Registrar una nueva promocion por categoria");
            System.out.println("3. Registrar una nueva promocion por volumen");
            System.out.println("4. Listar todas las promociones");
            System.out.println("5. Listar promociones vigentes");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerPercentageDiscount();
                    break;
                case "2":
                    registerCategoryDiscount();
                    break;
                case "3":
                    registerBulkPurchaseDiscount();
                    break;
                case "4":
                    listAllPromotions();
                    break;
                case "5":
                    listActivePromotions();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerPercentageDiscount() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Nombre: ");
            String name = scanner.nextLine().trim();
            System.out.print("Fecha de inicio (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Fecha de fin (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());
            promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
            System.out.println("Promocion registrada exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la promocion: " + e.getMessage());
        }
    }

    private void registerCategoryDiscount() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Nombre: ");
            String name = scanner.nextLine().trim();
            System.out.print("Fecha de inicio (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Fecha de fin (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());
            System.out.println("Categoria objetivo:");
            System.out.println("1. Videojuegos");
            System.out.println("2. Consolas");
            System.out.print("Seleccione una opcion: ");
            String categoryOption = scanner.nextLine().trim();
            String targetCategory = "2".equals(categoryOption) ? "CONSOLE" : "VIDEOGAME";
            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
            System.out.println("Promocion registrada exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la promocion: " + e.getMessage());
        }
    }

    private void registerBulkPurchaseDiscount() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Nombre: ");
            String name = scanner.nextLine().trim();
            System.out.print("Fecha de inicio (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Fecha de fin (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Cantidad minima de productos: ");
            int minimumQuantity = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Porcentaje de descuento: ");
            double percentage = Double.parseDouble(scanner.nextLine().trim());
            promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
            System.out.println("Promocion registrada exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la promocion: " + e.getMessage());
        }
    }

    private void listAllPromotions() {
        List<Promotion> promotions = promotionService.listAllPromotions();
        if (promotions.isEmpty()) {
            System.out.println("No hay promociones registradas.");
            return;
        }
        for (Promotion promotion : promotions) {
            System.out.println(promotion.getId() + " - " + promotion.getName()
                + " (" + promotion.getStartDate() + " a " + promotion.getEndDate() + ")");
        }
    }

    private void listActivePromotions() {
        List<Promotion> promotions = promotionService.listActivePromotions();
        if (promotions.isEmpty()) {
            System.out.println("No hay promociones vigentes.");
            return;
        }
        for (Promotion promotion : promotions) {
            System.out.println(promotion.getId() + " - " + promotion.getName()
                + " (" + promotion.getStartDate() + " a " + promotion.getEndDate() + ")");
        }
    }
}
