package DAO;

import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FirstLevel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstLevelDAO {

    /**
     * @return all information from First Level table
     * @throws SQLException in case of SQL error
     * @throws Exception in case of SQL error
     */
    public static ObservableList<FirstLevel> getAllDivision() throws SQLException, Exception {
        ObservableList<FirstLevel> allDivisions = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "SELECT first_level_divisions.*, countries.Country FROM first_level_divisions JOIN countries on first_level_divisions.Country_ID=countries.Country_ID";
        Query.makeQuery(sqlStatement);
        FirstLevel divisionResult;
        ResultSet result = Query.getResult();
        while (result.next()) {
            int divisionID = result.getInt("Division_ID");
            String division = result.getString("Division");
            int countryID = result.getInt("Country_ID");
            divisionResult = new FirstLevel(divisionID, division, countryID);
            allDivisions.add(divisionResult);
        }
        return allDivisions;
    }

}
