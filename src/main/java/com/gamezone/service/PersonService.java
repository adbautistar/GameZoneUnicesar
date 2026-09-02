package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonService {

    private final PersonRepository repository;
    private final List<Customer> customers;
    private final List<Seller> sellers;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
        this.customers = new ArrayList<>(repository.loadAllCustomers());
        this.sellers = new ArrayList<>(repository.loadAllSellers());
    }

    public void registerCustomer(String id, String firstName, String lastName, String phone, String email) {
        Customer customer = new Customer(id, firstName, lastName, phone, email);
        customers.add(customer);
        repository.saveAllCustomers(customers);
    }

    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public Seller findSellerById(String id) {
        for (Seller seller : sellers) {
            if (seller.getId().equals(id)) {
                return seller;
            }
        }
        return null;
    }

    public List<Customer> listAllCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public List<Seller> listAllSellers() {
        return Collections.unmodifiableList(sellers);
    }
}
