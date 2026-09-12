package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.service.AccessoryService;
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
 * {@code data/sales.csv}, resolving customer, seller, product, and accessory
 * references through the injected {@link ProductService}, {@link PersonService},
 * and {@link AccessoryService}.
 */
public class SaleRepository {

    private static final String DEFAULT_FILE_PATH = "data/sales.csv";

    private final ProductService productService;
    private final PersonService personService;
    private final AccessoryService accessoryService;
    private final String filePath;

    /**
     * Creates a new repository that resolves sale references through the
     * given services, backed by the default {@code data/sales.csv} file.
     *
     * @param productService   the service used to resolve product references
     * @param personService    the service used to resolve customer and seller references
     * @param accessoryService the service used to resolve accessory references
     */
    public SaleRepository(ProductService productService, PersonService personService,
                           AccessoryService accessoryService) {
        this(productService, personService, accessoryService, DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new repository that resolves sale references through the
     * given services, backed by the given file path. Used by tests to
     * isolate file operations from the real {@code data/sales.csv}.
     *
     * @param productService   the service used to resolve product references
     * @param personService    the service used to resolve customer and seller references
     * @param accessoryService the service used to resolve accessory references
     * @param filePath         the CSV file path to read from and write to
     */
    public SaleRepository(ProductService productService, PersonService personService,
                           AccessoryService accessoryService, String filePath) {
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        this.filePath = filePath;
    }

    /**
     * Overwrites the CSV file with the given list of sales.
     *
     * @param sales the complete list of sales to persist
     */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Sale sale : sales) {
                writer.write(toCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sales to " + filePath, e);
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
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return sales;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                sales.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sales from " + filePath, e);
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
        String appliedPromotionName = sale.getAppliedPromotionName() != null ? sale.getAppliedPromotionName() : "";
        return String.join(",",
            sale.getId(),
            sale.getDate().toString(),
            sale.getCustomer().getId(),
            sale.getSeller().getId(),
            productIds.toString(),
            String.valueOf(sale.getTotalAmount()),
            appliedPromotionName,
            String.valueOf(sale.getDiscountAmount()));
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
                product = accessoryService.findById(productId);
            }
            if (product == null) {
                throw new RuntimeException("Failed to resolve product or accessory " + productId + " for sale " + id);
            }
            products.add(product);
        }

        Sale sale = new Sale(id, date, customer, seller, products);
        sale.calculateTotal();
        if (fields.length > 7 && !fields[6].isEmpty()) {
            double discountAmount = Double.parseDouble(fields[7]);
            sale.setAppliedPromotionName(fields[6]);
            sale.setDiscountAmount(discountAmount);
            sale.setTotalAmount(sale.getTotalAmount() - discountAmount);
        }
        customer.addPurchase(sale);
        return sale;
    }
}
