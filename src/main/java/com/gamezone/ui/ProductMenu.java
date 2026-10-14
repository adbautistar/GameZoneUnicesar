package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.service.ProductService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for product management, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class ProductMenu implements MenuOption {

    private final Scanner scanner;
    private final ProductService productService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param productService the service used for product operations
     */
    public ProductMenu(Scanner scanner, ProductService productService) {
        this.scanner = scanner;
        this.productService = productService;
    }

    /**
     * Shows the product management submenu (video games and consoles) until the user chooses to go back.
     */
    public void show() {
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

    /**
     * Returns the label of this submenu in the main menu.
     *
     * @return "Gestion de productos"
     */
    @Override
    public String getLabel() {
        return "Gestion de productos";
    }

    /**
     * Shows this submenu until the user goes back to the main menu.
     */
    @Override
    public void run() {
        show();
    }
}
