package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use-case controller for registering a sale. Coordinates the collaborators
 * involved in a sale — people, the item catalog, promotions, warranties and
 * the sale history — so that each of them keeps a single responsibility and
 * none of them needs to know the others.
 */
public class SaleRegistrationService {

    private final SaleService saleService;
    private final PersonService personService;
    private final ItemCatalog itemCatalog;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    /**
     * Creates a new sale registration use case.
     *
     * @param saleService      the service that records and stores sales
     * @param personService    the service used to resolve customers and sellers
     * @param itemCatalog      the catalog used to resolve sold items and update their stock
     * @param promotionService the service used to find the best active promotion
     * @param warrantyService  the service used to assign warranties
     */
    public SaleRegistrationService(SaleService saleService, PersonService personService, ItemCatalog itemCatalog,
                                    PromotionService promotionService, WarrantyService warrantyService) {
        this.saleService = saleService;
        this.personService = personService;
        this.itemCatalog = itemCatalog;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale after validating that it contains at least one
     * item, that the customer, seller and items exist, and that enough stock
     * is available. Applies the best active promotion, assigns a basic
     * warranty to every warranty-eligible item and an extended warranty to
     * every opted-in item (adding its cost to the total), decrements stock
     * and records the sale.
     *
     * @param customerId                     the id of the customer making the purchase
     * @param sellerId                       the id of the seller handling the sale
     * @param productIds                     the ids of the items being sold, one entry per unit
     * @param productIdsWithExtendedWarranty the ids of the items for which an extended
     *                                       warranty was opted into; may be {@code null} or empty
     * @return the registered sale
     * @throws IllegalArgumentException if the item list is empty, if the
     *                                  customer, seller or an item cannot be
     *                                  found, or if stock is insufficient
     */
    public Sale registerSale(String customerId, String sellerId, List<String> productIds,
                              List<String> productIdsWithExtendedWarranty) {
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
            Product product = itemCatalog.findItem(productId);
            if (product == null) {
                throw new IllegalArgumentException("Producto o accesorio no encontrado: " + productId);
            }
            products.add(product);
        }
        checkStock(products);

        Sale sale = new Sale(IdGenerator.next("SALE"), LocalDate.now(), customer, seller, products);
        sale.calculateTotal();

        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            sale.applyPromotion(bestPromotion.getName(), bestPromotion.calculateDiscount(sale));
        }

        for (Product product : products) {
            if (product.isWarrantyEligible()) {
                warrantyService.assignBasicWarranty(product, sale, sale.getDate());
            }
            if (productIdsWithExtendedWarranty != null
                    && productIdsWithExtendedWarranty.contains(product.getId())) {
                ExtendedWarranty extended = warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                sale.addCharge(extended.getAdditionalCost());
            }
        }

        for (Product product : products) {
            itemCatalog.decreaseStock(product);
        }

        saleService.recordSale(sale);
        return sale;
    }

    private void checkStock(List<Product> products) {
        Map<String, Product> resolvedItems = new HashMap<>();
        Map<String, Integer> requestedQuantities = new HashMap<>();
        for (Product product : products) {
            resolvedItems.put(product.getId(), product);
            requestedQuantities.merge(product.getId(), 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = resolvedItems.get(entry.getKey());
            if (product.getStock() < entry.getValue()) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + product.getTitle());
            }
        }
    }
}
