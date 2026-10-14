package com.gamezone.persistence;

import com.gamezone.model.Accessory;

import java.util.List;

/**
 * Abstraction of the storage where accessories are kept. {@code AccessoryService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface AccessoryStore {

    /**
     * Replaces the stored accessories with the given list.
     *
     * @param accessories the complete list of accessories to store
     */
    void saveAll(List<Accessory> accessories);

    /**
     * Returns every stored accessories.
     *
     * @return the stored accessories, or an empty list if nothing is stored yet
     */
    List<Accessory> loadAll();
}
