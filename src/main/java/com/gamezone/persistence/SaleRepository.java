package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Sale} instances as CSV records in
 * {@code data/sales.csv}, resolving customer, seller, and product references
 * through the injected {@link ProductService} and {@link PersonService}.
 */
public class SaleRepository {

    private static final String FILE_PATH = "data/sales.csv";

    private final ProductService productService;
    private final PersonService personService;

    /**
     * Creates a new repository that resolves sale references through the
     * given services.
     *
     * @param productService the service used to resolve product references
     * @param personService  the service used to resolve customer and seller references
     */
    public SaleRepository(ProductService productService, PersonService personService) {
        this.productService = productService;
        this.personService = personService;
    }

    /**
     * Overwrites the CSV file with the given list of sales.
     *
     * @param sales the complete list of sales to persist
     */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sales to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of sales, resolving each
     * sale's customer, seller, and products by id.
     *
     * @return the list of sales found in the file, or an empty list if the
     *         file does not exist
     */
    public List<Sale> loadAll() {
        List<Sale> sales = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return sales;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                sales.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sales from " + FILE_PATH, e);
        }
        return sales;
    }

    private String toCsvLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds.append(";");
            }
            productIds.append(products.get(i).getId());
        }
        return String.join(",",
            sale.getId(),
            sale.getDate().toString(),
            sale.getCustomer().getId(),
            sale.getSeller().getId(),
            productIds.toString(),
            String.valueOf(sale.getTotalAmount()));
    }

    private Sale fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        String customerId = fields[2];
        String sellerId = fields[3];
        String[] productIds = fields[4].isEmpty() ? new String[0] : fields[4].split(";");

        Customer customer = personService.findCustomerById(customerId);
        Seller seller = personService.findSellerById(sellerId);
        if (customer == null || seller == null) {
            throw new RuntimeException("Failed to resolve customer or seller for sale " + id);
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productService.findById(productId);
            if (product == null) {
                throw new RuntimeException("Failed to resolve product " + productId + " for sale " + id);
            }
            products.add(product);
        }

        Sale sale = new Sale(id, date, customer, seller, products);
        sale.calculateTotal();
        customer.addPurchase(sale);
        return sale;
    }
}
