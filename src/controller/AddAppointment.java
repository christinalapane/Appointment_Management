package controller;


import DAO.*;
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
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddAppointment implements Initializable {
    //TextFields//
    @FXML private TextField typeText;
    @FXML private TextField appointmentID;
    @FXML private TextField titleText;
    @FXML private TextField descriptionText;
    @FXML private Label localZoneLabel;
    //ComboBoxes//
    @FXML private ComboBox<Users> userCombo;
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Appointment> locationCombo;
    @FXML private ComboBox<LocalTime> startCombo;
    @FXML private ComboBox<LocalTime> endCombo;
    @FXML private ComboBox<Contact> contactCombo;
    @FXML private DatePicker pickDate;
    //Buttons//
    @FXML private Button cancelButton;
    @FXML private Button saveButton;


    /*** time formatters*/
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    /**Observable lists to pull start times*/
    private final ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    /**Observable lists to pull end times*/
    private final ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();



    /**
     * Initializes datePicker with valid dates based on business hours.
     *   Initializes all comboboxes
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDates();
        try {
            loadComboBoxes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * retyrns to main screen
     * @param actionEvent returns to main screen
     * @throws IOException if an I/O error occurs.
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Scene scene = new Scene (root);
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
        String type = typeText.getText();
        Appointment location = locationCombo.getValue();
        LocalTime start = startCombo.getValue();
        LocalTime end = endCombo.getValue();
        Users userID = userCombo.getValue();
        Contact contact = contactCombo.getValue();
        String date = String.valueOf(pickDate.getValue());
        LocalDateTime startTotal = LocalDateTime.of(LocalDate.parse(date), start);
        LocalDateTime endTotal = LocalDateTime.of(LocalDate.parse(date),end);


        if (AppointmentDAO.itExists(startTotal, endTotal)) {
            MainScreen.informationAlert("Information", "Unable to schedule appointment \n" +
                    "Scheduling conflict \n Check appointments and try again");
        }
         else if(location == null || customer == null || userID == null || type.isEmpty() || description.isEmpty() ||
                title.isEmpty()) {
            emptyField();
        } else if (start.isAfter(end) || end.isBefore(start)) {
            timeAlert();
        } else if (!title.isEmpty() || !description.isEmpty() || !type.isEmpty()) {

            AppointmentDAO.addAppointment(title, description, location.getLocation(), type,
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
    private LocalDate checkDate(){
        if(LocalTime.now().isAfter(LocalTime.of(22,0)))
            switch (LocalDate.now().plusDays(1).getDayOfWeek()){
                case SATURDAY : return LocalDate.now().plusDays(3);
                case SUNDAY : return LocalDate.now().plusDays(2);
                default: return LocalDate.now().plusDays(1);
            }
        return LocalDate.now();
    }

    /**
     * lambda points to update pickDate to disable Saturday and Sundays
     * loads dates. Does not allow user to select Saturday or Sunday or current day, if after 10:00PM
     */
    private void loadDates(){
        pickDate.setValue(checkDate());
        pickDate.setDayCellFactory(datePicker -> new DateCell(){

                   @Override
                    public void updateItem(LocalDate date, boolean empty){
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
     * Loads all comboboxes with arrays.
     * @throws Exception if a SQL error exists
     */
    private void loadComboBoxes() throws Exception {
        customerCombo.setItems(CustomerDAO.getAllCustomers());
        contactCombo.setItems(ContactsDAO.allContacts());
        userCombo.setItems(UserDAO.getAllUsers());
        locationCombo.setItems(AppointmentDAO.getAllAppointments());

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
    private void setNewAppointmentTime(){
        LocalTime current = LocalTime.now();
        startCombo.getSelectionModel().select(0);
        endCombo.getSelectionModel().select(1);
        for(LocalTime time : startTimes){
            if(current.isBefore(time)){
                startCombo.getSelectionModel().select(time);
                endCombo.getSelectionModel().select(endTimes.indexOf(time.plusMinutes(15)));
                break;
            }
        }
    }

    /**
     * Error alert if any fields are empty
     */
    private void emptyField (){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("No fields can be empty. Add valid information for all fields");
        alert.showAndWait();
    }
    private void timeAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("Time error. Check if start is before end. \n Hours are between business hours 8:00 AM - 10:00 PM");
        alert.showAndWait();
    }

    /**
     * confirm alert that confirms appointment has been scheduled
     */
    private void confirmAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm adding appointment");
        alert.setContentText("Appointment has been added");
        alert.showAndWait();
    }

    /**
     * error alert if there is any issue scheduling appointment
     */
    private void errorAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding appointment");
        alert.setContentText("Appointment was not added to system. Try again");
        alert.showAndWait();

    }


    /*** unused*/
    public void onContact() {}
    /*** unused*/
    public void onLocation() {}
    /*** unused*/
    public void onStart() {}
    /*** unused*/
    public void onEnd() {}
    /*** unused*/
    public void onCustomer() {}
    /*** unused*/
    public void onUser() {}
}









