package com.gamezone.ui;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for people management, extracted from
 * {@link ConsoleMenu}. Prompts and messages are shown in Spanish and every
 * operation is delegated to the service layer.
 */
public class PersonMenu implements MenuOption {

    private final Scanner scanner;
    private final PersonService personService;

    /**
     * Creates the submenu.
     *
     * @param scanner the scanner shared with the main menu, reading from standard input
     * @param personService the service used for customer and seller operations
     */
    public PersonMenu(Scanner scanner, PersonService personService) {
        this.scanner = scanner;
        this.personService = personService;
    }

    /**
     * Shows the people management submenu (customers and sellers) until the user chooses to go back.
     */
    public void show() {
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

    /**
     * Returns the label of this submenu in the main menu.
     *
     * @return "Gestion de personas"
     */
    @Override
    public String getLabel() {
        return "Gestion de personas";
    }

    /**
     * Shows this submenu until the user goes back to the main menu.
     */
    @Override
    public void run() {
        show();
    }
}
