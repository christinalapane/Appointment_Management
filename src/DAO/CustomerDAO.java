package DAO;

import Database.DBConnection;
import controller.LoginPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.*;
import java.time.LocalDateTime;

public class CustomerDAO {


    /**
     * @return all information from customer table
     * @throws SQLException in case of SQL error
     * @throws Exception in case of SQL error
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException, Exception {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "SELECT customers.*, first_level_divisions.Division, first_level_divisions.COUNTRY_ID, countries.Country FROM customers, first_level_divisions, countries WHERE customers.Division_ID=first_level_divisions.Division_ID and first_level_divisions.COUNTRY_ID = countries.Country_ID";
        Query.makeQuery(sqlStatement);
        Customer customerResult;
        ResultSet result = Query.getResult();
        while (result.next()) {
            int customerID = result.getInt("Customer_ID");
            String name = result.getString("Customer_Name");
            String address = result.getString("Address");
            String zip = result.getString("Postal_Code");
            String phone = result.getString("Phone");
            Timestamp created = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp updated = result.getTimestamp("Last_Update");
            String updatedBy = result.getString("Last_Updated_By");
            int divisionID = result.getInt("Division_ID");
            String division = result.getString("Division");
            int countryID = result.getInt("Country_ID");
            String country = result.getString("Country");
            customerResult = new Customer(customerID, name, address, zip, phone, created, createdBy, updated, updatedBy, divisionID, division, countryID, country);
            allCustomers.add(customerResult);
        }
        return allCustomers;
    }

    /**
     * adds a new customer to the database
     * @param name Customer_Name
     * @param address Address
     * @param zip Postal_Code
     * @param phone Phone
     * @param divisionID Division_ID
     * @throws SQLException in case of SQL error
     */
    public static void addCustomer(String name, String address, String zip, String phone, int divisionID) throws SQLException {
        try {
            String sql = "INSERT INTO customers VALUE(NULL,?, ?, ?, ?, ?, ?, ?, ?, ?); ";

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, zip);
            ps.setString(4, phone);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, LoginPage.getLoggedOnUser().getUsername());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, LoginPage.getLoggedOnUser().getUsername());
            ps.setInt(9, divisionID);

            int n = ps.executeUpdate();
            ResultSet result = ps.getGeneratedKeys();

            if (n == 1 && result.next()) {
                result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes a customer based of customerID
     * @param id Customer_ID
     * @return
     */
    public static boolean deleteCustomer(int id) {
        try {
            String sql = "DELETE FROM customers WHERE Customer_ID = " + id;
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

            int n = ps.executeUpdate();
            if (n == 1) return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}



