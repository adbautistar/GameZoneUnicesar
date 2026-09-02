package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a sale of one or more products to a customer, handled by a seller.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<Product> products;
    private double totalAmount;

    /**
     * Creates a new sale with the given participants and products. The total
     * amount starts at zero until {@link #calculateTotal()} is invoked.
     *
     * @param id       the unique identifier of the sale
     * @param date     the date the sale was made
     * @param customer the customer who made the purchase
     * @param seller   the seller who handled the sale
     * @param products the products included in the sale
     */
    public Sale(String id, LocalDate date, Customer customer, Seller seller, List<Product> products) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.products = products;
        this.totalAmount = 0.0;
    }

    /**
     * Returns the unique identifier of this sale.
     *
     * @return the sale id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date this sale was made.
     *
     * @return the sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the customer who made this purchase.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the seller who handled this sale.
     *
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Returns the products included in this sale.
     *
     * @return the list of products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Returns the total amount of this sale, as last computed by
     * {@link #calculateTotal()}.
     *
     * @return the total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }
}
