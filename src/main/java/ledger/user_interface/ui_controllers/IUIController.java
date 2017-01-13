package ledger.user_interface.ui_controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * To assist in unifying all UI controllers common code
 */
public interface IUIController {

    /**
     * Generating code for the error popup
     *
     * @param s a string containing the error message
     */
    default void setupErrorPopup(String s, Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(s);
        if (e.getMessage() != null) {
            alert.setContentText(e.getMessage());
        }

        if (e.getStackTrace() != null) {
            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.showAndWait();
    }

    /**
     * Initializes the controllers - to be used inside constructor
     *
     * @param pageLoc location of the fxml file
     * @param c       the controller being initialized
     * @param errMsg  error message in for case of that specific controller
     */
    default void initController(String pageLoc, IUIController c, String errMsg) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(c);
            loader.setRoot(c);
            loader.load();
        } catch (IOException e) {
            System.out.println(errMsg + " : " + e);
            e.printStackTrace();
        }

    }

    /**
     * Sets the stage and the scene for the new modal
     *
     * @param s the previously initialized Scene that is set on the new Stage
     */
    default void createModal(Scene s, String windowName) {
        createModal(null, s, windowName);
    }

    default void createModal(Window parrent, Scene child, String windowName) {
        Stage newStage = new Stage();
        newStage.setScene(child);
        newStage.setResizable(false);
        newStage.setTitle(windowName);
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.show();

        if (parrent != null) {
            double x = parrent.getX();
            double y = parrent.getY();

            double xSize = parrent.getWidth();
            double ySize = parrent.getHeight();

            double centerX = x + xSize / 2;
            double centerY = y + ySize / 2;

            double childX = newStage.getWidth();
            double childY = newStage.getHeight();

            newStage.setX(centerX - childX / 2);
            newStage.setY(centerY - childY / 2);
        }
    }
}
