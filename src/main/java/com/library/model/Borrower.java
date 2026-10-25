package com.library.model;

import java.io.Serializable;

public class Borrower implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String contactNumber;
    private String email; // used for sending due-date / overdue reminders
    private String type; // e.g. Student, Faculty, Staff

    public Borrower(int id, String name, String contactNumber, String email, String type) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.type = type;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
