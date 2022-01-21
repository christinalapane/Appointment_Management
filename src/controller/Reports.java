package controller;

import DAO.AppointmentDAO;
import DAO.ContactsDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import model.Contact;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.Optional;
import java.util.ResourceBundle;

public class Reports implements Initializable {
    //appointment table
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIDCol;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, LocalDateTime> startCol;
    @FXML private TableColumn<Appointment, LocalDateTime> endCol;
    @FXML private TableColumn<Appointment, Integer> customerIDCol;
    //month table //
    @FXML private TableView<Object> monthTable;
    @FXML private TableColumn<Reports, String> monthTypeCol;
    @FXML private TableColumn<Reports, Month> monthCol;
    @FXML private TableColumn<Reports, Integer> totalMonthCol;

    //year table //
    @FXML private TableView<Object> yearTable;
    @FXML private TableColumn<Reports, String> contactCol;
    @FXML private TableColumn<Reports, Year> yearCol;
    @FXML private TableColumn<Reports, Integer> totalYearCol;


    @FXML private Label contactLabel;
    @FXML private ComboBox<Contact> selectContact;
    @FXML private Button mainScreenButton;
    @FXML private Button exitButton;

   private final ObservableList<Object> typeMonth = FXCollections.observableArrayList();
   private final ObservableList<Contact> contacts = FXCollections.observableArrayList();
   private final ObservableList<Object> typeYear = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final FilteredList<Appointment> filteredAppointments = new FilteredList<>(appointments);



    /**
     * initializes comboboxes and all tables
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            loadAppointmentTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loadMonthTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loadYearTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * lambda filters through appointments based off selected contact
     * @param actionEvent selected contact
     */
    @FXML  void onSelectContact(ActionEvent actionEvent) {
        Contact selected = selectContact.getSelectionModel().getSelectedItem();
        contactLabel.setText(selected.getName());
        if(selected != null ){
            contactLabel.setText(selected.getName());
            filteredAppointments.setPredicate(appointment -> appointment.getContactID() == selected.getContactID());
        }
    }

    /**
     * returns to main screen
     * @param actionEvent main screen button
     * @throws IOException in case of I/O error
     */
    @FXML void onMainScreen(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Scene scene = new Scene (root);
        stage.setScene(scene);
        Stage currentStage = (Stage) mainScreenButton.getScene().getWindow();
        currentStage.close();
        stage.show();
    }

    /**
     * exits system
     * @param actionEvent exit button
     */
    @FXML void onExitButton(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            System.exit(0);
        }
    }

    /**
     * loads all information to each list
     * @throws Exception in case of SQL error
     */
    public void loadInformation() throws  Exception{

        typeMonth.addAll(AppointmentDAO.monthlyReports());
        typeYear.addAll(AppointmentDAO.yearlyReports());
        appointments.addAll(AppointmentDAO.getAllAppointments());
        contacts.addAll(ContactsDAO.allContacts());
        selectContact.setItems(contacts);
    }

    /**
     * loads appointment table
     * @throws Exception in case of SQL error
     */
    public void loadAppointmentTable () throws Exception {
        loadInformation();
        appointmentTable.setItems(filteredAppointments);
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * loads monthly report table
     */
    public void loadMonthTable (){
        monthTable.setItems(typeMonth);
        monthTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalMonthCol.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    /**
     * loads year report table
     */
    public void loadYearTable() {
        yearTable.setItems(typeYear);
        contactCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalYearCol.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

}



