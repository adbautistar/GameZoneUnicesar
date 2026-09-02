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

public class PersonRepository {

    private static final String CUSTOMERS_FILE_PATH = "data/customers.csv";
    private static final String SELLERS_FILE_PATH = "data/sellers.csv";

    public void saveAllCustomers(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE_PATH))) {
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
            throw new RuntimeException("Failed to save customers to " + CUSTOMERS_FILE_PATH, e);
        }
    }

    public List<Customer> loadAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        Path path = Path.of(CUSTOMERS_FILE_PATH);
        if (!Files.exists(path)) {
            return customers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                customers.add(new Customer(fields[0], fields[1], fields[2], fields[3], fields[4]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customers from " + CUSTOMERS_FILE_PATH, e);
        }
        return customers;
    }

    public void saveAllSellers(List<Seller> sellers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SELLERS_FILE_PATH))) {
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
            throw new RuntimeException("Failed to save sellers to " + SELLERS_FILE_PATH, e);
        }
    }

    public List<Seller> loadAllSellers() {
        List<Seller> sellers = new ArrayList<>();
        Path path = Path.of(SELLERS_FILE_PATH);
        if (!Files.exists(path)) {
            return sellers;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(SELLERS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                sellers.add(new Seller(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sellers from " + SELLERS_FILE_PATH, e);
        }
        return sellers;
    }
}
