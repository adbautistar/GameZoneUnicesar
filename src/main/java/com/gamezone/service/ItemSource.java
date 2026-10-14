package com.gamezone.service;

import com.gamezone.model.Product;

/**
 * A catalog that owns some of the sellable items (for example, products or
 * accessories) and keeps their stock. {@link ItemCatalog} asks its sources
 * instead of checking the concrete type of an item, so a new catalog is added
 * by implementing this interface, without modifying {@code ItemCatalog}.
 *
 * <p>Stock methods are only called with ids that this source owns, that is,
 * ids for which {@link #findItem(String)} returned an item.</p>
 */
public interface ItemSource {

    /**
     * Finds an item owned by this source.
     *
     * @param id the item id
     * @return the item, or {@code null} if this source does not own it
     */
    Product findItem(String id);

    /**
     * Decreases by one the stock of an item owned by this source, after a sale,
     * and persists the change.
     *
     * @param id the id of the sold item
     */
    void decreaseStock(String id);

    /**
     * Restores by one the stock of an item owned by this source, after a
     * return, and persists the change.
     *
     * @param id the id of the returned item
     */
    void restoreStock(String id);
}
