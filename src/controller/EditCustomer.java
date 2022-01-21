package controller;

import DAO.CountriesDAO;
import DAO.FirstLevelDAO;
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
import model.Countries;
import model.Customer;
import model.FirstLevel;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class EditCustomer implements Initializable {
    //comboboxes//
    @FXML private ComboBox<FirstLevel> stateCombo;
    @FXML private ComboBox<Countries> countryCombo;
    //textfields//
    @FXML private TextField zipcodeText;
    @FXML private Label stateLabel;
    @FXML private TextField phoneText;
    @FXML private TextField customerID;
    @FXML private TextField nameText;
    @FXML private TextField addressText;
    //buttons//
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    /**
     * adds countries and divisions to lists
     */
    private final ObservableList<Countries> countries = CountriesDAO.getAllCountries();
    private final ObservableList<FirstLevel> divisions = FirstLevelDAO.getAllDivision();
    /**
     * to add customer
     */
    private Customer customer = null;
    private Consumer<Customer> onComplete;
    /**
     * selected customer to compare information
     */
    private Customer selectedCustomer;

    /**
     * Not sure why this was added, but it had to do with declaring the ObservableList
     * @throws Exception in case of SQl error
     */
    public EditCustomer() throws Exception {
    }


    /**
     *Initializes the comboboxes and text fields with customer information based off the customer selected on MainScreen
     */
    @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            selectedCustomer = MainScreen.customerToModify();
            customerID.setText(String.valueOf(selectedCustomer.getCustomerID()));
            nameText.setText(selectedCustomer.getCustomerName());
            addressText.setText(selectedCustomer.getCustomerAddress());
            zipcodeText.setText(selectedCustomer.getCustomerZipCode());
            phoneText.setText(selectedCustomer.getCustomerPhone());
            for (FirstLevel division : divisions) {
                if (division.getDivisionID() != selectedCustomer.getDivisionID()) continue;
                stateCombo.setValue(division);
                for (Countries countries : countries) {
                    if (countries.getCountryID() != division.getCountryID()) continue;
                    countryCombo.setValue(countries);
                }
            }
            stateCombo.setItems(divisions);
            countryCombo.setItems(countries);
        }

    /**
     *
     * @param actionEvent returns to main screen
     * @throws IOException in case of I/O error
     */
    public void onCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks for valid information and empty fields. If valid adds edited customer into database.
     * @param actionEvent returns to main screen after pressing save
     * @throws IOException in case of I/O error
     * @throws SQLException in case of SQL error
     */
    public void onSave(ActionEvent actionEvent) throws IOException, SQLException {
        String name = nameText.getText();
        String address = addressText.getText();
        String phone = phoneText.getText();
        String zip = zipcodeText.getText();
        FirstLevel division = stateCombo.getValue();
        Countries countries = countryCombo.getValue();

        if (division == null)
            return;

        if (!name.isEmpty() || !address.isEmpty() || !address.isEmpty() || !zip.isEmpty()) {
            modifyCustomer(name, address,zip, phone, division.getDivisionID());
            confirmAlert("Confirm", selectedCustomer.getCustomerName() + " has been edited");
            Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            stage.setTitle("Main Screen");
            stage.setScene(new Scene(scene));
            stage.show();
        } else {
            errorAlert("Error", selectedCustomer.getCustomerName()+" has not been edited.");
        }
    }

    /**
     * compares countryID in divisions vs in country. Filters list based on that.
     */
    public void updateDivision() {
        int countryID = countryCombo.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<FirstLevel> filteredDivisions = FXCollections.observableArrayList();
        for (FirstLevel division : divisions) {
            if (division.getCountryID() == countryID) {
                filteredDivisions.add(division);
                if (countryID == 1) {
                    stateLabel.setText("State");
                } else {
                    stateLabel.setText("Province");
                }
            }
        }
        stateCombo.setItems(filteredDivisions);
    }

    /**
     * gets countryCode and then uploads filtered list into stateCombo
     * @param actionEvent choosing a country
     */
    public void onCountry(ActionEvent actionEvent) {updateDivision();}


    /**
     * adds new information in database where customerID = selectedCustomer
     * @param name Customer_Name
     * @param address Address
     * @param zip Postal_Code
     * @param phone Phone
     * @param divisionID Division_ID
     * @throws SQLException in case of SQL error
     */
     void modifyCustomer(String name, String address, String zip, String phone, int divisionID) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name=?,  Address=?, Postal_Code=?, Phone=?, Create_Date=?, Created_By=?, Last_Update=?, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?";
        int id = MainScreen.customerToModify().getCustomerID();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, zip);
            ps.setString(4, phone);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, LoginPage.getLoggedOnUser().getUsername());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, LoginPage.getLoggedOnUser().getUsername());
            ps.setInt(9, divisionID);
            ps.setInt(10,id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    /**
     *
     * @param title Title of alert
     * @param context context of alert
     * @return confirmation alert
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
     *
     * @param title title of alert
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

    /**
     * unused
     */
    public void onStateCombo() {}

}
