package com.gamezone.ui;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for GameZone Unicesar. Shows the main menu in
 * Spanish, numbering the options it receives in order, and runs the option
 * the user chooses. It does not know any concrete submenu: a new module is
 * added by giving it one more {@link MenuOption}.
 */
public class ConsoleMenu {

    private final Scanner scanner;
    private final List<MenuOption> options;

    /**
     * Creates a new console menu with the given options.
     *
     * @param scanner the scanner reading from standard input, shared with the options
     * @param options the main-menu options, shown as 1, 2, 3, ... in this order
     */
    public ConsoleMenu(Scanner scanner, List<MenuOption> options) {
        this.scanner = scanner;
        this.options = List.copyOf(options);
    }

    /**
     * Starts the main menu loop until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== GameZone Unicesar =====");
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i + 1) + ". " + options.get(i).getLabel());
            }
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            String option = scanner.nextLine().trim();
            if ("0".equals(option)) {
                running = false;
                System.out.println("Gracias por usar GameZone Unicesar.");
                continue;
            }
            MenuOption chosen = find(option);
            if (chosen == null) {
                System.out.println("Opcion invalida.");
            } else {
                chosen.run();
            }
        }
    }

    private MenuOption find(String option) {
        for (int i = 0; i < options.size(); i++) {
            if (String.valueOf(i + 1).equals(option)) {
                return options.get(i);
            }
        }
        return null;
    }
}
