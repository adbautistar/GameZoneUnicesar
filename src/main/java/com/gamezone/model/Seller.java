package com.gamezone.model;

/**
 * Represents a seller employed by the store, with an employee code and
 * shift in addition to the attributes shared with every {@link Person}.
 */
public class Seller extends Person {

    private String employeeCode;
    private String shift;

    /**
     * Creates a new seller with the given shared and specific attributes.
     *
     * @param id           the unique identifier of the person
     * @param firstName    the person's first name
     * @param lastName     the person's last name
     * @param phone        the person's phone number
     * @param employeeCode the seller's employee code
     * @param shift        the seller's work shift
     */
    public Seller(String id, String firstName, String lastName, String phone,
                   String employeeCode, String shift) {
        super(id, firstName, lastName, phone);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    /**
     * Returns the employee code of this seller.
     *
     * @return the employee code
     */
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
     * Sets the employee code of this seller.
     *
     * @param employeeCode the new employee code
     */
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    /**
     * Returns the work shift of this seller.
     *
     * @return the work shift
     */
    public String getShift() {
        return shift;
    }

    /**
     * Sets the work shift of this seller.
     *
     * @param shift the new work shift
     */
    public void setShift(String shift) {
        this.shift = shift;
    }
}
