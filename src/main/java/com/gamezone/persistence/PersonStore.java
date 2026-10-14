package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Abstraction of the storage where customers and sellers are kept. {@code PersonService}
 * depends on this interface instead of a concrete repository; it lives in the
 * persistence package so that the dependency still points from the service
 * layer to the persistence layer.
 */
public interface PersonStore {

    /**
     * Replaces the stored customers with the given list.
     *
     * @param customers the complete list of customers to store
     */
    void saveAllCustomers(List<Customer> customers);

    /**
     * Returns every stored customers.
     *
     * @return the stored customers, or an empty list if nothing is stored yet
     */
    List<Customer> loadAllCustomers();

    /**
     * Replaces the stored sellers with the given list.
     *
     * @param sellers the complete list of sellers to store
     */
    void saveAllSellers(List<Seller> sellers);

    /**
     * Returns every stored sellers.
     *
     * @return the stored sellers, or an empty list if nothing is stored yet
     */
    List<Seller> loadAllSellers();
}
