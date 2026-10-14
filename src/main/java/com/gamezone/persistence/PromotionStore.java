package com.gamezone.persistence;

import com.gamezone.model.Promotion;

import java.util.List;

/**
 * Abstraction of the storage where promotions are kept. {@code PromotionService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface PromotionStore {

    /**
     * Replaces the stored promotions with the given list.
     *
     * @param promotions the complete list of promotions to store
     */
    void saveAll(List<Promotion> promotions);

    /**
     * Returns every stored promotions.
     *
     * @return the stored promotions, or an empty list if nothing is stored yet
     */
    List<Promotion> loadAll();
}
