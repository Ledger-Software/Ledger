package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.IgnoredExpression;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.MatchOrContainsStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by gert on 2/7/17.
 */
public class IgnoredTransactionTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/IgnoredTransactionTableView.fxml";
    @FXML
    public TableColumn<IgnoredExpression, String> expressionColumn;
    @FXML
    public TableColumn<IgnoredExpression, Boolean> matchOrContainColumn;



    public IgnoredTransactionTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        expressionColumn.setCellValueFactory(new PropertyValueFactory<IgnoredExpression, String>("expression"));
        expressionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        expressionColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<IgnoredExpression, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<IgnoredExpression, String> event) {
                String exp = event.getNewValue();
                IgnoredExpression igEx = event.getRowValue();
                igEx.setExpression(exp);

                TaskWithArgs task = DbController.INSTANCE.editIgnoredExpression(igEx);
                task.startTask();
                task.waitForComplete();
            }
        });

        matchOrContainColumn.setCellValueFactory(new PropertyValueFactory<IgnoredExpression, Boolean>("matchOrContain"));
        matchOrContainColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new MatchOrContainsStringConverter(), FXCollections.observableArrayList(true,false)));
        matchOrContainColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<IgnoredExpression, Boolean>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<IgnoredExpression, Boolean> event) {
                Boolean value = event.getNewValue();
                IgnoredExpression igEx = event.getRowValue();
                igEx.setMatchOrContain(value);

                TaskWithArgs task = DbController.INSTANCE.editIgnoredExpression(igEx);
                task.startTask();
                task.waitForComplete();
            }
        });

        DbController.INSTANCE.registerIgnoredExpressionSuccessEvent(this::asyncUpdateTableView);
        updateTableView();
    }

    public void updateTableView() {
        TaskWithReturn<List<IgnoredExpression>> task = DbController.INSTANCE.getAllIgnoredExpressions();
        task.startTask();
        List<IgnoredExpression> ignoredExpressions = task.waitForResult();
        this.getItems().clear();
        this.getItems().addAll(ignoredExpressions);
    }

    public void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTableView);
    }
}
