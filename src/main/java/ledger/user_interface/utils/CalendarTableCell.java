package ledger.user_interface.utils;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.component.tablecolumn.CalenderColumn;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;

import static java.time.format.FormatStyle.MEDIUM;

public class CalendarTableCell extends TableCell<RecurringTransaction, Calendar> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(MEDIUM);
    private final DatePicker datePicker;

    public CalendarTableCell(CalenderColumn column) {
        this.datePicker = new DatePicker();
        this.datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                String rv = null;
                if (object != null) {
                    rv = formatter.format(object);
                }
                return rv;
            }

            @Override
            public LocalDate fromString(String string) {
                LocalDate rv = null;
                if (!Optional.ofNullable(string).orElse("").isEmpty()) {
                    rv = LocalDate.parse(string, formatter);
                }
                return rv;
            }
        });
        // Manage editing
        this.datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                final TableView<RecurringTransaction> tableView = getTableView();
                tableView.getSelectionModel().select(getTableRow().getIndex());
                tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
            }
        });
        this.datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditing()) {
                Instant instant = Instant.from(newValue.atStartOfDay(ZoneId.systemDefault()));
                Calendar newDate = Calendar.getInstance();
                newDate.setTimeInMillis(instant.toEpochMilli());

                commitEdit(newDate);
            }
        });

        // Bind this cells editable property to the whole column
        editableProperty().bind(column.editableProperty());
        // and then use this to configure the date picker
        contentDisplayProperty().bind(Bindings
                .when(editableProperty())
                .then(ContentDisplay.GRAPHIC_ONLY)
                .otherwise(ContentDisplay.TEXT_ONLY)
        );
    }

    @Override
    protected void updateItem(Calendar item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            LocalDate localdate = LocalDate.of(item.get(Calendar.YEAR), item.get(Calendar.MONTH), item.get(Calendar.DAY_OF_MONTH));

            // Date Picker can handle null values
            this.datePicker.setValue(localdate);
            setGraphic(this.datePicker);
            setText(formatter.format(localdate));
        }
    }

}
