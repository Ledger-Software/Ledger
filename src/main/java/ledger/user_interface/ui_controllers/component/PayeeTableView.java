package ledger.user_interface.ui_controllers.component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * {@link TableView} for showing and editing {@link Payee}
 * Auto pulls from the database
 */
public class PayeeTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/PayeeTableView.fxml";

    @FXML
    private TableColumn<Payee, String> nameColumn;
    @FXML
    private TableColumn<Payee, String> descriptionColumn;

    public PayeeTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            String name = event.getNewValue();
            Payee payee = event.getRowValue();
            payee.setName(name);

            TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
            task.startTask();
            task.waitForComplete();
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            String description = event.getNewValue();
            Payee payee = event.getRowValue();
            payee.setDescription(description);

            TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
            task.startTask();
            task.waitForComplete();
        });


        DbController.INSTANCE.registerPayeeSuccessEvent(this::asyncUpdateTableView);
        updateTableView();
    }

    private void updateTableView() {
        TaskWithReturn<List<Payee>> task = DbController.INSTANCE.getAllPayees();
        task.startTask();
        List<Payee> payees = task.waitForResult();
        this.getItems().clear();
        this.getItems().addAll(payees);
    }

    private void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTableView);
    }
}
