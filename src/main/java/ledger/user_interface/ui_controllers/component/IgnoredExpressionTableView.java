package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.IgnoredExpression;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.IsMatchConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * {@link TableView} for editing and deleting IgnoredExpressions
 */
public class IgnoredExpressionTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/IgnoredTransactionTableView.fxml";
    @FXML
    private TableColumn<IgnoredExpression, String> expressionColumn;
    @FXML
    private TableColumn<IgnoredExpression, Boolean> matchOrContainColumn;


    public IgnoredExpressionTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setOnKeyPressed(t -> {
            //Put your awesome application specific logic here
            if (t.getCode() == KeyCode.DELETE) {
                handleDelete();
            }
        });
        expressionColumn.setCellValueFactory(new PropertyValueFactory<>("expression"));
        expressionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        expressionColumn.setOnEditCommit(event -> {
            String exp = event.getNewValue();
            IgnoredExpression igEx = event.getRowValue();
            igEx.setExpression(exp);

            TaskNoReturn task = DbController.INSTANCE.editIgnoredExpression(igEx);
            task.startTask();
            task.waitForComplete();
        });

        matchOrContainColumn.setCellValueFactory(new PropertyValueFactory<>("match"));
        matchOrContainColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IsMatchConverter(), FXCollections.observableArrayList(true, false)));
        matchOrContainColumn.setOnEditCommit(event -> {
            Boolean value = event.getNewValue();
            IgnoredExpression igEx = event.getRowValue();
            igEx.setMatch(value);

            TaskNoReturn task = DbController.INSTANCE.editIgnoredExpression(igEx);
            task.startTask();
            task.waitForComplete();
        });

        DbController.INSTANCE.registerIgnoredExpressionSuccessEvent((ignored) -> this.asyncUpdateTableView());
        updateTableView();
    }

    private void handleDelete() {
        IgnoredExpression item = (IgnoredExpression) this.getSelectionModel().getSelectedItem();
        TaskNoReturn deleteTask = DbController.INSTANCE.deleteIgnoredExpression(item);
        deleteTask.RegisterFailureEvent((e) -> {
            asyncUpdateTableView();
            setupErrorPopup("Error deleting transaction.", e);
        });
        deleteTask.startTask();
        deleteTask.waitForComplete();

    }

    private void updateTableView() {
        TaskWithReturn<List<IgnoredExpression>> task = DbController.INSTANCE.getAllIgnoredExpressions();
        task.startTask();
        List<IgnoredExpression> ignoredExpressions = task.waitForResult();
        this.getItems().clear();
        this.getItems().addAll(ignoredExpressions);
    }

    private void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTableView);
    }
}
