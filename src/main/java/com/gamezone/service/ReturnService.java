package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnRecord;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations for registering and querying product
 * returns, and the monthly balance of sales minus refunds.
 */
public class ReturnService {

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final ItemCatalog itemCatalog;
    private final List<Return> returns;

    /**
     * Creates a new service backed by the given repository, loading the
     * current returns into memory. Stored references are resolved here: a
     * return whose sale no longer exists is skipped, and a product that no
     * longer exists is left out of its return.
     *
     * @param repository  the repository used to persist and load returns
     * @param saleService the service used to find the original sales
     * @param itemCatalog the catalog used to resolve returned items and restore their stock
     */
    public ReturnService(ReturnRepository repository, SaleService saleService, ItemCatalog itemCatalog) {
        this.repository = repository;
        this.saleService = saleService;
        this.itemCatalog = itemCatalog;
        this.returns = new ArrayList<>();
        for (ReturnRecord record : repository.loadAll()) {
            Return returnItem = resolve(record);
            if (returnItem != null) {
                returns.add(returnItem);
            }
        }
    }

    /**
     * Registers the return of the given products from the given sale, after
     * checking that the sale exists, that it is within the 30-day window and
     * that every product belongs to it. Restores the stock of every returned
     * item and persists the updated list of returns.
     *
     * @param saleId     the id of the original sale
     * @param productIds the ids of the returned products, one entry per unit
     * @param reason     the reason given for the return
     * @return the registered return
     * @throws IllegalArgumentException if the sale does not exist, is older
     *                                  than 30 days, or does not include a product
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = saleService.findById(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("Venta no encontrada.");
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("La venta excede el plazo de 30 dias para devolucion.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product matched = sale.findProduct(productId);
            if (matched == null) {
                throw new IllegalArgumentException("El producto " + productId + " no pertenece a la venta original.");
            }
            returnedProducts.add(matched);
        }

        String returnId = IdGenerator.next("RET");
        Return newReturn = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason);

        for (Product product : returnedProducts) {
            itemCatalog.restoreStock(product);
        }

        returns.add(newReturn);
        repository.saveAll(returns);

        return newReturn;
    }

    private Return resolve(ReturnRecord record) {
        Sale sale = saleService.findById(record.getSaleId());
        if (sale == null) {
            System.err.println("Failed to resolve sale " + record.getSaleId() + " for return " + record.getId()
                + "; skipping.");
            return null;
        }
        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : record.getProductIds()) {
            Product product = itemCatalog.findItem(productId);
            if (product == null) {
                System.err.println("Failed to resolve product or accessory " + productId
                    + " for return " + record.getId() + "; skipping item.");
                continue;
            }
            returnedProducts.add(product);
        }
        return new Return(record.getId(), record.getDate(), sale, returnedProducts, record.getReason());
    }

    public List<Return> viewAllReturns() {
        return Collections.unmodifiableList(returns);
    }

    /**
     * Returns the returns whose original sale was made by the given customer.
     *
     * @param customerId the id of the customer
     * @return the matching returns
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();
        for (Return returnItem : returns) {
            if (returnItem.belongsToCustomer(customerId)) {
                result.add(returnItem);
            }
        }
        return result;
    }

    /**
     * Returns the returns registered against the given sale.
     *
     * @param saleId the id of the sale
     * @return the matching returns
     */
    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (Return returnItem : returns) {
            if (returnItem.belongsToSale(saleId)) {
                result.add(returnItem);
            }
        }
        return result;
    }

    /**
     * Computes the balance of the given month: the total of the sales made in
     * that month minus the refunds of the returns registered in that month.
     *
     * @param month the month, from 1 to 12
     * @param year  the year
     * @return total sales minus total refunds for that month
     */
    public double generateMonthlyBalance(int month, int year) {
        double totalSales = 0.0;
        for (Sale sale : saleService.viewAllSales()) {
            if (sale.wasMadeIn(month, year)) {
                totalSales += sale.getTotalAmount();
            }
        }

        double totalReturns = 0.0;
        for (Return returnItem : returns) {
            if (returnItem.wasRegisteredIn(month, year)) {
                totalReturns += returnItem.getRefundAmount();
            }
        }

        return totalSales - totalReturns;
    }
}
