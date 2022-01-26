package model;

/**
 * All variables for users table
 */
public class Users {
    public  int userID;
    public String username;
    public String password;

    /**
     * @param userID User_ID
     * @param username User_Name
     * @param password Password
     */
    public Users(int userID, String username, String password) {
       this.userID = userID;
       this.username = username;
       this.password = password;
    }

    /**
     * @param user_name User_Name
     * @param user_id User_ID
     */
    public Users(String user_name, int user_id) {
    }


    /**
     * Set and get all variables for user table
     */
    public  int getUserID(){return userID;}
    public void setUserID(int userID){this.userID = userID;}

    public  String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

    /**
     * @return username string
     */
    @Override
    public String toString() {
        return username;
    }
}
;