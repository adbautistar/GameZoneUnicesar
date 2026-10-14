package com.gamezone.service;

import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;

/**
 * What the sale registration use case needs from promotions: choosing the
 * best one for a sale. Defined by the use case (the high-level policy) and
 * implemented by the promotion service, so the use case does not depend on
 * how promotions are registered, stored or listed.
 */
public interface PromotionSelector {

    /**
     * Finds the active promotion that gives the largest discount for the
     * given sale.
     *
     * @param sale the sale to evaluate, with its total already calculated
     * @return the best promotion, or {@code null} if none gives a discount
     */
    Promotion findBestPromotionFor(Sale sale);
}
