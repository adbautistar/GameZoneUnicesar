package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.SaleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar, displaying menus in
 * Spanish and delegating all business operations to the injected services.
 */
public class ConsoleMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final Scanner scanner;

    /**
     * Creates a new console menu backed by the given services.
     *
     * @param productService   the service used for product operations
     * @param personService    the service used for customer and seller operations
     * @param saleService      the service used for sale operations
     * @param accessoryService the service used for accessory operations
     * @param promotionService the service used for promotion operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService,
                        AccessoryService accessoryService, PromotionService promotionService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.scanner = new Scanner(System.in);
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
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    showProductMenu();
                    break;
                case "2":
                    showPersonMenu();
                    break;
                case "3":
                    showSaleMenu();
                    break;
                case "4":
                    showAccessoryMenu();
                    break;
                case "5":
                    showPromotionMenu();
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

    private void showProductMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de productos -----");
            System.out.println("1. Registrar videojuego");
            System.out.println("2. Registrar consola");
            System.out.println("3. Listar todos los productos");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerVideoGame();
                    break;
                case "2":
                    registerConsole();
                    break;
                case "3":
                    listAllProducts();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerVideoGame() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Plataforma: ");
            String platform = scanner.nextLine().trim();
            System.out.print("Genero: ");
            String genre = scanner.nextLine().trim();
            System.out.print("Clasificacion por edad: ");
            String ageRating = scanner.nextLine().trim();
            productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el videojuego: " + e.getMessage());
        }
    }

    private void registerConsole() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Marca: ");
            String brand = scanner.nextLine().trim();
            System.out.print("Modelo: ");
            String model = scanner.nextLine().trim();
            System.out.print("Generacion: ");
            String generation = scanner.nextLine().trim();
            productService.registerConsole(id, title, price, stock, brand, model, generation);
            System.out.println("Consola registrada exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la consola: " + e.getMessage());
        }
    }

    private void listAllProducts() {
        List<Product> products = productService.listAllProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        for (Product product : products) {
            System.out.println(product.getDescription());
        }
    }

    private void showPersonMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de personas -----");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerCustomer();
                    break;
                case "2":
                    listAllCustomers();
                    break;
                case "3":
                    listAllSellers();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private void registerCustomer() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Nombre: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Apellido: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Telefono: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Correo electronico: ");
            String email = scanner.nextLine().trim();
            personService.registerCustomer(id, firstName, lastName, phone, email);
            System.out.println("Cliente registrado exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el cliente: " + e.getMessage());
        }
    }

    private void listAllCustomers() {
        List<Customer> customers = personService.listAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer.getId() + " - " + customer.getFullName() + " - " + customer.getEmail());
        }
    }

    private void listAllSellers() {
        List<Seller> sellers = personService.listAllSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }
        for (Seller seller : sellers) {
            System.out.println(seller.getId() + " - " + seller.getFullName() + " - " + seller.getShift());
        }
    }

    private void showSaleMenu() {
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
            Sale sale = saleService.registerSale(customerId, sellerId, productIds);
            System.out.println("Venta registrada exitosamente.");
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

    private void showAccessoryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----- Gestion de accesorios -----");
            System.out.println("1. Registrar un nuevo control");
            System.out.println("2. Registrar un nuevo cable");
            System.out.println("3. Registrar una nueva memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Consultar accesorios compatibles con una consola");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1":
                    registerController();
                    break;
                case "2":
                    registerCable();
                    break;
                case "3":
                    registerMemory();
                    break;
                case "4":
                    listAllAccessories();
                    break;
                case "5":
                    listAccessoriesByType();
                    break;
                case "6":
                    findAccessoriesCompatibleWith();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private List<String> promptCompatibleConsoleIds() {
        System.out.print("IDs de consolas compatibles (separados por coma): ");
        String input = scanner.nextLine().trim();
        List<String> consoleIds = new ArrayList<>();
        if (!input.isEmpty()) {
            for (String consoleId : input.split(",")) {
                consoleIds.add(consoleId.trim());
            }
        }
        return consoleIds;
    }

    private void registerController() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            List<String> compatibleConsoleIds = promptCompatibleConsoleIds();
            System.out.print("Tipo de conexion (WIRELESS/WIRED): ");
            String connectionType = scanner.nextLine().trim();
            accessoryService.registerController(id, title, price, stock, compatibleConsoleIds, connectionType);
            System.out.println("Control registrado exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el control: " + e.getMessage());
        }
    }

    private void registerCable() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            List<String> compatibleConsoleIds = promptCompatibleConsoleIds();
            System.out.print("Longitud en metros: ");
            double lengthInMeters = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Tipo de conector (ej. HDMI, USB): ");
            String connectorType = scanner.nextLine().trim();
            accessoryService.registerCable(id, title, price, stock, compatibleConsoleIds, lengthInMeters, connectorType);
            System.out.println("Cable registrado exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el cable: " + e.getMessage());
        }
    }

    private void registerMemory() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Titulo: ");
            String title = scanner.nextLine().trim();
            System.out.print("Precio: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("Stock: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            List<String> compatibleConsoleIds = promptCompatibleConsoleIds();
            System.out.print("Capacidad en GB: ");
            int capacityInGB = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Tipo de memoria (SD/MICROSD/INTERNAL): ");
            String memoryType = scanner.nextLine().trim();
            accessoryService.registerMemory(id, title, price, stock, compatibleConsoleIds, capacityInGB, memoryType);
            System.out.println("Memoria registrada exitosamente.");
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la memoria: " + e.getMessage());
        }
    }

    private void listAllAccessories() {
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados.");
            return;
        }
        for (Accessory accessory : accessories) {
            System.out.println(accessory.getDescription());
        }
    }

    private void listAccessoriesByType() {
        System.out.print("Tipo (CONTROLLER/CABLE/MEMORY): ");
        String type = scanner.nextLine().trim();
        List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados de ese tipo.");
            return;
        }
        for (Accessory accessory : accessories) {
            System.out.println(accessory.getDescription());
        }
    }

    private void findAccessoriesCompatibleWith() {
        System.out.print("ID de la consola: ");
        String consoleId = scanner.nextLine().trim();
        List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios compatibles con esa consola.");
            return;
        }
        for (Accessory accessory : accessories) {
            System.out.println(accessory.getDescription());
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
}
