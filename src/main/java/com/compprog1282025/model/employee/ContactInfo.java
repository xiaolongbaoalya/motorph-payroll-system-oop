package com.compprog1282025.model.employee;

// This is a Mini Data Container for Employees' contact information

public class ContactInfo {
    private String address;
    private String phone;

// Constructor - initializes the contact information fields for an employee

    public ContactInfo(String address, String phone) {
    	this.address = address;
    	this.phone = phone;
    }

    // Getters and Setters - allow access and modification of the contact information fields for an employee
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    

    // This makes sure that it returns lr.mabernales@mmdc.edu.ph | 0912345678 | Makati
    @Override
    public String toString() {
        return String.format("%s | %s | %s", phone, address);

    }

}
