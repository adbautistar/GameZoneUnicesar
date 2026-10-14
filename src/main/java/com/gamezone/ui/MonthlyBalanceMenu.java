package com.gamezone.ui;

import com.gamezone.service.MonthlyBalanceService;

import java.util.Scanner;

/**
 * Main-menu option that asks for a month and a year and prints the balance of
 * sales minus refunds for that period. Moved out of {@link ReturnMenu} together
 * with the extraction of {@code MonthlyBalanceService}.
 */
public class MonthlyBalanceMenu implements MenuOption {

    private final Scanner scanner;
    private final MonthlyBalanceService monthlyBalanceService;

    /**
     * Creates the option.
     *
     * @param scanner               the scanner shared with the main menu, reading from standard input
     * @param monthlyBalanceService the service used to compute the monthly balance
     */
    public MonthlyBalanceMenu(Scanner scanner, MonthlyBalanceService monthlyBalanceService) {
        this.scanner = scanner;
        this.monthlyBalanceService = monthlyBalanceService;
    }

    /**
     * Returns the label of this option in the main menu.
     *
     * @return "Consultar balance mensual"
     */
    @Override
    public String getLabel() {
        return "Consultar balance mensual";
    }

    /**
     * Asks for a month and a year and prints the balance of sales minus refunds for that period.
     */
    @Override
    public void run() {
        try {
            System.out.print("Mes (1-12): ");
            int month = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Anio (yyyy): ");
            int year = Integer.parseInt(scanner.nextLine().trim());
            double balance = monthlyBalanceService.generateMonthlyBalance(month, year);
            System.out.println("Balance del mes " + month + "/" + year + ": $" + String.format("%.2f", balance));
        } catch (RuntimeException e) {
            System.out.println("Error al consultar el balance: " + e.getMessage());
        }
    }
}
