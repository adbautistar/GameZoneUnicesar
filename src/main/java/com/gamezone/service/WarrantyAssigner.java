package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;

import java.time.LocalDate;

/**
 * What the sale registration use case needs from warranties: assigning them
 * to the products of a sale. Defined by the use case (the high-level policy)
 * and implemented by the warranty service, so the use case does not depend on
 * how warranties are stored or queried.
 */
public interface WarrantyAssigner {

    /**
     * Assigns and records a basic warranty for a product of a sale.
     *
     * @param product   the covered product
     * @param sale      the sale the product belongs to
     * @param startDate the date the warranty starts
     * @return the assigned warranty
     */
    BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate);

    /**
     * Assigns and records an extended warranty for a product of a sale.
     *
     * @param product   the covered product
     * @param sale      the sale the product belongs to
     * @param startDate the date the warranty starts
     * @return the assigned warranty, whose additional cost is charged to the sale
     */
    ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate);
}
