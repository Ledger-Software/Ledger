package ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;

/**
 * Created by gert on 5/2/17.
 */
public class NoteColumnTableCell extends TableCell<Transaction, Transaction> {

    private TextArea noteText;
    private Transaction transaction;
    private TableView tableView;

    public NoteColumnTableCell(){
        tableView = this.getTableView();
        this.noteText = new TextArea("");
        this.noteText.setMinSize(125,30);
        this.noteText.setMaxHeight(30);
        this.setMaxHeight(30);
        this.setPrefHeight(30);
        this.noteText.setWrapText(true);
        this.noteText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    noteText.requestFocus();
                    noteText.setMaxHeight(90);
                    setMaxHeight(90);
                    setPrefHeight(90);
                    startEdit();
                } else {
                    noteText.setMaxHeight(30);
                    setMaxHeight(30);
                    setPrefHeight(30);
                    if(!transaction.getNote().getNoteText().equals(noteText.getText())) {
                        transaction.getNote().setNoteText(noteText.getText());
                        commitEdit(transaction);
                    }
                }
            }
        });
        this.noteText.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
                    noteText.appendText(System.lineSeparator());
                } else if (event.getCode() == KeyCode.ENTER) {
                    if(!transaction.getNote().getNoteText().equals(noteText.getText())) {
                        transaction.getNote().setNoteText(noteText.getText());
                        commitEdit(transaction);
                    }
                    event.consume();

                }
            }
        });
    }

    @Override
    protected void updateItem(Transaction transaction, boolean empty) {
            super.updateItem(transaction, empty);
            if (empty) {
            setText(null);
            setGraphic(null);
            } else {
                if(transaction==null||transaction.getNote()==null){

                } else{
                    this.transaction = transaction;
                    noteText.setText(this.transaction.getNote().getNoteText());
                }
                setGraphic(noteText);
            }
    }
    @Override
    public void commitEdit(Transaction item) {


            final TableView table = getTableView();
            if (table != null) {
                TablePosition position = new TablePosition(getTableView(), getTableRow().getIndex(), getTableColumn());
                TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(table, position, TableColumn.editCommitEvent(), item);
                Event.fireEvent(getTableColumn(), editEvent);
            }
            updateItem(item, false);
            if (table != null) {
                table.edit(-1, null);
            }


    }



}
