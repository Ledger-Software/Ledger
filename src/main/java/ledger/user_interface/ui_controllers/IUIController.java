package ledger.user_interface.ui_controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        try {
            ErrorPopupController errCon = new ErrorPopupController(s);
            Scene scene = new Scene(errCon);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Ledger");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.show();
        } catch (Exception e) {
            System.out.println("Error on triggering add error screen: " + e);
        }
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
        } catch (Exception e) {
            this.setupErrorPopup(errMsg +  e);
        }

    }

    default void createModal(Scene s){
            Stage newStage = new Stage();
            newStage.setScene(s);
            newStage.setTitle("Ledger");
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.show();
    }
}
