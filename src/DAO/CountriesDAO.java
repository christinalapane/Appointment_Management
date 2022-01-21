package DAO;

import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Countries;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDAO {

    /**
     * @return all information from Countries table
     * @throws SQLException in case of SQL error
     * @throws Exception in case of SQL error
     */
    public static ObservableList<Countries> getAllCountries() throws SQLException, Exception {
        ObservableList<Countries> allCountries = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "SELECT Country_ID, Country FROM countries";
        Query.makeQuery(sqlStatement);
        Countries countriesResult;
        ResultSet result = Query.getResult();

        while (result.next()) {
            int countryID = result.getInt("Country_ID");
            String country = result.getString("Country");
            countriesResult = new Countries(countryID, country);
            allCountries.add(countriesResult);
        }

        return allCountries;
    }
}
