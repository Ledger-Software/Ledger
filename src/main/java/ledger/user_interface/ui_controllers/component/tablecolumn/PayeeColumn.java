package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.io.input.TypeConversion;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells.PayeeComboBoxTableCell;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.PayeeEventHandler;
import ledger.user_interface.utils.PayeeComboboxComparator;
import ledger.user_interface.utils.PayeeStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TableColumn for Payees
 */
public class PayeeColumn extends TableColumn<Transaction, IEntity> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";
    private ObservableList observableAllPayees;
    private ObservableList observableAllAccounts;

    public PayeeColumn() {
        this.initController(pageLoc, this, "Unable to load PayeeColumn");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();
        this.observableAllPayees = FXCollections.observableList(allPayees);
        TaskWithReturn<List<Account>> getAllAccountsTask = DbController.INSTANCE.getAllAccounts();
        getAllAccountsTask.startTask();
        List<Account> allAccounts = getAllAccountsTask.waitForResult();
        this.observableAllAccounts = FXCollections.observableList(allAccounts);
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), observableAllPayees));
        this.setCellFactory(param -> new PayeeComboBoxTableCell(observableAllPayees, observableAllAccounts));
        this.setCellValueFactory(new PropertyValueFactory<Transaction, IEntity>("payee"));
//        this.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, IEntity>, ObservableValue<IEntity>>() {
//
//            @Override
//            public ObservableValue<IEntity> call(CellDataFeatures<Transaction, IEntity> param) {
//                Transaction trans =  param.getValue();
//                if (trans.getType().equals(TypeConversion.ACC_TRANSFER)) {
//                    return new SimpleObjectProperty(trans.getTransferAccount());
//                }
//
//
//                return new SimpleObjectProperty(trans.getPayee());
//            }
//
//
//        });
        this.setOnEditCommit(new PayeeEventHandler());
        this.setComparator(new PayeeComboboxComparator());

        DbController.INSTANCE.registerPayeeSuccessEvent((ignored) -> Startup.INSTANCE.runLater(this::updatePayeeList));
        DbController.INSTANCE.registerAccountSuccessEvent((ignored) -> Startup.INSTANCE.runLater(this::updateAccountList));
    }

    /**
     * function to update accounts in dropdowns for db success call
     */
    private void updateAccountList() {
        TaskWithReturn<List<Account>> getAllAccountsTask = DbController.INSTANCE.getAllAccounts();
        getAllAccountsTask.startTask();
        List<Account> allAccounts = getAllAccountsTask.waitForResult();

        for (Account currentAccount : allAccounts) {
            if (!observableAllAccounts.contains(currentAccount)) {
                observableAllAccounts.add(currentAccount);
            }
        }
    }

    /**
     * function to update payees in dropdowns for db success call
     */
    private void updatePayeeList() {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();

        for (Payee currentPayee : allPayees) {
            if (!observableAllPayees.contains(currentPayee)) {
                observableAllPayees.add(currentPayee);
            }
        }
    }
}
