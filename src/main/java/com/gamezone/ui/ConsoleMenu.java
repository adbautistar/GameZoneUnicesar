package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleRegistrationService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar, displaying menus in
 * Spanish and delegating all business operations to the injected services.
 */
public class ConsoleMenu {

    private final SaleService saleService;
    private final PromotionService promotionService;
    private final ReturnService returnService;
    private final WarrantyService warrantyService;
    private final Scanner scanner;
    private final AccessoryMenu accessoryMenu;
    private final SaleMenu saleMenu;
    private final PersonMenu personMenu;
    private final ProductMenu productMenu;

    /**
     * Creates a new console menu backed by the given services.
     *
     * @param productService   the service used for product operations
     * @param personService    the service used for customer and seller operations
     * @param saleService      the service used for sale queries
     * @param saleRegistrationService the use case used to register new sales
     * @param accessoryService the service used for accessory operations
     * @param promotionService the service used for promotion operations
     * @param returnService    the service used for return operations
     * @param warrantyService  the service used for warranty operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService,
                        SaleRegistrationService saleRegistrationService,
                        AccessoryService accessoryService, PromotionService promotionService,
                        ReturnService returnService, WarrantyService warrantyService) {
        this.saleService = saleService;
        this.promotionService = promotionService;
        this.returnService = returnService;
        this.warrantyService = warrantyService;
        this.scanner = new Scanner(System.in);
        this.accessoryMenu = new AccessoryMenu(scanner, accessoryService);
        this.saleMenu = new SaleMenu(scanner, saleService, saleRegistrationService);
        this.personMenu = new PersonMenu(scanner, personService);
        this.productMenu = new ProductMenu(scanner, productService);
    }

    /**
     * Starts the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== GameZone Unicesar =====");
            System.out.println("1. Gestion de productos");
            System.out.println("2. Gestion de personas");
            System.out.println("3. Gestion de ventas");
            System.out.println("4. Gestion de accesorios");
            System.out.println("5. Gestion de promociones");
            System.out.println("6. Gestion de devoluciones");
            System.out.println("7. Consultar balance mensual");
            System.out.println("8. Gestion de garantias");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    productMenu.show();
                    break;
                case "2":
                    personMenu.show();
                    break;
                case "3":
                    saleMenu.show();
                    break;
                case "4":
                    accessoryMenu.show();
                    break;
                case "5":
                    showPromotionMenu();
                    break;
                case "6":
                    showReturnMenu();
                    break;
                case "7":
                    showMonthlyBalance();
                    break;
                case "8":
                    showWarrantyMenu();
                    break;
                case "0":
                    running = false;
                    System.out.println("Gracias por usar GameZone Unicesar.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void showPromotionMenu() {
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

    private void showReturnMenu() {
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

    private void showMonthlyBalance() {
        try {
            System.out.print("Mes (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Anio (yyyy): ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            double balance = returnService.generateMonthlyBalance(month, year);
            System.out.println("Balance del mes " + month + "/" + year + ": $" + String.format("%.2f", balance));
        } catch (RuntimeException e) {
            System.out.println("Error al consultar el balance: " + e.getMessage());
        }
    }

    private void showWarrantyMenu() {
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
