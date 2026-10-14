package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

/**
 * Read-only lookup of customers and sellers by id, for clients that only need
 * to find the people involved in a sale and never register or list people.
 */
public interface PersonLookup {

    /**
     * Finds a customer by id.
     *
     * @param id the customer id
     * @return the matching customer, or {@code null} if none is found
     */
    Customer findCustomerById(String id);

    /**
     * Finds a seller by id.
     *
     * @param id the seller id
     * @return the matching seller, or {@code null} if none is found
     */
    Seller findSellerById(String id);
}
