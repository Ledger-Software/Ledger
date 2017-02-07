package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.AccountComparator;
import ledger.user_interface.utils.AccountStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TableColumn for amounts
 */
public class AccountColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public AccountColumn() {
        this.initController(pageLoc, this, "Unable to load AccountColumn");
    }

    ObservableList<Account> observableAllAccounts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskWithReturn<List<Account>> getAllAccountTask = DbController.INSTANCE.getAllAccounts();
        getAllAccountTask.startTask();
        List<Account> allAccounts = getAllAccountTask.waitForResult();
        this.observableAllAccounts = FXCollections.observableList(allAccounts);


        this.setCellValueFactory(new PropertyValueFactory<Transaction, Account>("account"));
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new AccountStringConverter(), observableAllAccounts));
        this.setOnEditCommit(this.accountEditHandler);
        this.setComparator(new AccountComparator());

        DbController.INSTANCE.registerAccountSuccessEvent(this::updateAccountList);
    }

    private void updateAccountList() {
        TaskWithReturn<List<Account>> getAllAccountTask = DbController.INSTANCE.getAllAccounts();
        getAllAccountTask.startTask();
        List<Account> allAccounts = getAllAccountTask.waitForResult();

        for (Account currentAccount : allAccounts) {
            if (!observableAllAccounts.contains(currentAccount)) {
                observableAllAccounts.add(currentAccount);
            }
        }
    }

    private EventHandler<javafx.scene.control.TableColumn.CellEditEvent<Transaction, Account>> accountEditHandler = new EventHandler<javafx.scene.control.TableColumn.CellEditEvent<Transaction, Account>>() {
        @Override
        public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, Account> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Account accountToSet = t.getNewValue();

            transaction.setAccount(accountToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction payee.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
