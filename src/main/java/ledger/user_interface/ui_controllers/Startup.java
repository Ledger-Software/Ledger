package ledger.user_interface.ui_controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.exception.StorageException;

/**
 * Handles any tasks relevant for init of the Program.
 */
public class Startup extends Application {

    public static Startup INSTANCE;

    private Stage stage;

    public Startup() {
        INSTANCE = this;
    }

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
        primaryStage.setOnCloseRequest(e -> shutdown());
        this.stage = primaryStage;

        createLoginPage();
    }

    private void shutdown() {
        try {
            DbController.INSTANCE.shutdown();
        } catch (StorageException e) {
        }

        Platform.exit();
    }

    /**
     * Creates the login page to be the first thing seen in the application
     */
    public void createLoginPage() {
        LoginPageController loginController = new LoginPageController();
        Scene scene = new Scene(loginController);
        switchScene(scene, "Ledger Login");
    }

    public void switchScene(Scene scene, String title) {
        this.stage.setScene(scene);
        this.stage.setTitle(title);
        this.stage.show();
    }

    public void runLater(CallableMethodVoidNoArgs method) {
        Platform.runLater(() -> {
            try {
                method.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}