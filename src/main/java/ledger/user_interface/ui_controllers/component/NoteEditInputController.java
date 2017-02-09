package ledger.user_interface.ui_controllers.component;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.CallableMethod;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control that allows for the editing of notes inside of the TransactionTableview.
 */
public class NoteEditInputController extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/NoteEditInput.fxml";

    @FXML
    private TextArea noteText;
    @FXML
    private Button saveButton;

    private Transaction transaction;

    /**
     * Basic Constructor
     */
    public NoteEditInputController(Transaction transaction) {
        this.transaction = transaction;
        if (this.transaction.getNote() == null) {
            this.transaction.setNote(new Note(this.transaction.getId(), ""));
        }
        this.initController(pageLoc, this, "Unable to Load Note Editor");


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.toggleEditors(false);
        this.noteText.setText(this.transaction.getNote().getNoteText());
        this.noteText.focusedProperty().addListener(new NoteFocusChangeListener());
    }

    private void toggleEditors(boolean val) {
        if(val) {
            this.noteText.setMinHeight(90);
        } else{
            if(this.saveButton.isFocused()) {
                this.transaction.getNote().setNoteText(noteText.getText());
                TaskWithArgs<Transaction> updateNoteTask;
                updateNoteTask = DbController.INSTANCE.editTransaction(this.transaction);
                updateNoteTask.RegisterSuccessEvent(() -> Startup.INSTANCE.runLater(() -> {
                    this.toggleEditors(false);
                }));
                updateNoteTask.startTask();
                updateNoteTask.waitForComplete();
            } else{
                this.noteText.setText(transaction.getNote().getNoteText());
            }
            this.noteText.setMinHeight(30);
        }
        this.saveButton.setManaged(val);
    }
    private class NoteFocusChangeListener implements ChangeListener<Boolean> {


        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue){
                toggleEditors(true);
            }else{
                toggleEditors(false);
            }
        }
    }


}
