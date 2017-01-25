package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Note;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gert on 1/25/17.
 */
public class NoteEditInput extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/NoteEditInput.fxml";
    @FXML
    private TextArea noteText;
    @FXML
    private Button saveButton;
    private Note note;

    public NoteEditInput(){
        this.initController(pageLoc,this,"Unable to Load Note Editor");

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.setOnAction((event -> {
            note.setNoteText(noteText.getText());
            TaskWithArgs<Note> updateNoteTask = DbController.INSTANCE.editNote(note);
            updateNoteTask.startTask();
        }));
    }
    public void setNote(Note note){
        this.note = note;
        this.noteText.setText(this.note.getNoteText());
    }
}
