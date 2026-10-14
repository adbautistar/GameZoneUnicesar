package com.gamezone.ui;

import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.MonthlyBalanceService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleRegistrationService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar. Shows the main menu in
 * Spanish and delegates each module to its own submenu class, which in turn
 * delegates every operation to the service layer.
 */
public class ConsoleMenu {

    private final Scanner scanner;
    private final ProductMenu productMenu;
    private final PersonMenu personMenu;
    private final SaleMenu saleMenu;
    private final AccessoryMenu accessoryMenu;
    private final PromotionMenu promotionMenu;
    private final ReturnMenu returnMenu;
    private final MonthlyBalanceMenu monthlyBalanceMenu;
    private final WarrantyMenu warrantyMenu;

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
     * @param monthlyBalanceService the service used to compute the monthly balance
     * @param warrantyService  the service used for warranty operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService,
                        SaleRegistrationService saleRegistrationService,
                        AccessoryService accessoryService, PromotionService promotionService,
                        ReturnService returnService, MonthlyBalanceService monthlyBalanceService,
                        WarrantyService warrantyService) {
        this.scanner = new Scanner(System.in);
        this.productMenu = new ProductMenu(scanner, productService);
        this.personMenu = new PersonMenu(scanner, personService);
        this.saleMenu = new SaleMenu(scanner, saleService, saleRegistrationService);
        this.accessoryMenu = new AccessoryMenu(scanner, accessoryService);
        this.promotionMenu = new PromotionMenu(scanner, promotionService);
        this.returnMenu = new ReturnMenu(scanner, returnService, saleService);
        this.monthlyBalanceMenu = new MonthlyBalanceMenu(scanner, monthlyBalanceService);
        this.warrantyMenu = new WarrantyMenu(scanner, warrantyService);
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
                    monthlyBalanceMenu.run();
                    break;
                case "8":
                    warrantyMenu.show();
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
}
