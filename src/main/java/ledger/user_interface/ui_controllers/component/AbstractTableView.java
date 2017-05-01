package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.exception.StorageException;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.*;

import java.util.Comparator;
import java.util.List;

public abstract class AbstractTableView<T> extends TableView<T> {
    protected void asyncTableUpdate() {
        Startup.INSTANCE.runLater(this::updateTransactionTableView);
    }

    public abstract void updateTransactionTableView();
}
