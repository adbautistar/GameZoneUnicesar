package com.gamezone.service;

import com.gamezone.model.Product;

import java.util.List;

/**
 * Single entry point for every sellable item, whether it is a product
 * (video game or console) or an accessory. Clients that need to find an item
 * by id or adjust its stock talk to this intermediary instead of talking to
 * each catalog separately.
 *
 * <p>The catalog does not know the concrete catalogs: it asks its
 * {@link ItemSource}s, in the order they were given, and lets the source that
 * owns an item adjust its stock. A new kind of sellable item is added by
 * passing one more source, without modifying this class.</p>
 */
public class ItemCatalog implements ItemLookup {

    private final List<ItemSource> sources;

    /**
     * Creates a new catalog over the given sources. Lookups ask the sources
     * in this order, so the first source that owns an id wins.
     *
     * @param sources the item sources, for example the product and accessory services
     */
    public ItemCatalog(ItemSource... sources) {
        this.sources = List.of(sources);
    }

    /**
     * Finds a sellable item by id, asking the sources in order.
     *
     * @param id the item id
     * @return the matching item, or {@code null} if no source owns it
     */
    @Override
    public Product findItem(String id) {
        for (ItemSource source : sources) {
            Product item = source.findItem(id);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * Decreases by one the stock of the given item, in the source that owns
     * it, and persists the change. Does nothing if no source owns the item.
     *
     * @param item the sold item
     */
    public void decreaseStock(Product item) {
        ItemSource owner = ownerOf(item);
        if (owner != null) {
            owner.decreaseStock(item.getId());
        }
    }

    /**
     * Restores by one the stock of the given item, in the source that owns
     * it, and persists the change. Does nothing if no source owns the item.
     *
     * @param item the returned item
     */
    public void restoreStock(Product item) {
        ItemSource owner = ownerOf(item);
        if (owner != null) {
            owner.restoreStock(item.getId());
        }
    }

    private ItemSource ownerOf(Product item) {
        for (ItemSource source : sources) {
            if (source.findItem(item.getId()) != null) {
                return source;
            }
        }
        return null;
    }
}
