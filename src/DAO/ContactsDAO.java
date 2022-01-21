package DAO;

import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;
import model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactsDAO {

    /**
     * @return all information from Contact table
     * @throws SQLException in case of SQL error
     * @throws Exception in case of SQL error
     */
    public static ObservableList<Contact> allContacts() throws SQLException, Exception{
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "SELECT * FROM contacts";
        Query.makeQuery(sqlStatement);
        Contact contactResult;
        ResultSet result = Query.getResult();

        while(result.next()){
        int contactID = result.getInt("Contact_ID");
        String name = result.getString("Contact_Name");
        String email = result.getString("Email");
        contactResult = new Contact(contactID, name, email);
        allContacts.add(contactResult);
    }
             return allContacts;
    }
}
