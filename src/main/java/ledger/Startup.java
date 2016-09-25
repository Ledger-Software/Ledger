package ledger;

import javafx.application.Application;
import javafx.stage.Stage;

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
        primaryStage.setAlwaysOnTop(true); // Development Settings
        primaryStage.setTitle("Ledger");
        primaryStage.show();
    }
}