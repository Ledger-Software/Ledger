package ledger.user_interface;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        testPopup(primaryStage);
        primaryStage.show();
    }

    private void testPopup(Stage primaryStage){
        final Stage dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        VBox dialogVBox = new VBox(20);
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogVBox.getChildren().addAll(new Text("Some stuff"));
        dialog.setScene(dialogScene);
        dialog.show();
    }
}