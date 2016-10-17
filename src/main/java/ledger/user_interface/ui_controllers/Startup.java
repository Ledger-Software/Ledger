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

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        // createTransactionPopup(primaryStage);
        // createAccountPopup(primaryStage);
        createLoginPage(primaryStage);
        // createMainPage(primaryStage);
    }

    public void createMainPage(Stage primaryStage) {
        MainPageController mainPageController = new MainPageController();
        Scene scene = new Scene(mainPageController);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ledger");
        primaryStage.show();
    }

    public void createTransactionPopup(Stage primaryStage) {
        TransactionPopupController trxnController = new TransactionPopupController();
        Scene scene = new Scene(trxnController);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ledger");
        primaryStage.show();
    }

    public void createAccountPopup(Stage primaryStage) {
        AccountPopupController accountController = new AccountPopupController();
        Scene scene = new Scene(accountController);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ledger");
        primaryStage.show();
    }

    public void createLoginPage(Stage primaryStage) {
        LoginPageController loginController = new LoginPageController();
        Scene scene = new Scene(loginController);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ledger");
        primaryStage.show();
    }

}