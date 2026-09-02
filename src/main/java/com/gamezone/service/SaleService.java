package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the business operations available for registering and querying
 * sales, backed by a {@link SaleRepository}.
 */
public class SaleService {

    private final SaleRepository repository;
    private final ProductService productService;
    private final PersonService personService;
    private final List<Sale> sales;

    /**
     * Creates a new service backed by the given repository and collaborating
     * services, loading the current sales history into memory.
     *
     * @param repository     the repository used to persist and load sales
     * @param productService the service used to resolve and update products
     * @param personService  the service used to resolve customers and sellers
     */
    public SaleService(SaleRepository repository, ProductService productService, PersonService personService) {
        this.repository = repository;
        this.productService = productService;
        this.personService = personService;
        this.sales = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new sale after validating that it contains at least one
     * product, that the customer, seller, and products exist, and that
     * enough stock is available for every requested product. Decrements the
     * stock of every sold product, records the sale in the customer's
     * purchase history, and persists the updated sales history.
     *
     * @param customerId the id of the customer making the purchase
     * @param sellerId   the id of the seller handling the sale
     * @param productIds the ids of the products being sold, one entry per
     *                   unit purchased
     * @return the registered sale
     * @throws IllegalArgumentException if the product list is empty, if the
     *                                  customer, seller, or a product cannot
     *                                  be found, or if stock is insufficient
     */
    public Sale registerSale(String customerId, String sellerId, List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        Customer customer = personService.findCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + customerId);
        }
        Seller seller = personService.findSellerById(sellerId);
        if (seller == null) {
            throw new IllegalArgumentException("Vendedor no encontrado: " + sellerId);
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productService.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + productId);
            }
            products.add(product);
        }

        Map<String, Integer> requestedQuantities = new HashMap<>();
        for (Product product : products) {
            requestedQuantities.merge(product.getId(), 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (product.getStock() < entry.getValue()) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + product.getTitle());
            }
        }

        String saleId = "SALE-" + System.currentTimeMillis();
        Sale sale = new Sale(saleId, LocalDate.now(), customer, seller, products);
        sale.calculateTotal();

        for (Product product : products) {
            productService.updateStock(product.getId(), -1);
        }

        sales.add(sale);
        customer.addPurchase(sale);
        repository.saveAll(sales);

        return sale;
    }
}
