package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a promotional discount that can be applied to a sale.
 * Concrete promotion types must extend this class and provide their own
 * discount calculation.
 */
public abstract class Promotion {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new promotion with the given identifying and vigency data.
     *
     * @param id        the unique identifier of the promotion
     * @param name      the promotion's display name
     * @param startDate the date the promotion becomes active
     * @param endDate   the date the promotion stops being active
     */
    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the unique identifier of this promotion.
     *
     * @return the promotion id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this promotion.
     *
     * @param id the new promotion id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the display name of this promotion.
     *
     * @return the promotion name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of this promotion.
     *
     * @param name the new promotion name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the date this promotion becomes active.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the date this promotion becomes active.
     *
     * @param startDate the new start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the date this promotion stops being active.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the date this promotion stops being active.
     *
     * @param endDate the new end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Checks whether this promotion is active on the given date.
     *
     * @param date the date to check
     * @return {@code true} if the date falls within the promotion's vigency,
     *         inclusive of both start and end dates
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the discount this promotion grants for the given sale.
     * Each concrete subclass provides its own implementation.
     *
     * @param sale the sale to evaluate
     * @return the discount amount, in currency
     */
    public abstract double calculateDiscount(Sale sale);
}
