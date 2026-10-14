package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRecord;
import com.gamezone.persistence.SaleStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps the sales history: loads it, records new sales and answers queries
 * about it, backed by a {@link SaleStore}. Registering a new sale is
 * coordinated by {@link SaleRegistrationService}.
 */
public class SaleService implements SaleLookup {

    private final SaleStore repository;
    private final PersonLookup personLookup;
    private final ItemCatalog itemCatalog;
    private final List<Sale> sales;

    /**
     * Creates a new service backed by the given repository, loading the
     * current sales history into memory. Stored references are resolved
     * here, and every loaded sale is added to its customer's purchase history.
     *
     * @param repository    the store used to persist and load sales
     * @param personLookup the lookup used to resolve customers and sellers
     * @param itemCatalog   the catalog used to resolve sold items
     * @throws RuntimeException if a stored sale references a customer, seller
     *                          or item that no longer exists
     */
    public SaleService(SaleStore repository, PersonLookup personLookup, ItemCatalog itemCatalog) {
        this.repository = repository;
        this.personLookup = personLookup;
        this.itemCatalog = itemCatalog;
        this.sales = new ArrayList<>();
        for (SaleRecord record : repository.loadAll()) {
            sales.add(resolve(record));
        }
    }

    private Sale resolve(SaleRecord record) {
        Customer customer = personLookup.findCustomerById(record.getCustomerId());
        Seller seller = personLookup.findSellerById(record.getSellerId());
        if (customer == null || seller == null) {
            throw new RuntimeException("Failed to resolve customer or seller for sale " + record.getId());
        }
        List<Product> products = new ArrayList<>();
        for (String productId : record.getProductIds()) {
            Product product = itemCatalog.findItem(productId);
            if (product == null) {
                throw new RuntimeException("Failed to resolve product or accessory " + productId
                    + " for sale " + record.getId());
            }
            products.add(product);
        }
        Sale sale = new Sale(record.getId(), record.getDate(), customer, seller, products);
        sale.calculateTotal();
        if (record.getAppliedPromotionName() != null) {
            sale.applyPromotion(record.getAppliedPromotionName(), record.getDiscountAmount());
        }
        customer.addPurchase(sale);
        return sale;
    }

    /**
     * Records a newly registered sale: adds it to the history and to its
     * customer's purchase history, and persists the updated history.
     *
     * @param sale the sale to record
     */
    public void recordSale(Sale sale) {
        sales.add(sale);
        sale.getCustomer().addPurchase(sale);
        repository.saveAll(sales);
    }

    /**
     * Returns an unmodifiable view of every registered sale.
     *
     * @return the list of all sales
     */
    @Override
    public List<Sale> viewAllSales() {
        return Collections.unmodifiableList(sales);
    }

    /**
     * Returns the sales made by the customer with the given id.
     *
     * @param customerId the id of the customer to filter by
     * @return the list of matching sales
     */
    public List<Sale> viewSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.wasMadeBy(customerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Returns the sales handled by the seller with the given id.
     *
     * @param sellerId the id of the seller to filter by
     * @return the list of matching sales
     */
    public List<Sale> viewSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.wasHandledBy(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Finds a sale by its id.
     *
     * @param id the id to search for
     * @return the matching sale, or {@code null} if none is found
     */
    @Override
    public Sale findById(String id) {
        for (Sale sale : sales) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
    }
}
