package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRecord;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations for assigning and querying warranties.
 */
public class WarrantyService {

    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    /**
     * Creates a new service backed by the given repository, loading the
     * current warranties into memory. Stored references are resolved here:
     * a warranty whose product or sale no longer exists is skipped.
     *
     * @param repository  the repository used to persist and load warranties
     * @param saleService the service used to resolve the sale of each warranty
     * @param itemCatalog the catalog used to resolve the covered product
     * @throws IllegalArgumentException if a stored warranty has an unknown type
     */
    public WarrantyService(WarrantyRepository repository, SaleService saleService, ItemCatalog itemCatalog) {
        this.repository = repository;
        this.warranties = new ArrayList<>();
        for (WarrantyRecord record : repository.loadRecords()) {
            Warranty warranty = resolve(record, saleService, itemCatalog);
            if (warranty != null) {
                warranties.add(warranty);
            }
        }
    }

    /**
     * Creates a new service whose repository resolves references itself.
     *
     * @param repository the repository used to persist and load warranties
     * @deprecated transitional constructor kept only until {@code Main} is
     *             rewired; use {@link #WarrantyService(WarrantyRepository, SaleService, ItemCatalog)}
     */
    @Deprecated
    public WarrantyService(WarrantyRepository repository) {
        this.repository = repository;
        this.warranties = new ArrayList<>(repository.loadAll());
    }

    private static Warranty resolve(WarrantyRecord record, SaleService saleService, ItemCatalog itemCatalog) {
        Product product = itemCatalog.findItem(record.getProductId());
        if (product == null) {
            System.err.println("Failed to resolve product or accessory " + record.getProductId()
                + " for warranty " + record.getId() + "; skipping.");
            return null;
        }
        Sale sale = saleService.findById(record.getSaleId());
        if (sale == null) {
            System.err.println("Failed to resolve sale " + record.getSaleId() + " for warranty " + record.getId()
                + "; skipping.");
            return null;
        }
        if (BASIC_TYPE.equals(record.getType())) {
            return new BasicWarranty(record.getId(), product, sale, record.getStartDate());
        }
        if (EXTENDED_TYPE.equals(record.getType())) {
            return new ExtendedWarranty(record.getId(), product, sale, record.getStartDate());
        }
        throw new IllegalArgumentException("Unknown warranty type in CSV: " + record.getType());
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = IdGenerator.next("WAR");
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

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
