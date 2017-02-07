package ledger.user_interface.ui_controllers;


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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Control that allows for the editing of notes inside of the TransactionTableview.
 */
public class NoteEditInputController extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/NoteEditInput.fxml";
    @FXML
    private TextField compactNoteText;
    @FXML
    private TextArea noteText;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private Transaction transaction;
    private CallableMethod<Boolean> collapseMethod;

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
        this.compactNoteText.setText(this.transaction.getNote().getNoteText());
        toggleEditors(false);
        this.
                saveButton.setOnAction((event -> {
            this.transaction.getNote().setNoteText(noteText.getText());
            TaskWithArgs<Transaction> updateNoteTask;
            updateNoteTask = DbController.INSTANCE.editTransaction(this.transaction);
            updateNoteTask.RegisterSuccessEvent(() -> Startup.INSTANCE.runLater(() -> {
                this.toggleEditors(false);
                this.compactNoteText.setText(this.transaction.getNote().getNoteText());
            }));
            updateNoteTask.startTask();
            updateNoteTask.waitForComplete();
        }));
        this.cancelButton.setOnAction((event -> {
            toggleEditors(false);
        }));
        this.compactNoteText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                toggleEditors(true);
            }
        });
    }

    private void toggleEditors(boolean val) {
        this.noteText.setText(this.transaction.getNote().getNoteText());
        this.compactNoteText.setManaged(!val);
        this.compactNoteText.setVisible(!val);
        this.noteText.setManaged(val);
        this.cancelButton.setManaged(val);
        this.saveButton.setManaged(val);
    }


}
