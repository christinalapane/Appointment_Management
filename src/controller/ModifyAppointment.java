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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;


public class ModifyAppointment implements Initializable {
    //textfields//
    @FXML TextField typeText;
    @FXML private TextField appointmentID;
    @FXML private TextField titleText;
    @FXML private TextField descriptionText;
    //combobox//
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Contact> contactCombo;
    @FXML private ComboBox<Users> userCombo;
    @FXML private DatePicker pickDate;
    @FXML private ComboBox<Appointment> locationCombo;
    @FXML private ComboBox<LocalTime> startCombo;
    @FXML private ComboBox<LocalTime> endCombo;
    //labels//
    @FXML private Label localZoneLabel;
    //buttons//
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Appointment selectedAppointment;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM:dd:yyyy hh:mm a");
    private final ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    private final ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = AppointmentDAO.getAllAppointments();
    private final ObservableList<Contact> contacts = ContactsDAO.allContacts();
    private final ObservableList<Users> users = UserDAO.getAllUsers();
    private final ObservableList<Customer> customers = CustomerDAO.getAllCustomers();

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
        pickDate.setValue(selectedAppointment.getStartTime().toLocalDate());
        startCombo.setValue(selectedAppointment.getStartTime().toLocalTime());
        endCombo.setValue(selectedAppointment.getEndTime().toLocalTime());
        typeText.setText(selectedAppointment.getType());

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
        Appointment location = locationCombo.getValue();
        String type = typeText.getText();
        LocalTime start = startCombo.getValue();
        LocalTime end = endCombo.getValue();
        Users userID = userCombo.getValue();
        Contact contact = contactCombo.getValue();
        String date = String.valueOf(pickDate.getValue());


        Consumer<Appointment> onComplete = result -> {
            if (isConflict(result)) {
                MainScreen.informationAlert("Information", "Unable to schedule appointment \n" +
                        "Scheduling conflict \n Check appointments and try again");
            }
        };
        if (location == null || customer == null || userID == null || type.isEmpty() || description.isEmpty() ||
                title.isEmpty()) {
            emptyField();
        } else if (start.isAfter(end) || end.isBefore(start)) {
            timeAlert();
        }else if(!title.isEmpty() || !description.isEmpty() || !type.isEmpty()) {
            modifyAppointment(title, description, location.getLocation(), type,
                    LocalDateTime.of(LocalDate.parse(date), start), LocalDateTime.of(LocalDate.parse(date), end), customer.getCustomerID(), userID.getUserID(), contact.getContactID());
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
     * Adds updated information into database
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
    void modifyAppointment(String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {
        String sql = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Create_Date=?, Created_By=?, Last_Update=?, Last_Updated_By=?, Customer_ID=?, Contact_ID=?, User_ID=? WHERE Appointment_ID=?";
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
            ps.setString(8, LoginPage.getLoggedOnUser().getUsername());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(10, LoginPage.getLoggedOnUser().getUsername());
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

                if (item == null || empty) {
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
    private void endComboInitialize() {
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

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(timeFormatter));
                }
            }
        });
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

        for(Appointment appointment : appointments){
            if(appointment.getLocation() != selectedAppointment.getLocation()) continue;
            locationCombo.setValue(appointment);
        } locationCombo.setItems(appointments);


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
        setNewAppointmentTime();
        endComboInitialize();
        startComboInitialize();
        startCombo.setItems(startTimes);
        endCombo.setItems(endTimes);
    }

    /**
     * Gets current time and sets lists to next available times at 15 min increments.
     */
    private void setNewAppointmentTime() {
        LocalTime current = LocalTime.now();
        startCombo.getSelectionModel().select(0);
        endCombo.getSelectionModel().select(1);
        for (LocalTime time : startTimes) {
            if (current.isBefore(time)) {
                startCombo.getSelectionModel().select(time);
                endCombo.getSelectionModel().select(endTimes.indexOf(time.plusMinutes(15)));
                break;}
        }
    }

    /**
     * lambda to filter through appointments with customerID to check for start and end times.
     *  lambda finds any match between appointment being scheduled and customers with already scheduled appointments
     * @param result the customerID that has an appointment
     * @return if there are any appointments already scheduled at this time
     */
    private boolean isConflict(Appointment result) {
        return appointments.stream()
                .filter(appointment -> appointment.getCustomerID() == result.getCustomerID() && (
                                appointment.getStartTime().toLocalDate().equals(result.getStartTime().toLocalDate()) ||
                                        appointment.getEndTime().toLocalDate().equals(result.getEndTime().toLocalDate()))).anyMatch(
                        appointment -> appointment.getStartTime().isEqual(result.getStartTime())
                                || appointment.getEndTime().isEqual(result.getEndTime())
                                || appointment.getStartTime().isBefore(result.getStartTime())
                                || appointment.getEndTime().isBefore(result.getEndTime()));
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
        alert.setHeaderText("Confirm adding appointment");
        alert.setContentText("Appointment has been added");
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

    /**
     * unused
     */
    public void onLocation() {}
    public void onStart() {}
    public void onEnd() {}
    public void onCustomer() {}
    public void onUser() {}
    public void onContact() {}
}
