package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations available for managing customers and
 * sellers, backed by a {@link PersonRepository}.
 */
public class PersonService {

    private final PersonRepository repository;
    private final List<Customer> customers;
    private final List<Seller> sellers;

    /**
     * Creates a new service backed by the given repository, loading the
     * current customers and sellers into memory.
     *
     * @param repository the repository used to persist and load people
     */
    public PersonService(PersonRepository repository) {
        this.repository = repository;
        this.customers = new ArrayList<>(repository.loadAllCustomers());
        this.sellers = new ArrayList<>(repository.loadAllSellers());
    }

    /**
     * Registers a new customer and persists the change.
     *
     * @param id        the unique identifier of the person
     * @param firstName the person's first name
     * @param lastName  the person's last name
     * @param phone     the person's phone number
     * @param email     the customer's email address
     */
    public void registerCustomer(String id, String firstName, String lastName, String phone, String email) {
        Customer customer = new Customer(id, firstName, lastName, phone, email);
        customers.add(customer);
        repository.saveAllCustomers(customers);
    }

    /**
     * Finds a customer by its id.
     *
     * @param id the id to search for
     * @return the matching customer, or {@code null} if none is found
     */
    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Finds a seller by its id.
     *
     * @param id the id to search for
     * @return the matching seller, or {@code null} if none is found
     */
    public Seller findSellerById(String id) {
        for (Seller seller : sellers) {
            if (seller.getId().equals(id)) {
                return seller;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of every registered customer.
     *
     * @return the list of all customers
     */
    public List<Customer> listAllCustomers() {
        return Collections.unmodifiableList(customers);
    }

    /**
     * Returns an unmodifiable view of every registered seller.
     *
     * @return the list of all sellers
     */
    public List<Seller> listAllSellers() {
        return Collections.unmodifiableList(sellers);
    }
}
