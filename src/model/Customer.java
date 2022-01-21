package model;

import java.sql.Timestamp;

/**
 * All variables for Customer table
 */
public class Customer {
    private  int customerID;
    private  String customerName;
    private  String customerAddress;
    private  String customerZipCode;
    private  String customerPhone;
    private Timestamp created;
    private String createdBy;
    private Timestamp updated;
    private String updatedBy;
    private  int divisionID;
    private  String division;
    private int countryID;
    private String country;


    /**
     * @param customerID Customer_ID
     * @param customerName Customer_Name
     * @param customerAddress Address
     * @param customerZipCode Postal_Code
     * @param customerPhone Phone
     * @param created Create_Date
     * @param createdBy Created_By
     * @param updated Last_Update
     * @param updatedBy Last_Updated_By
     * @param divisionID Division_ID
     * @param division Division
     * @param countryID Country_ID
     * @param country Country
     */
    public Customer(int customerID, String customerName, String customerAddress, String customerZipCode, String customerPhone, Timestamp created, String createdBy, Timestamp updated, String updatedBy, int divisionID, String division, int countryID, String country) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerZipCode = customerZipCode;
        this.customerPhone = customerPhone;
        this.created = created;
        this.createdBy = createdBy;
        this.updated = updated;
        this.updatedBy = updatedBy;
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
        this.country = country;
    }


    /**
     * set and get all variables for Customer table
     */
    public int getCustomerID(){return customerID;}
    public  void setCustomerID(int customerID){ this.customerID = customerID;}


    public  String getCustomerName(){return customerName;}
    public void setCustomerName(String customerName){this.customerName = customerName;}

    public  String getCustomerAddress(){return customerAddress;}
    public void setCustomerAddress(String customerAddress){this.customerAddress = customerAddress;}

    public  String getCustomerZipCode(){return customerZipCode;}
    public void setCustomerZipCode(String customerZipCode){this.customerZipCode = customerZipCode;}

    public  String getCustomerPhone(){return customerPhone;}
    public void setCustomerPhone(String customerPhone){this.customerPhone = customerPhone;}

    public Timestamp getCreated(){return created;}
    public void setCreated(Timestamp created){this.created =created;}

    public String getCreatedBy(){return createdBy;}
    public void setCreatedBy(String createdBy){this.createdBy = createdBy;}

    public Timestamp getUpdated(){return  updated;}
    public void setUpdated(Timestamp updated){this.updated = updated;}

    public String getUpdatedBy(){return updatedBy;}
    public void setUpdatedBy(String updatedBy){this.updatedBy = updatedBy;}

    public  int getDivisionID(){return divisionID;}
    public void setDivisionID(int divisionID){this.divisionID = divisionID;}

    public  String getDivision(){return division;}
    public void setDivision(String division){this.division = division;}

    public int getCountryID(){return countryID;}
    public void setCountryID(int countryID){this.countryID = countryID;}

    public  String getCountry(){return country;}
    public void setCountry(String country){this.country = country;}

    /**
     * @return Customer_Name as string
     */
    @Override
    public String toString(){
        return customerName;}
    }



