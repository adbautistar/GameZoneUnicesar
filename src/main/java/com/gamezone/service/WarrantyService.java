package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.model.WarrantyKind;
import com.gamezone.persistence.WarrantyRecord;
import com.gamezone.persistence.WarrantyStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations for assigning and querying warranties.
 */
public class WarrantyService implements WarrantyAssigner {

    private final WarrantyStore repository;
    private final List<Warranty> warranties;

    /**
     * Creates a new service backed by the given repository, loading the
     * current warranties into memory. Stored references are resolved here:
     * a warranty whose product or sale no longer exists is skipped.
     *
     * @param repository  the store used to persist and load warranties
     * @param saleLookup  the read-only view used to resolve the sale of each warranty
     * @param itemLookup  the lookup used to resolve the covered product
     * @throws IllegalArgumentException if a stored warranty has an unknown type
     */
    public WarrantyService(WarrantyStore repository, SaleLookup saleLookup, ItemLookup itemLookup) {
        this.repository = repository;
        this.warranties = new ArrayList<>();
        for (WarrantyRecord record : repository.loadAll()) {
            Warranty warranty = resolve(record, saleLookup, itemLookup);
            if (warranty != null) {
                warranties.add(warranty);
            }
        }
    }

    private static Warranty resolve(WarrantyRecord record, SaleLookup saleLookup, ItemLookup itemLookup) {
        Product product = itemLookup.findItem(record.getProductId());
        if (product == null) {
            System.err.println("Failed to resolve product or accessory " + record.getProductId()
                + " for warranty " + record.getId() + "; skipping.");
            return null;
        }
        Sale sale = saleLookup.findById(record.getSaleId());
        if (sale == null) {
            System.err.println("Failed to resolve sale " + record.getSaleId() + " for warranty " + record.getId()
                + "; skipping.");
            return null;
        }
        return kindOf(record.getType()).create(record.getId(), product, sale, record.getStartDate());
    }

    private static WarrantyKind kindOf(String tag) {
        for (WarrantyKind kind : WarrantyKind.values()) {
            if (kind.name().equals(tag)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown warranty type in CSV: " + tag);
    }

    @Override
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = IdGenerator.next("WAR");
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    @Override
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = IdGenerator.next("WAR");
        ExtendedWarranty warranty = new ExtendedWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    /**
     * Finds the warranty that covers the given product in the given sale.
     *
     * @param productId the id of the product
     * @param saleId    the id of the sale
     * @return the first matching warranty, or {@code null} if none covers that product in that sale
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            if (warranty.covers(productId, saleId)) {
                return warranty;
            }
        }
        return null;
    }

    public List<Warranty> listAllWarranties() {
        return Collections.unmodifiableList(warranties);
    }

    public List<Warranty> listActiveWarranties() {
        List<Warranty> active = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                active.add(warranty);
            }
        }
        return active;
    }

    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);
        List<Warranty> result = new ArrayList<>();
        for (Warranty warranty : warranties) {
            LocalDate endDate = warranty.getEndDate();
            if (!endDate.isBefore(today) && !endDate.isAfter(limit)) {
                result.add(warranty);
            }
        }
        return result;
    }
}
