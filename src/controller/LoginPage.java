package controller;

import DAO.UserDAO;
import Database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import resources.Logger;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginPage implements Initializable {
    //labels
    public Label loginError;
    @FXML private Label userLabel;
    @FXML private Label localArea;
    @FXML private Label passwordLabel;
    @FXML private Label loginLabel;

    //buttons
    @FXML private Button login;
    @FXML private Button exit;

    //textfields
    @FXML private PasswordField password;
    @FXML private TextField username;

    private final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    /**
     * declares for logged Users
     */
    static boolean yesApp = false;


    /**
     * initializes labels and buttons on login screen.
     *  references languages based on local location.
     */
    @Override
    public void initialize(URL location, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("resources/Languages");
         TimeZone zone = TimeZone.getDefault();
         String name = zone.getDisplayName();
         localArea.setText(Locale.getDefault().getDisplayCountry() + "\n" + name);

        loginError.setVisible(false);
                userLabel.setText(rb.getString("username"));
                passwordLabel.setText(rb.getString("password"));
                loginLabel.setText(rb.getString("login"));
                login.setText(rb.getString("loginButton"));
                exit.setText(rb.getString("exit"));
                loginError.setText(rb.getString("error"));
            }


    /**
     * If hitting ENTER -> login
     * @param keyEvent pressing ENTER
     * @throws IOException in case of I/O error
     * @throws SQLException in case of SQL error
     */
    public void onEnter(KeyEvent keyEvent) throws Exception {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            onLogin(new ActionEvent());
        }
    }

    /**
     * Checks valid username and password - logs if logged in
     * @param actionEvent pressing login button- goes to main screen if valid
     * @throws IOException in case of I/O error
     * @throws SQLException in case of SQL error
     */
    @FXML public void onLogin(ActionEvent actionEvent) throws SQLException, IOException {

        if (username.getText().isBlank() == false && password.getText().isBlank() == false) {
            boolean success = UserDAO.authenticateLogin(username.getText(), password.getText());
            Logger.auditLogin(username.getText(), success);
            if (success) {
                fifteenMinutes();
                if(!yesApp){
                    MainScreen.informationAlert("Appointments","No appointments in the next 15 minutes" );

                }
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                stage.setTitle("Main Screen");
                stage.setScene(new Scene(scene));
                stage.show();
            } else {
                loginError.setVisible(true);
                System.out.println(username + "Didn't validate");
            }
        }

    }
        /**
         * when exit is hit, checks to make sure that's what you want to do and then exits system
         * @param actionEvent pressing exit will exit out of system
         */
        @FXML void onExit (ActionEvent actionEvent){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.exit(0);
            }

        }


    private static boolean nearAppointment(LocalDateTime start){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after15 = LocalDateTime.now().plusMinutes(15);
        LocalDateTime before15 = LocalDateTime.now().plusMinutes(-15);



        if(now.isBefore(after15) && now.isAfter(before15)){
            return true;
        }else{return  false;}
    }

    static void fifteenMinutes() throws SQLException {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime fifteen = LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(15);
        int user = UserDAO.getLoggedOnUser();

        String sql = "SELECT Appointment_ID, Start, End FROM Appointments WHERE Start BETWEEN ? AND ? AND User_ID = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setTimestamp(1, Timestamp.valueOf(now));
        ps.setTimestamp(2, Timestamp.valueOf(fifteen));
        ps.setInt(3, user);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (nearAppointment(rs.getTimestamp("Start").toLocalDateTime())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment notice");
                alert.setHeaderText("Appointment coming up");
                alert.setHeaderText("You have an appointment in next 15 minutes \n" +
                        "Appointment ID: " + rs.getInt("Appointment_ID") + " \n" +
                        "Scheduled:  " + rs.getTimestamp("Start").toLocalDateTime().format(dateTimeFormat) + " - " + rs.getTimestamp("End").toLocalDateTime().format(dateTimeFormat));
                alert.showAndWait();
                yesApp = true;
                break;
            }
        }

    }






    /*** unused*/
    @FXML void onPassword() {}
    /*** unused*/
    @FXML void onUsername() {}
}
