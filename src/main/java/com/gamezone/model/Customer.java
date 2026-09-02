package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer of the store, with an email address in addition to
 * the attributes shared with every {@link Person}.
 */
public class Customer extends Person {

    private String email;
    private List<Sale> purchaseHistory;

    /**
     * Creates a new customer with the given shared and specific attributes.
     *
     * @param id        the unique identifier of the person
     * @param firstName the person's first name
     * @param lastName  the person's last name
     * @param phone     the person's phone number
     * @param email     the customer's email address
     */
    public Customer(String id, String firstName, String lastName, String phone, String email) {
        super(id, firstName, lastName, phone);
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    /**
     * Returns the email address of this customer.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this customer.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the list of sales this customer has made.
     *
     * @return the purchase history
     */
    public List<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }

    /**
     * Appends a sale to this customer's purchase history.
     *
     * @param sale the sale to add
     */
    public void addPurchase(Sale sale) {
        purchaseHistory.add(sale);
    }
}
