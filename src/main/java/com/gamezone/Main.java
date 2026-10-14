package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ItemCatalog;
import com.gamezone.service.MonthlyBalanceService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleRegistrationService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.AccessoryMenu;
import com.gamezone.ui.ConsoleMenu;
import com.gamezone.ui.MenuOption;
import com.gamezone.ui.MonthlyBalanceMenu;
import com.gamezone.ui.PersonMenu;
import com.gamezone.ui.ProductMenu;
import com.gamezone.ui.PromotionMenu;
import com.gamezone.ui.ReturnMenu;
import com.gamezone.ui.SaleMenu;
import com.gamezone.ui.WarrantyMenu;

import java.util.List;
import java.util.Scanner;

/**
 * Application entry point for GameZone Unicesar. Wires the persistence,
 * service, and UI layers together and launches the console menu.
 */
public class Main {

    /**
     * Loads all repositories and services, composes the main-menu options in
     * their display order, then starts the console menu.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ProductRepository productRepository = new ProductRepository();
            PersonRepository personRepository = new PersonRepository();
            AccessoryRepository accessoryRepository = new AccessoryRepository();
            PromotionRepository promotionRepository = new PromotionRepository();

            ProductService productService = new ProductService(productRepository);
            PersonService personService = new PersonService(personRepository);
            AccessoryService accessoryService = new AccessoryService(accessoryRepository);
            PromotionService promotionService = new PromotionService(promotionRepository);

            ItemCatalog itemCatalog = new ItemCatalog(productService, accessoryService);

            SaleRepository saleRepository = new SaleRepository();
            SaleService saleService = new SaleService(saleRepository, personService, itemCatalog);

            ReturnRepository returnRepository = new ReturnRepository();
            ReturnService returnService = new ReturnService(returnRepository, saleService, itemCatalog);
            MonthlyBalanceService monthlyBalanceService = new MonthlyBalanceService(saleService, returnService);

            WarrantyRepository warrantyRepository = new WarrantyRepository();
            WarrantyService warrantyService = new WarrantyService(warrantyRepository, saleService, itemCatalog);

            SaleRegistrationService saleRegistrationService = new SaleRegistrationService(saleService, personService,
                itemCatalog, promotionService, warrantyService);

            Scanner scanner = new Scanner(System.in);
            List<MenuOption> options = List.of(
                new ProductMenu(scanner, productService),
                new PersonMenu(scanner, personService),
                new SaleMenu(scanner, saleService, saleRegistrationService),
                new AccessoryMenu(scanner, accessoryService),
                new PromotionMenu(scanner, promotionService),
                new ReturnMenu(scanner, returnService, saleService),
                new MonthlyBalanceMenu(scanner, monthlyBalanceService),
                new WarrantyMenu(scanner, warrantyService));
            ConsoleMenu consoleMenu = new ConsoleMenu(scanner, options);
            consoleMenu.start();
        } catch (RuntimeException e) {
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
