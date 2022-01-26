package DAO;

import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static Users loggedOnUser;

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

    /**
     * Checks against entered username and password versus database.
     * @param user User_Name
     * @param pass Password
     * @return username and password from database
     * @throws SQLException in case of SQL error
     */
    public static Boolean authenticateLogin(String user, String pass) throws SQLException{
        String sql = "SELECT * FROM users WHERE User_Name =? AND Password = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, user);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            loggedOnUser = new Users(rs.getString("User_Name"), rs.getInt("User_ID"));
            return true;
        }else{
            return false;
        }

    }

    /**
     *
     * @return name of loggedOnUser -> always test in this project
     */
    public static Integer getLoggedOnUser() throws SQLException {
        Integer user = null;
        String sql = "SELECT DISTINCT User_ID FROM users";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            user = rs.getInt("User_ID");
        }
        return user;
    }

    /**
     * gets currently logged in username
     * @return username
     * @throws SQLException in case of SQL error
     */
    public static String getLoggedName () throws SQLException{
        String user = " ";
        String sql = "SELECT DISTINCT User_Name FROM users";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            user = rs.getString("User_Name");
        }
        return user;
    }


}
