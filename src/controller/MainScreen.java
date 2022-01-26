package controller;

import DAO.AppointmentDAO;
import DAO.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainScreen implements Initializable {
    //Customer table//
    @FXML private TableColumn<Customer, String> addressCol;
    @FXML private TableColumn<Customer, String> postalCol;
    @FXML private TableColumn<Customer, String> countryCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerIDCol;
    @FXML private TableColumn<Customer, String> customerNameCol;


    //buttons
    @FXML private Button reportsButton;
    @FXML private Button exit;
    @FXML private Button deleteCustomer;
    @FXML private Button updateCustomer;
    @FXML private Button addCustomer;
    @FXML private Button deleteAppointment;
    @FXML private Button updateAppointment;
    @FXML private Button addAppointment;
    @FXML private Button clearButton;
    @FXML private RadioButton weeklyButton;
    @FXML private RadioButton monthlyButton;

    //labels //
    @FXML private Label appointmentLabel;
    @FXML private Label dateLabel;
    @FXML private Label customerLabel;

    //Appointment table
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> contactCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, LocalDateTime> startCol;
    @FXML private TableColumn<Appointment, LocalDateTime> endCol;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIDCol;
    @FXML private TableColumn<Appointment, Integer> customerAppointmentCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> typeCol;


    private static Customer selectedCustomer;
    private static Appointment selectedAppointment;
    private final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();



    /**
     * initializes appointment table, customer table, and also alerts if there is an appointment in the next 15 minutes.
     * Also shows current date.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        dateLabel.setText(LocalDate.now().format(dateFormat));
        try {
            loadAppointmentTable();
            loadCustomerTable();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * loads customers into list and appointments into list
     * @throws Exception in case of SQL error
     */
    public void loadInformation() throws Exception {
        ObservableList<Customer> customerList = CustomerDAO.getAllCustomers();
        customers.addAll(customerList);
        ObservableList<Appointment> aList = AppointmentDAO.getAllAppointments();
        appointments.addAll(aList);

    }

    /**
     * sets variables to the columns
     * @throws Exception in case of SQL error
     */
    public void loadAppointmentTable() throws Exception {
        loadInformation();
        appointmentTable.setItems(appointments);
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        customerAppointmentCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    /**
     * loads variables into columns
     * @throws Exception in case of SQL error
     */
    public void loadCustomerTable() throws Exception {

        customerTable.setItems(customers);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("customerZipCode"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
    }

    /**
     * @return selected customer from table
     */
    public static Customer customerToModify() {
        return selectedCustomer;
    }

    /**
     * @return selected appointment from table
     */
    public static Appointment appointmentToModify() {
        return selectedAppointment;
    }


    /**
     * confirms you want to exit and then exits system
     * @param actionEvent pressing exit, exits the entire system
     * @throws IOException in case of I/O error
     */
    @FXML
    private void onExit(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);

        }
    }

    /**
     * pressing add appointment goes to AddAppointment screen
     *
     * @param actionEvent pressing add appointment
     * @throws IOException in case of I/O error
     */
    @FXML
    void onAddAppointment(ActionEvent actionEvent) throws IOException {
        Stage addApptStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        addApptStage.setScene(new Scene(scene));
        addApptStage.show();

    }

    /**
     * confirms user selects an appointment and if valid, goes to ModifyAppointment Screen.
     * if Not, error alert shows
     * @param actionEvent pressing updateAppointment button
     * @throws IOException in case of I/O error
     */
    @FXML
    void onUpdateAppointment(ActionEvent actionEvent) throws IOException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            informationAlert("Edit Appointment", "About to edit Appointment ID: " + selectedAppointment.getAppointmentID());
            Stage updateApptStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/ModifyAppointment.fxml"));
            updateApptStage.setScene(new Scene(scene));
            updateApptStage.show();
        } else {
            errorAlert("Error", "Please choose an appointment to edit");
        }
    }

    /**
     * Confirms appointment is selected. If not -> error alert.
     * Confirms to delete an appointment then refreshes the main screen
     *
     * @param actionEvent delete appointment button selected
     * @throws IOException in case of I/O error
     */
    public void onDeleteAppointment(ActionEvent actionEvent) throws IOException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            errorAlert("Error", "Please select an appointmnent to delete");
        } else {
            AppointmentDAO.deleteSingleAppointment(selectedAppointment.getAppointmentID());
            String id = String.valueOf(selectedAppointment.getAppointmentID());
            String type = selectedAppointment.getType();
            confirmAlert("Confirm", "Appointment ID: " + id + "\n"
            + "Type: " + type +"\n is being deleted");
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setTitle("Main Screen");
            stage.setScene(new Scene(scene));
            stage.show();
        }


    }

    /**
     * moves to AddCustomer screen
     * @param actionEvent addCustomer button
     * @throws IOException in case of I/O error
     */
    @FXML
    void onAddCustomer(ActionEvent actionEvent) throws IOException {
        Stage addCustomerStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        addCustomerStage.setScene(new Scene(scene));
        addCustomerStage.show();
    }

    /**
     * confirms customer is selected from table. If not -> error alert.
     * Goes to EditCustomer screen
     * @param actionEvent edit customer button
     * @throws IOException in case of I/O error
     */
    public void onUpdateCustomer(ActionEvent actionEvent) throws IOException {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            errorAlert("Error", "Please select Customer to edit");

        } else {
            confirmAlert("Confirm", "You are about to edit " + selectedCustomer.getCustomerName() + "\n press OK to continue");
            Stage updateCustomerStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/EditCustomer.fxml"));
            updateCustomerStage.setScene(new Scene(scene));
            updateCustomerStage.show();
        }
    }

    /**
     * confirms customer is selected from table. If not -> error alert.
     *lambda searches through and finds any match to customerID and selectedCustomer to delete a customer
     * @param actionEvent delete customer button
     * @throws IOException in case of I/O error.
     *                     lambda checks if customer has an appointment. alert if there are appointments.
     *                     deletes from database and table and refreshes main screen
     */
    public void onDeleteCustomer(ActionEvent actionEvent) throws IOException {

        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            errorAlert("Error", "Please select a customer to delete");
        }
        if (appointments.stream().anyMatch((appointment -> appointment.getCustomerID() == selectedCustomer.getCustomerID()))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Multiple appointments");
            alert.setContentText("Customer has multiple appointments, everything will be deleted \n Press X to cancel");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                appointments.removeIf(appointment -> appointment.getCustomerID() == selectedCustomer.getCustomerID());
            }
            CustomerDAO.deleteCustomer(selectedCustomer.getCustomerID());
            confirmAlert("Confirm", "Customer deleted. System is going to refresh now");
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setTitle("Main Screen");
            stage.setScene(new Scene(scene));
            stage.show();
        }
        if (!appointments.stream().anyMatch((appointment -> appointment.getCustomerID() == selectedCustomer.getCustomerID()))) {
            CustomerDAO.deleteCustomer(selectedCustomer.getCustomerID());
            confirmAlert("Confirm", "Customer deleted. System is going to refresh now");
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            errorAlert("Error", "Customer was not able to be deleted");
        }
    }

    /**
     * moves to Reports Screen
     * @param actionEvent reports button
     * @throws IOException in case of I/O Error
     */
    @FXML
    void onReports(ActionEvent actionEvent) throws IOException {
        Stage reportStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("/view/Reports.fxml"));
        reportStage.setScene(new Scene(scene));
        reportStage.show();
    }



    /**
     * if monthly pressed -> generate monthly filtered list
     */
    @FXML
    void onMonthly() throws Exception {
        String now = LocalDateTime.now().format(dateFormat);
        String month = LocalDateTime.now().plusMonths(1).format(dateFormat);
        appointmentLabel.setText(now + " to "+ month);

        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime monthDate = LocalDateTime.now().plusMonths(1);
        filtered = AppointmentDAO.getDateFiltered(nowDate, monthDate);
        appointmentTable.setItems(filtered);
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        customerAppointmentCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));


    }



    /**
     * if pressed -> generates weekly filtered list
     */
    @FXML
    void onWeekly() throws Exception{
        String now = LocalDateTime.now().format(dateFormat);
        String week = LocalDateTime.now().plusWeeks(1).format(dateFormat);
        appointmentLabel.setText(now + " to " + week);
        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime weekDate = LocalDateTime.now().plusWeeks(1);
        filtered = AppointmentDAO.getDateFiltered(nowDate, weekDate);
        appointmentTable.setItems(filtered);
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        customerAppointmentCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }



    /**
     * clears customer label, clear appointment label, and loads appointment table with all appointments
     * @param actionEvent pressing clear
     */
    @FXML void onClear(ActionEvent actionEvent)  {
        customerLabel.setText(" ");
        appointmentLabel.setText(" ");
        appointmentTable.setItems(appointments);
    }


    /**
     * @param title   title of alert
     * @param context context of alert
     * @return confirm alert
     */
    static boolean confirmAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(context);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;

        }
    }

    /**
     * @param title   title of alert
     * @param context context of alert
     * @return information alert
     */
    static boolean informationAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(context);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;

        }
    }

    /**
     * @param title   title of alert
     * @param context context of alert
     * @return error alert
     */
    static boolean errorAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(context);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}