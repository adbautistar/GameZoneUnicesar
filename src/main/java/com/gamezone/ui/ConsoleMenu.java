package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

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
    private final Scanner scanner;

    /**
     * Creates a new console menu backed by the given services.
     *
     * @param productService the service used for product operations
     * @param personService  the service used for customer and seller operations
     * @param saleService    the service used for sale operations
     */
    public ConsoleMenu(ProductService productService, PersonService personService, SaleService saleService) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
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
}
