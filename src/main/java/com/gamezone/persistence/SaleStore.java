package com.gamezone.persistence;

import com.gamezone.model.Sale;

import java.util.List;

/**
 * Abstraction of the storage where sales are kept. {@code SaleService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface SaleStore {

    /**
     * Replaces the stored sales with the given list.
     *
     * @param sales the complete list of sales to store
     */
    void saveAll(List<Sale> sales);

    /**
     * Returns every stored sale record (references kept as ids).
     *
     * @return the stored sale records, or an empty list if nothing is stored yet
     */
    List<SaleRecord> loadAll();
}
