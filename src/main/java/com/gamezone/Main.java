package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.ConsoleMenu;

/**
 * Application entry point for GameZone Unicesar. Wires the persistence,
 * service, and UI layers together and launches the console menu.
 */
public class Main {

    /**
     * Loads all repositories and services, then starts the console menu.
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

            SaleRepository saleRepository = new SaleRepository(productService, personService, accessoryService);
            SaleService saleService = new SaleService(saleRepository, productService, personService,
                accessoryService, promotionService);

            ReturnRepository returnRepository = new ReturnRepository(saleService, productService, accessoryService);
            ReturnService returnService = new ReturnService(returnRepository, saleService, productService,
                accessoryService);

            ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService, accessoryService,
                promotionService, returnService);
            consoleMenu.start();
        } catch (RuntimeException e) {
            System.err.println("Error fatal: " + e.getMessage());
            System.exit(1);
        }
    }
}
