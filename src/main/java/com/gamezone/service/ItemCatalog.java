package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Product;

/**
 * Single entry point for every sellable item, whether it is a product
 * (video game or console) or an accessory. Clients that need to find an item
 * by id or adjust its stock talk to this intermediary instead of talking to
 * {@link ProductService} and {@link AccessoryService} separately.
 *
 * <p>This is the only class that knows which catalog owns an item, so the
 * lookup order and the stock dispatch are written once.</p>
 */
public class ItemCatalog implements ItemLookup {

    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates a new catalog over the given product and accessory services.
     *
     * @param productService   the service that owns video games and consoles
     * @param accessoryService the service that owns accessories
     */
    public ItemCatalog(ProductService productService, AccessoryService accessoryService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Finds a sellable item by id, looking first among products and then
     * among accessories.
     *
     * @param id the item id
     * @return the matching product or accessory, or {@code null} if none exists
     */
    @Override
    public Product findItem(String id) {
        Product item = productService.findById(id);
        if (item == null) {
            item = accessoryService.findById(id);
        }
        return item;
    }

    /**
     * Decreases by one the stock of the given item, in the catalog that owns it,
     * and persists the change.
     *
     * @param item the sold item
     */
    public void decreaseStock(Product item) {
        if (item instanceof Accessory) {
            accessoryService.updateStock(item.getId(), -1);
        } else {
            productService.updateStock(item.getId(), -1);
        }
    }

    /**
     * Restores by one the stock of the given item, in the catalog that owns it,
     * and persists the change.
     *
     * @param item the returned item
     */
    public void restoreStock(Product item) {
        if (item instanceof Accessory) {
            accessoryService.updateStock(item.getId(), 1);
        } else {
            productService.restoreStock(item.getId(), 1);
        }
    }
}
