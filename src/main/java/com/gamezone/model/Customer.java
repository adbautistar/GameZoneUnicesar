package com.gamezone.model;

public class Customer extends Person {

    private String email;

    public Customer(String id, String firstName, String lastName, String phone, String email) {
        super(id, firstName, lastName, phone);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
