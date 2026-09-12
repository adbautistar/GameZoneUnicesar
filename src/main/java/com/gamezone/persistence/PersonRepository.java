package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Customer} and {@link Seller}
 * instances, using two separate CSV files: {@code data/customers.csv} and
 * {@code data/sellers.csv}.
 */
public class PersonRepository {

    private static final String DEFAULT_CUSTOMERS_FILE_PATH = "data/customers.csv";
    private static final String DEFAULT_SELLERS_FILE_PATH = "data/sellers.csv";

    private final String customersFilePath;
    private final String sellersFilePath;

    /**
     * Creates a repository backed by the default {@code data/customers.csv}
     * and {@code data/sellers.csv} files.
     */
    public PersonRepository() {
        this(DEFAULT_CUSTOMERS_FILE_PATH, DEFAULT_SELLERS_FILE_PATH);
    }

    /**
     * Creates a repository backed by the given file paths. Used by tests to
     * isolate file operations from the real customers/sellers CSV files.
     *
     * @param customersFilePath the CSV file path for customers
     * @param sellersFilePath   the CSV file path for sellers
     */
    public PersonRepository(String customersFilePath, String sellersFilePath) {
        this.customersFilePath = customersFilePath;
        this.sellersFilePath = sellersFilePath;
    }

    /**
     * Overwrites the customers CSV file with the given list of customers.
     *
     * @param customers the complete list of customers to persist
     */
    public void saveAllCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customersFilePath))) {
            for (Customer customer : customers) {
                writer.write(String.join(",",
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getPhone(),
                    customer.getEmail()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customers to " + customersFilePath, e);
        }
    }

    /**
     * Reads the customers CSV file and reconstructs the list of customers.
     *
     * @return the list of customers found in the file, or an empty list if
     *         the file does not exist
     */
    public List<Customer> loadAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        Path path = Path.of(customersFilePath);
        if (!Files.exists(path)) {
            return customers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(customersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                customers.add(new Customer(fields[0], fields[1], fields[2], fields[3], fields[4]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customers from " + customersFilePath, e);
        }
        return customers;
    }

    /**
     * Overwrites the sellers CSV file with the given list of sellers.
     *
     * @param sellers the complete list of sellers to persist
     */
    public void saveAllSellers(List<Seller> sellers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sellersFilePath))) {
            for (Seller seller : sellers) {
                writer.write(String.join(",",
                    seller.getId(),
                    seller.getFirstName(),
                    seller.getLastName(),
                    seller.getPhone(),
                    seller.getEmployeeCode(),
                    seller.getShift()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sellers to " + sellersFilePath, e);
        }
    }

    /**
     * Reads the sellers CSV file and reconstructs the list of sellers.
     *
     * @return the list of sellers found in the file, or an empty list if
     *         the file does not exist
     */
    public List<Seller> loadAllSellers() {
        List<Seller> sellers = new ArrayList<>();
        Path path = Path.of(sellersFilePath);
        if (!Files.exists(path)) {
            return sellers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(sellersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                sellers.add(new Seller(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sellers from " + sellersFilePath, e);
        }
        return sellers;
    }
}
