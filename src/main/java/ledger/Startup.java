package ledger;

import javafx.application.Application;
import javafx.stage.Stage;

public class Startup extends Application {

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