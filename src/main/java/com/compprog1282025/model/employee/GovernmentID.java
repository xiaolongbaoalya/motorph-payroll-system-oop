package com.compprog1282025.model.employee;

// This is a Mini Data Container for Employees' government identification numbers

public class GovernmentID {
    private String sss;
    private String philHealth;
    private String tin;
    private String pagIbig;


// Constructor - initializes all the government ID fields for an employee

    public GovernmentID(String sss, String philHealth, String tin, String pagIbig) {
        this.sss = sss;
        this.philHealth = philHealth;
        this.tin = tin;
        this.pagIbig = pagIbig;
    }

// Getters and Setters - allow access and modification of the government ID fields for an employee

    public String getSss() {
        return sss;
    }

    public void setSss(String sss) {
        this.sss = sss;
    }

    public String getPhilHealth() {
        return philHealth;
    }

    public void setPhilHealth(String philHealth) {
        this.philHealth = philHealth;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getPagIbig() {
        return pagIbig;
    }

    public void setPagIbig(String pagIbig) {
        this.pagIbig = pagIbig;
    }

// This makes sure it returns SSS: 12345678 | PhilHealth: 12234234 | TIN: 34324234 | Pag-IBIG: 23423423

    @Override
    public String toString() {
        return String.format("SSS: %s | PhilHealth: %s | TIN: %s | Pag-IBIG: %s", sss, philHealth, tin, pagIbig);
    }

}