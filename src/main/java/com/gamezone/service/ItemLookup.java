package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * Read-only view of every sellable item (products and accessories), for
 * clients that only need to find items by id and never change their stock.
 */
public interface ItemLookup {

    /**
     * Finds a sellable item by id.
     *
     * @param id the item id
     * @return the matching product or accessory, or {@code null} if none exists
     */
    Product findItem(String id);
}
