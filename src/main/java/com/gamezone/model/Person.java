package com.gamezone.model;

/**
 * Represents a person who interacts with the store.
 * Concrete roles must extend this class with their own role-specific attributes.
 */
public abstract class Person {

    private String id;
    private String firstName;
    private String lastName;
    private String phone;

    /**
     * Creates a new person with the given identifying and contact data.
     *
     * @param id        the unique identifier of the person
     * @param firstName the person's first name
     * @param lastName  the person's last name
     * @param phone     the person's phone number
     */
    public Person(String id, String firstName, String lastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Returns the unique identifier of this person.
     *
     * @return the person id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this person.
     *
     * @param id the new person id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the first name of this person.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of this person.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of this person.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of this person.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the phone number of this person.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of this person.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the full name of this person, combining first and last name.
     *
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
