package ledger.user_interface.ui_controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    default void setupErrorPopup(String s) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(s);

        /*
        * The code below will allow us to change the icon in the upper left to match our application icon.
        * We should use this if we ever get an icon for our app. In future JavaFX versions, dialogs should
        * use the same icon as the application that its running from. This is not true currently (in 8u40)
        * */

        // Can grab the application icon like this
        // dialog.initOwner(otherStage);

        //Or use these below to use a different one.

        // Get the Stage.
        //Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        //stage.getIcons().add(new Image(this.getClass().getResource("login.png").toString()));

        alert.showAndWait();
    }

    /**
     * Generating code for the error popup
     *
     * @param s a string containing the error message
     * @param e the exception caused be the error(s)
     */
    default void setupErrorPopup(String s, Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(s);
        alert.setContentText(e.getMessage());

        /*
        * The code below will allow us to change the icon in the upper left to match our application icon.
        * We should use this if we ever get an icon for our app. In future JavaFX versions, dialogs should
        * use the same icon as the application that its running from. This is not true currently (in 8u40)
        * */

        // Can grab the application icon like this
        // dialog.initOwner(otherStage);

        //Or use these below to use a different one.

        // Get the Stage.
        //Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        //stage.getIcons().add(new Image(this.getClass().getResource("login.png").toString()));


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
     * @param s          the previously initialized Scene that is set on the new Stage
     * @param windowName The name of the window to be created
     */
    default void createModal(Scene s, String windowName) {
        createModal(null, s, windowName);
    }

    default void createModal(Window parrent, Scene child, String windowName) {
        createModal(parrent, child, windowName, false);
    }

    default void createModal(Window parrent, Scene child, String windowName, boolean resizeable) {
        Stage newStage = new Stage();
        newStage.setScene(child);
        newStage.setResizable(resizeable);
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

    default void createIntroductionAlerts(String title, String message, Alert a) {
        a.setContentText(message);
        a.getButtonTypes().add(ButtonType.OK);
        DialogPane root = a.getDialogPane();
        Stage stage = new Stage(StageStyle.DECORATED);
        for (ButtonType bt : root.getButtonTypes()) {
            ButtonBase b = (ButtonBase) root.lookupButton(bt);
            b.setOnAction(e -> {
                stage.close();
            });
        }
        root.getScene().setRoot(new Group());
        root.setPadding(new Insets(10, 0, 10, 0));
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
