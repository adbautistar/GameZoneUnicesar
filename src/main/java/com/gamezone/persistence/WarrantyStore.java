package com.gamezone.persistence;

import com.gamezone.model.Warranty;

import java.util.List;

/**
 * Abstraction of the storage where warranties are kept. {@code WarrantyService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface WarrantyStore {

    /**
     * Replaces the stored warranties with the given list.
     *
     * @param warranties the complete list of warranties to store
     */
    void saveAll(List<Warranty> warranties);

    /**
     * Returns every stored warranty records (references kept as ids).
     *
     * @return the stored warranty records (references kept as ids), or an empty list if nothing is stored yet
     */
    List<WarrantyRecord> loadAll();
}
