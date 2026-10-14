package com.gamezone.service;

import com.gamezone.model.Sale;

import java.util.List;

/**
 * Read-only view of the registered sales, for clients that only need to read
 * them (returns, warranties, reports) and never record or filter sales.
 */
public interface SaleLookup {

    /**
     * Finds a sale by its id.
     *
     * @param id the id to search for
     * @return the matching sale, or {@code null} if none is found
     */
    Sale findById(String id);

    /**
     * Returns every registered sale.
     *
     * @return an unmodifiable list of all sales
     */
    List<Sale> viewAllSales();
}
