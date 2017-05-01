package ledger.user_interface.ui_controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.*;
import ledger.controller.register.CallableMethodVoidNoArgs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * To assist in unifying all UI controllers common code
 */
public interface IUIController {

    Image image = new Image("/images/Letter-T-blue-icon.png");

    /**
     * Generating code for the error popup
     *
     * @param s a string containing the error message
     */
    default void setupErrorPopup(String s) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(s);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

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

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(image);

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
    default void createModal(Scene s, String windowName, CallableMethodVoidNoArgs onClose) {
        createModal(null, s, windowName, false, Modality.WINDOW_MODAL, StageStyle.UTILITY, onClose);
    }

    default void createModal(Window parent, Scene child, String windowName) {
        createModal(parent, child, windowName, false);
    }

    default void createModal(Window parent, Scene child, String windowName, boolean resizeable) {
        createModal(parent, child, windowName, resizeable, Modality.WINDOW_MODAL);
    }

    default void createModal(Window parent, Scene child, String windowName, boolean resizeable, Modality modality) {
        createModal(parent, child, windowName, resizeable, modality, StageStyle.UTILITY);
    }
    default void createModal(Window parent, Scene child, String windowName, boolean resizeable, Modality modality, StageStyle stageStyle){
        createModal(parent, child, windowName, resizeable, modality, stageStyle, ()-> {});
        }
    default void createModal(Window parent, Scene child, String windowName, boolean resizeable, Modality modality, StageStyle stageStyle, CallableMethodVoidNoArgs onClose) {
        Stage newStage = new Stage();
        newStage.initOwner(parent);
        newStage.setScene(child);
        newStage.setResizable(resizeable);
        newStage.setTitle(windowName);
        newStage.initModality(modality);
        newStage.initStyle(stageStyle);
//        newStage.setAlwaysOnTop(true);
        newStage.onCloseRequestProperty().setValue(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                    try {
                        onClose.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        });
        newStage.getIcons().add(image);

        newStage.show();

        if (parent != null) {
            double x = parent.getX();
            double y = parent.getY();

            double xSize = parent.getWidth();
            double ySize = parent.getHeight();

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
            b.setOnAction(e -> stage.close());
        }
        root.getScene().setRoot(new Group());
        root.setPadding(new Insets(10, 0, 10, 0));
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.getIcons().addAll(image);
        stage.show();
    }
}
