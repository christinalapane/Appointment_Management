package DAO;

import Database.DBConnection;
import controller.MainScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Reports;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static DAO.UserDAO.*;


public class AppointmentDAO {

    /**
     * gets all information from appointment table
     *
     * @return all infomration from appointment table
     * @throws SQLException in case of SQL error
     * @throws Exception    in case of SQl error
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException, Exception {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

        DBConnection.getConnection();
        String sqlStatement = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Type,\n" +
                "appointments.Start, appointments.End, appointments.Create_Date, appointments.Created_By, \n" +
                "appointments.Last_Update, appointments.Last_Updated_By, appointments.Contact_ID, appointments.Customer_ID, appointments.User_ID, contacts.Contact_Name, customers.Customer_Name, users.User_Name\n" +
                "FROM appointments, contacts, customers, users \n" +
                "WHERE appointments.Contact_ID = contacts.Contact_ID \n" +
                "and appointments.Customer_ID = customers.Customer_ID\n" +
                "and appointments.User_ID = users.User_ID;";
        Query.makeQuery(sqlStatement);
        Appointment appointmentResult;
        ResultSet result = Query.getResult();

        while (result.next()) {
            int appointmentID = result.getInt("Appointment_ID");
            String title = result.getString("Title");
            String description = result.getString("Description");
            String location = result.getString("Location");
            String type = result.getString("Type");
            LocalDateTime start = result.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = result.getTimestamp("End").toLocalDateTime();
            LocalDateTime created = result.getTimestamp("Create_Date").toLocalDateTime();
            String createdBy = result.getString("Created_By");
            LocalDateTime updated = result.getTimestamp("Last_Update").toLocalDateTime();
            String updatedBy = result.getString("Last_Updated_By");
            int customerID = result.getInt("Customer_ID");
            String customerName = result.getString("Customer_Name");
            int userID = result.getInt("User_ID");
            String username = result.getString("User_Name");
            int contactID = result.getInt("Contact_ID");
            String contactName = result.getString("Contact_Name");

            appointmentResult = new Appointment(appointmentID, title, description, location, type, start, end, created, createdBy, updated, updatedBy, customerID, customerName, userID, username, contactID, contactName);
            allAppointments.add(appointmentResult);
        }
        return allAppointments;
    }

    /**
     * @return all monthly total reports
     * @throws SQLException in case of SQL error
     * @throws Exception    in case of SQL error
     */
    public static ObservableList<Reports> monthlyReports() throws SQLException, Exception {
        ObservableList<Reports> monthlyList = FXCollections.observableArrayList();
        DBConnection.getConnection();
        String sqlStatement = "Select month(start) as month, type, count(*) as total from appointments GROUP BY month, type;";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while (result.next()) {
            Reports row = new Reports(result.getLong("month"), result.getString("type"), result.getLong("total"));
            monthlyList.add(row);
        }
        return monthlyList;
    }

    /**
     * @return yearly total reports
     * @throws SQLException in case of SQl error
     * @throws Exception    in case of SQL error
     */
    public static ObservableList<Reports> yearlyReports() throws SQLException, Exception {
        DBConnection.getConnection();
        String sqlStatement = "Select year(start) as year, contacts.Contact_Name as Contact, count(*) as total from appointments JOIN contacts on contacts.Contact_ID=appointments.Contact_ID GROUP BY year, contact; ";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        ObservableList<Reports> yearlyReport = FXCollections.observableArrayList();

        while (result.next()) {
            Reports row = new Reports(result.getLong("year"), result.getString("Contact"), result.getLong("total"));

            yearlyReport.add(row);
        }
        return yearlyReport;
    }

    /**
     * adds appointment into the database
     *
     * @param title       Title
     * @param description Description
     * @param location    Location
     * @param type        Type
     * @param start       Start
     * @param end         End
     * @param customerID  Customer_ID
     * @param userID      User_ID
     * @param contactID   Contact_ID
     * @throws SQLException in case of SQL error
     */
    public static void addAppointment(String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) throws SQLException {

        try {
            String sqlStatement = "INSERT INTO appointments VALUE(NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(start));
            ps.setTimestamp(6, Timestamp.valueOf(end));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, getLoggedName());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(10, getLoggedName());
            ps.setInt(11, customerID);
            ps.setInt(12, userID);
            ps.setInt(13, contactID);


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
     * deletes an appointment based off appointment ID
     *
     * @param id Appointment_ID
     * @return deletes a single appointment
     */
    public static boolean deleteSingleAppointment(int id) {
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID = " + id;
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            int n = ps.executeUpdate();

            if (n == 1) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param now   start date
     * @param later end date
     * @return a filtered appointment list based off entered values
     * @throws SQLException in case of SQL error
     */
    public static ObservableList<Appointment> getDateFiltered(LocalDateTime now, LocalDateTime later) throws SQLException {
        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        String sql = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Type,\n" +
                "appointments.Start, appointments.End, appointments.Create_Date, appointments.Created_By, \n" +
                "appointments.Last_Update, appointments.Last_Updated_By, appointments.Contact_ID, appointments.Customer_ID, appointments.User_ID, contacts.Contact_Name, customers.Customer_Name, users.User_Name\n" +
                "FROM appointments, contacts, customers, users \n" +
                "WHERE appointments.Contact_ID = contacts.Contact_ID \n" +
                "and appointments.Customer_ID = customers.Customer_ID\n" +
                "and appointments.User_ID = users.User_ID  and Start between ? AND ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(now));
        ps.setTimestamp(2, Timestamp.valueOf(later));
        ResultSet result = ps.executeQuery();
        Appointment appointmentResult;

        while (result.next()) {
            int appointmentID = result.getInt("Appointment_ID");
            String title = result.getString("Title");
            String description = result.getString("Description");
            String location = result.getString("Location");
            String type = result.getString("Type");
            LocalDateTime start = result.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = result.getTimestamp("End").toLocalDateTime();
            LocalDateTime created = result.getTimestamp("Create_Date").toLocalDateTime();
            String createdBy = result.getString("Created_By");
            LocalDateTime updated = result.getTimestamp("Last_Update").toLocalDateTime();
            String updatedBy = result.getString("Last_Updated_By");
            int customerID = result.getInt("Customer_ID");
            String customerName = result.getString("Customer_Name");
            int userID = result.getInt("User_ID");
            String username = result.getString("User_Name");
            int contactID = result.getInt("Contact_ID");
            String contactName = result.getString("Contact_Name");

            appointmentResult = new Appointment(appointmentID, title, description, location, type, start, end, created, createdBy, updated, updatedBy, customerID, customerName, userID, username, contactID, contactName);
            filtered.add(appointmentResult);
        }
        return filtered;
    }

    /**
     * * Adds updated information into database
     * @param title Title
     * @param description Description
     * @param location Location
     * @param type Type
     * @param start Start
     * @param end End
     * @param customerID Customer_ID
     * @param userID User_ID
     * @param contactID Contact_ID
     */
    public static void modifyAppointment(String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        String sql = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Create_Date=?, Created_By=?, Last_Update=?, Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID=?";
        int appointmentID = MainScreen.appointmentToModify().getAppointmentID();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setTimestamp(5, Timestamp.valueOf(start));
            ps.setTimestamp(6, Timestamp.valueOf(end));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, UserDAO.getLoggedName());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(10, UserDAO.getLoggedName());
            ps.setInt(11, customerID);
            ps.setInt(12, userID);
            ps.setInt(13, contactID);
            ps.setInt(14, appointmentID);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * changes dates to represent local time
     * @param dateTime input of dateTime string
     * @return string of local time zone dateTime
     */
    public static String toLocal(String dateTime){
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        LocalDateTime local = timestamp.toLocalDateTime();
        ZonedDateTime zoned = local.atZone(ZoneId.of("UTC"));
        ZonedDateTime zonedLocal = zoned.withZoneSameLocal(ZoneId.of(ZoneId.systemDefault().toString()));
        LocalDateTime localDate = zonedLocal.toLocalDateTime();
        String finish = localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
        return  finish;
    }



}



