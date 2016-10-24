package ledger.user_interface.ui_controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;

/**
 * Handles any tasks relevant for init of the Program.
 */
public class Startup extends Application {

    /**
     * Main entry point into the GUI for Ledger Software.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Kicks off the application flow
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        createLoginPage(primaryStage);
    }

    /**
     * Creates the login page to be the first thing seen in the application
     *
     * @param primaryStage
     */
    public void createLoginPage(Stage primaryStage) {
        LoginPageController loginController = new LoginPageController();
        Scene scene = new Scene(loginController);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ledger Login");
        primaryStage.show();
    }

}