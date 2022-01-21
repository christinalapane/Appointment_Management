package DAO;

import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * @return all information from Users table
     * @throws SQLException in case of SQL error
     * @throws Exception in case of SQL error
     */
    public static ObservableList<Users> getAllUsers () throws SQLException, Exception{
        ObservableList<Users> allUsers = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "select * from users";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while(result.next()){
            int userID = result.getInt("User_ID");
            String user = result.getString("User_Name");
            String pass = result.getString("Password");
            Users userResult;
            userResult = new Users(userID, user, pass);
            allUsers.add(userResult);

        }
        return  allUsers;
    }


}
