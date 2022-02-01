package controller;


import DAO.*;
import Database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import java.io.IOException;
import java.net.URL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;



public class ModifyAppointment implements Initializable {

    //textfields//
    @FXML TextField typeText;
    @FXML private TextField appointmentID;
    @FXML private TextField titleText;
    @FXML private TextField descriptionText;
    @FXML private TextField locationText;
    //combobox//
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Contact> contactCombo;
    @FXML private ComboBox<Users> userCombo;
    @FXML private DatePicker pickDate;
    @FXML private ComboBox<LocalTime> startCombo;
    @FXML private ComboBox<LocalTime> endCombo;
    //labels//
    @FXML private Label localZoneLabel;
    //buttons//
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Appointment selectedAppointment;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd:yyyy hh:mm a");
    private final ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private final ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = AppointmentDAO.getAllAppointments();
    private final ObservableList<Contact> contacts = ContactsDAO.allContacts();
    private final ObservableList<Users> users = UserDAO.getAllUsers();
    private final ObservableList<Customer> customers = CustomerDAO.getAllCustomers();
    private final ObservableList<FirstLevel> divisions = FirstLevelDAO.getAllDivision();

    public ModifyAppointment() throws Exception {}
    /**
     * Initializes all information with values from selected appointment.
     *  loads dates, times, and comboboxes with all lists
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        selectedAppointment = MainScreen.appointmentToModify();
        appointmentID.setText(String.valueOf(selectedAppointment.getAppointmentID()));
        titleText.setText(selectedAppointment.getTitle());
        descriptionText.setText(selectedAppointment.getDescription());
        locationText.setText(selectedAppointment.getLocation());
        pickDate.setValue(selectedAppointment.getStartTime().toLocalDate());
        startCombo.setValue(selectedAppointment.getStartTime().toLocalTime());
        endCombo.setValue(selectedAppointment.getEndTime().toLocalTime());
        typeText.setText(selectedAppointment.getType());

        String now = LocalDateTime.now().format(timeFormatter);
        localZoneLabel.setText("Local time: " + now );

        loadDates();
        loadComboBoxes();
    }


    /**
     * @param actionEvent returns to main screen
     * @throws IOException if an I/O error occurs.
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
        stage.show();
    }
    /**
     * Checks for valid information, valid times, empty spaces. Saves and then adds new Appointment to database.
     * @param actionEvent saves valid information and returns to mainScreen
     * @throws IOException if an I/O error occurs.
     * @throws SQLException if an SQL error occurs
     */
    public void onSave(ActionEvent actionEvent) throws Exception {

        Customer customer = customerCombo.getValue();
        String title = titleText.getText();
        String description = descriptionText.getText();
        String location = locationText.getText();
        String type = typeText.getText();
        LocalTime start = startCombo.getValue();
        LocalTime end = endCombo.getValue();
        Users userID = userCombo.getValue();
        Contact contact = contactCombo.getValue();
        String date = String.valueOf(pickDate.getValue());
        LocalDateTime startTotal = LocalDateTime.of(LocalDate.parse(date), start);
        LocalDateTime endTotal = LocalDateTime.of(LocalDate.parse(date),end);


        if(location == null || customer == null || userID == null || type.isEmpty() || description.isEmpty() ||
                title.isEmpty() || description.isEmpty() || type.isEmpty()) {
            emptyField();
        } else if (start.isAfter(end) || end.isBefore(start) || start.isBefore(LocalTime.now())) {
            timeAlert();
        } else if (ifContactExist() && ifCustomerExist()) {


            AppointmentDAO.modifyAppointment(title, description, location, type,
                    startTotal, endTotal, customer.getCustomerID(), userID.getUserID(), contact.getContactID());
            confirmAlert();
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setTitle("Main Screen");
            stage.setScene(new Scene(scene));
            stage.show();

        } else {
            errorAlert();
        }
    }


    /**
     * loads business hours into start and end array at increments of 15 minutes
     */
    private void loadTimes() {
        int[] quarterHours = {0, 15, 30, 45, 0};

        for (int i = 8; i < 22; i++) {
            for (int j = 0; j < 4; j++) {
                startTimes.add(LocalTime.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(i, quarterHours[j])).atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalTime()));
                endTimes.add(LocalTime.from(LocalDateTime.of(LocalDate.now(), LocalTime.of(i, quarterHours[j])).atZone(ZoneId.of("America/New_York")).withZoneSameInstant(ZoneId.systemDefault()).toLocalTime()));
            }
        }
    }

    /**
     * Checks the current time and date. If after 10Pm, goes to next day. Saturday or Sunday goes to Monday.
     * @return the current time and date.
     */
    private LocalDate checkDate() {
        if (LocalTime.now().isAfter(LocalTime.of(22, 0)))
            switch (LocalDate.now().plusDays(1).getDayOfWeek()) {
                case SATURDAY:
                    return LocalDate.now().plusDays(3);
                case SUNDAY:
                    return LocalDate.now().plusDays(2);
                default:
                    return LocalDate.now().plusDays(1);
            }
        return LocalDate.now();
    }
    /**
     * loads dates. Does not allow user to select Saturday or Sunday or current day, if after 10:00PM
     */
    private void loadDates() {
        pickDate.setValue(checkDate());
        pickDate.setDayCellFactory(datePicker -> new DateCell() {

            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }
        });
        pickDate.setEditable(false);
    }

    /**
     * loads all combo boxes with all lists, but sets current value to value of selected appointment
     */
    private void loadComboBoxes()  {
        selectedAppointment = MainScreen.appointmentToModify();
        for(Customer customers : customers){
            if (customers.getCustomerID() != selectedAppointment.getCustomerID()) continue;
            customerCombo.setValue(customers);
        }
            customerCombo.setItems(customers);



        for(Contact contacts : contacts){
            if(contacts.getContactID() != selectedAppointment.getContactID()) continue;
                contactCombo.setValue(contacts);}
                    contactCombo.setItems(contacts);

        for(Users users : users){
            if(users.getUserID() != selectedAppointment.getUserID()) continue;
                userCombo.setValue(users);
        }
                     userCombo.setItems(users);

        loadTimes();
        startComboInitialize();
        endComboInitialize();
        startCombo.setItems(startTimes);
        endCombo.setItems(endTimes);
    }


    /**
     * Sets startCombo at HH:mm format
     */
    private void startComboInitialize() {
        startCombo.setItems(startTimes);
        startCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LocalTime> call(ListView<LocalTime> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(LocalTime item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null) {
                            setText(null);
                        } else {
                            setText(item.format(timeFormatter));
                        }
                    }
                };
            }
        });
        startCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(timeFormatter));
                }
            }
        });
    }

    /**
     * sets endCombo times in HH:mm format
     */
    private void endComboInitialize () {
        endCombo.setItems(endTimes);
        endCombo.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LocalTime> call(ListView<LocalTime> stringListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(LocalTime item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null) {
                            setText(null);
                        } else {
                            setText(item.format(timeFormatter));
                        }
                    }
                };
            }
        });
        endCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(timeFormatter));
                }
            }
        });
    }
    /**
     * Goes through appointments and finds if selected contact has an appointment scheduled already
     * @return if contact has an appointment
     * @throws SQLException in case of SQL error
     */
    public boolean ifContactExist() throws SQLException {
        String contact = "";
        String scheduledIn = "no";
        String scheduledOut = "no";
        LocalTime start = startCombo.getValue();
        LocalTime end = endCombo.getValue();
        String date = String.valueOf(pickDate.getValue());
        LocalDateTime startTotal = LocalDateTime.of(LocalDate.parse(date), start);
        LocalDateTime endTotal = LocalDateTime.of(LocalDate.parse(date),end);

        // Getting Contact ID
        PreparedStatement ps2 = DBConnection.getConnection().prepareStatement("SELECT * "
                + "FROM contacts "
                + "WHERE Contact_Name = ?");
        ps2.setString(1, contactCombo.getValue().getName());
        ResultSet result1 = ps2.executeQuery();
        while (result1.next()) {
            contact = result1.getString("Contact_ID");
        }

        // all appointments for that one contact ID and compare it
        PreparedStatement ps3 = DBConnection.getConnection().prepareStatement("SELECT * "
                + "FROM appointments "
                + "WHERE (? BETWEEN Start AND End AND Contact_ID = ?) OR (Start BETWEEN ? AND ? AND Contact_ID = ?)");
        ps3.setTimestamp(1, Timestamp.valueOf(startTotal));
        ps3.setString(2, contact);
        ps3.setTimestamp(3, Timestamp.valueOf(startTotal));
        ps3.setTimestamp(4, Timestamp.valueOf(endTotal));
        ps3.setString(5, contact);
        ResultSet result = ps3.executeQuery();
        while (result.next()) {
            scheduledIn = result.getString("Start");
            scheduledOut = result.getString("End");

        }

        if (scheduledIn != "no") {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scheduling error");
            alert.setHeaderText("This appointment can not happen");
            alert.setContentText( contactCombo.getValue() + " already has an appointment  " +
                    "from " + AppointmentDAO.toLocal(scheduledIn) + " to " + AppointmentDAO.toLocal(scheduledOut)
                    + "\nPlease select another time or date.");
            Optional<ButtonType> result9 = alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Searches through appointments to find if customer has an appointment
     * @return if customer has an appointment scheduled
     * @throws SQLException in case of SQL error
     */
    public boolean ifCustomerExist() throws SQLException {
        String scheduledIn = "no";
        String scheduledOut = "no";
        LocalTime start = startCombo.getValue();
        LocalTime end = endCombo.getValue();
        String date = String.valueOf(pickDate.getValue());
        LocalDateTime startTotal = LocalDateTime.of(LocalDate.parse(date), start);
        LocalDateTime endTotal = LocalDateTime.of(LocalDate.parse(date),end);


        PreparedStatement ps4 = DBConnection.getConnection().prepareStatement("SELECT *" +
                "FROM customers WHERE Customer_Name = ?");
        ps4.setString(1, customerCombo.getValue().getCustomerName());
        String customer = " ";
        ResultSet result3 = ps4.executeQuery();
        while(result3.next()) {
            customer = (result3.getString("Customer_ID"));
        }
        // Now we grab all appointments for that one contact ID and compare it
        PreparedStatement ps3 = DBConnection.getConnection().prepareStatement("SELECT * "
                + "FROM appointments "
                + "WHERE (? BETWEEN Start AND End AND Customer_ID = ?) OR (Start BETWEEN ? AND ? AND Customer_ID = ?)");
        ps3.setTimestamp(1, Timestamp.valueOf(startTotal));
        ps3.setString(2, customer);
        ps3.setTimestamp(3, Timestamp.valueOf(startTotal));
        ps3.setTimestamp(4, Timestamp.valueOf(endTotal));
        ps3.setString(5, customer);
        ResultSet result = ps3.executeQuery();
        while (result.next()) {
            scheduledIn = result.getString("Start");
            scheduledOut = result.getString("End");
        }

        if (scheduledIn != "no") {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scheduling error");
            alert.setHeaderText("This appointment can not happen");
            alert.setContentText( customerCombo.getValue() + " already has an appointment  " +
                    "from " + AppointmentDAO.toLocal(scheduledIn) + " to " + AppointmentDAO.toLocal(scheduledOut)
                    + "\nPlease select another time or date.");
            Optional<ButtonType> result9 = alert.showAndWait();
            return false;
        }
        return true;
    }


    /**
     * time alert error
     */
    private void timeAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("Time error. Check if start is before end. \n Hours are between business hours 8:00 AM - 10:00 PM");
        alert.showAndWait();
    }

    /**
     * empty field error alert
     */
    private void emptyField (){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("No fields can be empty. Add valid information for all fields");
        alert.showAndWait();
    }

    /**
     * confirm  alert
     */
    private void confirmAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm editing appointment");
        alert.setContentText("Appointment has been edited");
        alert.showAndWait();
    }

    /**
     * error alert
     */
    private void errorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("Appointment was not added to system. Try again");
        alert.showAndWait();
    }

    /*** unused*/
    public void onStart() {}
    /*** unused*/
    public void onEnd() {}
    /*** unused*/
    public void onCustomer() {}
    /*** unused*/
    public void onUser() {}
    /*** unused*/
    public void onContact() {}
}
