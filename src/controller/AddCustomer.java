package controller;

import DAO.CountriesDAO;
import DAO.CustomerDAO;
import DAO.FirstLevelDAO;
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
import model.Countries;
import model.Customer;
import model.FirstLevel;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Consumer;


public class AddCustomer implements Initializable {
    //TextFields//
    @FXML private TextField phoneText;
    @FXML private TextField customerID;
    @FXML private TextField nameText;
    @FXML private TextField addressText;
    @FXML private TextField zipcodeText;
    @FXML private Label stateLabel;
    //Comboboxes//
    @FXML private ComboBox<Countries> countryCombo;
    @FXML private ComboBox<FirstLevel> stateCombo;
    //Buttons//
    @FXML private Button cancelButton;
    @FXML private Button saveButton;


    /*** countries list*/
    private final ObservableList<Countries> countries = FXCollections.observableArrayList();
    /*** divisions list*/
    private final ObservableList<FirstLevel> divisions = FXCollections.observableArrayList();
    /*** to allow a new customer*/
    private Customer customer = null;
    private Consumer<Customer> onComplete;


    /**
     * Initializes countryCombo and Divisions
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.customer = customer;
        this.onComplete = onComplete;
        try {
            loadDivision();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loadCountries();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stateCombo.setItems(divisions);
        countryCombo.setItems(countries);
    }

    /**
     * Returns to main screen
     * @param actionEvent pressing cancel button
     * @throws IOException in an I/O error
     *
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {

        Stage primaryStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Object scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        primaryStage.setScene(new Scene((Parent) scene));
        primaryStage.show();
    }

    /**
     * Checks for valid information, empty fields. Adds new customer to database
     * @param actionEvent pressing save button
     * @throws SQLException in case of a SQl error
     * @throws IOException in case of an I/O error
     */
    public void onSave(ActionEvent actionEvent) throws SQLException, IOException {
        String name = nameText.getText();
        String address = addressText.getText();
        String phone = phoneText.getText();
        String zip = zipcodeText.getText();
        FirstLevel division = stateCombo.getValue();
        Countries countries = countryCombo.getValue();

        if (division == null)
            return;

        if (!name.isEmpty() || !address.isEmpty() || !address.isEmpty() || !zip.isEmpty()) {
            CustomerDAO.addCustomer(name, address, zip, phone, division.getDivisionID());
            confirmAlert();
            Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setTitle("Main Screen");
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            errorAlert();
        }
    }

    /**
     * Loads all countries into list
     * @throws Exception in case of SQL error
     */
    public void loadCountries() throws Exception {
        CountriesDAO countriesDAO = new CountriesDAO();
        ObservableList<Countries> countryList = CountriesDAO.getAllCountries();
        countries.addAll(countryList);
    }

    /**
     * Loads all divisions into list
     * @throws Exception in case of SQL error
     */
    public void loadDivision() throws Exception{
        FirstLevelDAO firstLevelDAO = new FirstLevelDAO();
        ObservableList<FirstLevel> divisionList = FirstLevelDAO.getAllDivision();
        divisions.addAll(divisionList);
    }

    /**
     * compares country ID in country table and division table.
     *  filters divisions based off that
     */
    public void  updateDivision (){
        int countryID = countryCombo.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<FirstLevel> filteredDivisions = FXCollections.observableArrayList();
        for(FirstLevel division : divisions){
            if(division.getCountryID() == countryID){
                filteredDivisions.add(division);
                if(countryID == 1){
                    stateLabel.setText("State");
                }else{
                    stateLabel.setText("Province");
                }
            }
        }
        stateCombo.setItems(filteredDivisions);


    }
    /*** When country is chosen, will update stateCombo to filtered divisions*/
    @FXML void onCountry()  {updateDivision();}

    /*** confirm alert that customer has been added*/
    private void confirmAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm adding customer");
        alert.setContentText("Customer has been added");
        alert.showAndWait();
    }

    /*** error alert if customer was not added to database*/
    private void errorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error adding customer");
        alert.setContentText("Customer was not added to system. Try again");
        alert.showAndWait();

    }

    /*** unused*/
    @FXML void onStateCombo(){}

}



