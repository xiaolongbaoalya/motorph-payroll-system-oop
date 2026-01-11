package com.compprog1282025.model;

public class ContactInfo {
    private String address, phoneNumber;

    public ContactInfo(String address, String phoneNumber) {
    this.address = address;
    this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
