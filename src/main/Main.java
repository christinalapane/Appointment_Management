package main;
import Database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
/**
 * @author Christina LaPane
 */

/**
 * Opens up to Login Page  and opens connection, then closes once exits
 */

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginPage.fxml"));
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args){
        DBConnection.openConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
