package com.gamezone.persistence;

import com.gamezone.model.Return;

import java.util.List;

/**
 * Abstraction of the storage where returns are kept. {@code ReturnService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface ReturnStore {

    /**
     * Replaces the stored returns with the given list.
     *
     * @param returns the complete list of returns to store
     */
    void saveAll(List<Return> returns);

    /**
     * Returns every stored return records (references kept as ids).
     *
     * @return the stored return records (references kept as ids), or an empty list if nothing is stored yet
     */
    List<ReturnRecord> loadAll();
}
