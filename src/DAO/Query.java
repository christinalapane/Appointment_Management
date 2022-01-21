package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import static Database.DBConnection.connection;

public class Query {
    private static String query;
    private static Statement statement;
    private static ResultSet result;

    /**
     * Used to do any SQL query in DAO
     * @param q sqlStatement
     */
    public static void makeQuery(String q){
        query = q;
        try{
            statement = connection.createStatement();
            if(query.toLowerCase().startsWith("select"))
                result = statement.executeQuery(q);
            if(query.toLowerCase().startsWith("delete") || query.toLowerCase().startsWith("insert")
            || query.toLowerCase().startsWith("update"))
                statement.executeQuery(q);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used for any SQL return
     * @return SQL result
     */
    public static ResultSet getResult(){
        return result;
    }
}
