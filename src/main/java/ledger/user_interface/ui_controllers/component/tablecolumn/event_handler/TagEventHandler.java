package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AbstractTableView;
import ledger.user_interface.utils.TaggableSwitch;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gert on 5/2/17.
 */
public class TagEventHandler implements EventHandler<TableColumn.CellEditEvent<ITaggable, ITaggable>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<ITaggable, ITaggable> t) {

        ITaggable transactionToSet = t.getNewValue();

        if (transactionToSet instanceof Transaction && ((Transaction) transactionToSet).getType().getName().equals("Transfer")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Cleared Status");
            alert.setHeaderText("This change will cause the matching transfer to change as well.");
            alert.setContentText("The Tags will change to " + tagsToString(transactionToSet.getTags()) + ". Is this okay?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() != ButtonType.OK) {
                ((AbstractTableView) t.getTableView()).updateTransactionTableView();
                return;
            }
        }

        TaskNoReturn task = TaggableSwitch.edit(transactionToSet);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction tags field.", e));

        task.startTask();
        task.waitForComplete();
    }

    private String tagsToString(List<Tag> tags) {
        return String.join(", ", tags.stream().map(Tag::getName).collect(Collectors.toList()));
    }
}
