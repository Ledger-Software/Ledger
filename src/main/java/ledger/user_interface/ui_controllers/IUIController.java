package ledger.user_interface.ui_controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * To assist in unifying all UI controllers common code
 */
public interface IUIController {

    /**
     * Generating code for the error popup
     *
     * @param s a string containing the error message
     */
    default void setupErrorPopup(String s){
        GenericPopupController errCon = new GenericPopupController(s, "Error!");
        Scene scene = new Scene(errCon);
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Error!");
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.show();
    }

    /**
     * Initializes the controllers - to be used inside constructor
     *
     * @param pageLoc location of the fxml file
     * @param c the controller being initialized
     * @param errMsg error message in for case of that specific controller
     */
    default void initController(String pageLoc, IUIController c, String errMsg) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(c);
            loader.setRoot(c);
            loader.load();
        } catch (IOException e) {
            this.setupErrorPopup(errMsg +  e);
        }

    }

    /**
     * Sets the stage and the scene for the new modal
     *
     * @param s the previously initialized Scene that is set on the new Stage
     */
    default void createModal(Scene s, String windowName){
            Stage newStage = new Stage();
            newStage.setScene(s);
            newStage.setTitle(windowName);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.show();
    }
}
