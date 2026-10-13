package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.service.AccessoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for accessory management, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class AccessoryMenu {

    private final Scanner scanner;
    private final AccessoryService accessoryService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param accessoryService the service used for accessory operations
     */
    public AccessoryMenu(Scanner scanner, AccessoryService accessoryService) {
        this.scanner = scanner;
        this.accessoryService = accessoryService;
    }

    /**
     * Shows the accessory management submenu until the user chooses to go back.
     */
    public void show() {
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
}
