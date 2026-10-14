package com.gamezone.persistence;

import com.gamezone.model.Product;

import java.util.List;

/**
 * Abstraction of the storage where products are kept. {@code ProductService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface ProductStore {

    /**
     * Replaces the stored products with the given list.
     *
     * @param products the complete list of products to store
     */
    void saveAll(List<Product> products);

    /**
     * Returns every stored product.
     *
     * @return the stored products, or an empty list if nothing is stored yet
     */
    List<Product> loadAll();
}
