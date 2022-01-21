package controller;

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
import model.Users;
import resources.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

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
    /**
     * declares for ZonedId, Locale, and Users
     */
    private static Locale localUser;
    private static ZoneId localTimeZone;
    private static Users loggedOnUser;

    /**
     * initializes labels and buttons on login screen.
     *  references languages based on local location.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("resources/languages", locale);
        userLabel.setText(resourceBundle.getString("usernameLabel"));
        passwordLabel.setText(resourceBundle.getString("passwordLabel"));
        loginLabel.setText(resourceBundle.getString("loginLabel"));
        login.setText(resourceBundle.getString("login"));
        exit.setText(resourceBundle.getString("exit"));
        localArea.setText(locale.getDisplayCountry());

    }

    /**
     * If hitting ENTER -> login
     * @param keyEvent pressing ENTER
     * @throws IOException in case of I/O error
     * @throws SQLException in case of SQL error
     */
    public void onEnter(KeyEvent keyEvent) throws IOException, SQLException {
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
    @FXML public void onLogin(ActionEvent actionEvent) throws IOException, SQLException {

        if (username.getText().isBlank() == false && password.getText().isBlank() == false) {
            boolean success = authenticateLogin(username.getText(), password.getText());
            Logger.auditLogin(username.getText(), success);

            if (success) {
                Stage stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                Parent scene = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                stage.setTitle("Main Screen");
                stage.setScene(new Scene(scene));
                stage.show();
            } else {
                loginError.setText("Invalid username or password.");
                System.out.println(username + "Didn't validate");
            }
        }
    }

    /**
     * Checks against entered username and password versus database.
     * @param user User_Name
     * @param pass Password
     * @return username and password from database
     * @throws SQLException in case of SQL error
     */
    public static Boolean authenticateLogin(String user, String pass) throws SQLException{
        String sql = "SELECT * FROM users WHERE User_Name =? AND Password = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, user);
        ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            loggedOnUser = new Users(rs.getString("User_Name"), rs.getInt("User_ID"));
            localUser = Locale.getDefault();
            localTimeZone = ZoneId.systemDefault();
            return true;
        }else{
            return false;
        }

    }

    /**
     *
     * @return name of loggedOnUser -> always test in this project
     */
    public static Users getLoggedOnUser(){
        return loggedOnUser;
    }

    /**
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

    /**
     * unused
     */
    @FXML void onPassword() {}
    @FXML void onUsername() {}
}
