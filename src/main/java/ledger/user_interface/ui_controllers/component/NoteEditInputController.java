package ledger.user_interface.ui_controllers.component;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control that allows for the editing of notes inside of the TransactionTableView.
 */
public class NoteEditInputController extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/NoteEditInput.fxml";

    @FXML
    private TextArea noteText;


    private final Transaction transaction;

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
        this.noteText.setOnKeyPressed(new NoteKeyEventHandler());
    }

    private void toggleEditors(boolean isToggleOpen) {
        if (isToggleOpen) {
            this.noteText.setMinHeight(90);
        } else {
            this.noteText.setText(transaction.getNote().getNoteText());
            this.noteText.setMinHeight(30);
        }

    }

    private class NoteFocusChangeListener implements ChangeListener<Boolean> {


        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            toggleEditors(newValue);
        }
    }

    private class NoteKeyEventHandler implements EventHandler<KeyEvent> {


        @Override
        public void handle(KeyEvent event) {
            if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
                noteText.appendText(System.lineSeparator());
            } else if (event.getCode() == KeyCode.ENTER) {
                transaction.getNote().setNoteText(noteText.getText());
                TaskWithArgs<Transaction> updateNoteTask;
                updateNoteTask = DbController.INSTANCE.editTransaction(transaction);
                updateNoteTask.RegisterSuccessEvent(() -> Startup.INSTANCE.runLater(() -> toggleEditors(false)));
                updateNoteTask.startTask();
                updateNoteTask.waitForComplete();
            }
        }
    }


}
