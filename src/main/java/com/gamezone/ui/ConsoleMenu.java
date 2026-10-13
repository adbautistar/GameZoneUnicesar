package com.gamezone.ui;

import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleRegistrationService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar, displaying menus in
 * Spanish and delegating all business operations to the injected services.
 */
public class ConsoleMenu {

    private final WarrantyService warrantyService;
    private final Scanner scanner;
    private final ReturnMenu returnMenu;
    private final PromotionMenu promotionMenu;
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
        this.warrantyService = warrantyService;
        this.scanner = new Scanner(System.in);
        this.returnMenu = new ReturnMenu(scanner, returnService, saleService);
        this.promotionMenu = new PromotionMenu(scanner, promotionService);
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
                    promotionMenu.show();
                    break;
                case "6":
                    returnMenu.show();
                    break;
                case "7":
                    returnMenu.showMonthlyBalance();
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
