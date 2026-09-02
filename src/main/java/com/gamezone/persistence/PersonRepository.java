package com.gamezone.persistence;

import com.gamezone.model.Customer;

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
}
