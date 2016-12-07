package ledger.user_interface.ui_controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.database.entity.Account;
import ledger.exception.StorageException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class MainPageController extends GridPane implements Initializable, IUIController {
    @FXML
    private Button addAccountBtn;
    @FXML
    private Button importTransactionsBtn;
    @FXML
    private Button trackSpendingBtn;
    @FXML
    private Button addTransactionBtn;
    @FXML
    private Button exportDataBtn;
    @FXML
    private FilteringAccountDropdown chooseAccount;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField searchTextField;

    // Transaction table UI objects
    @FXML
    private TransactionTableView transactionTableView;

    @FXML
    private Button logoutBtn;

    private final static String pageLoc = "/fxml_files/MainPage.fxml";

    MainPageController() {
        this.initController(pageLoc, this, "Error on main page startup: ");
    }

    /**
     * Sets up action listeners for the page, allowing for navigation
     * <p>
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation The location used to resolve relative paths for the root object, or
     *                         <tt>null</tt> if the location is not known.
     * @param resources        The resources used to localize the root object, or <tt>null</tt> if
     *                         the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.addAccountBtn.setOnAction((event) -> {
            createAccountPopup();
        });

        this.addTransactionBtn.setOnAction((event) -> {
            createAddTransPopup();
        });

        this.trackSpendingBtn.setOnAction((event) -> {
            createExpenditureChartsPage();
        });

        this.importTransactionsBtn.setOnAction((event) -> {
            createImportTransPopup();
        });

        this.logoutBtn.setOnAction((event) -> {
            logout(event);
        });

        this.searchButton.setOnAction(this::searchClick);
        this.clearButton.setOnAction(this::clearSearch);

        this.searchTextField.setOnAction(this::searchClick);

        this.exportDataBtn.setOnAction((event) -> {
            exportData();
        });

        this.chooseAccount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
            @Override
            public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                transactionTableView.updateAccountFilter(chooseAccount.getSelectedAccount());
            }
        });
    }

    private void clearSearch(ActionEvent actionEvent) {
        searchTextField.setText("");
        transactionTableView.updateSearchFilterString("");
    }

    private void searchClick(ActionEvent actionEvent) {
        String searchText = searchTextField.getText();
        transactionTableView.updateSearchFilterString(searchText);
    }

    /**
     * Redirects to login screen and properly closes the database file
     *
     * @param event action event used to reset the stage
     */
    private void logout(ActionEvent event) {
        LoginPageController login = new LoginPageController();
        Scene scene = new Scene(login);
        Stage currStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currStage.close();
        Stage newStage = new Stage();
        newStage.setResizable(false);
        newStage.setScene(scene);;
        newStage.setTitle("Ledger Login");
        newStage.show();
        try {
            DbController.INSTANCE.shutdown();
        } catch (StorageException e) {
            this.setupErrorPopup("Database file did not close properly.", e);
        }
    }

    /**
     * Creates the Import Transaction modal
     */
    private void createImportTransPopup() {
        ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
        Scene scene = new Scene(importTrxnController);
        this.createModal(scene, "Import Transactions");
    }

    /**
     * Creates the expenditure chart page
     */
    private void createExpenditureChartsPage() {
        ExpenditureChartsController chartController = new ExpenditureChartsController();
        Scene scene = new Scene(chartController);
        this.createModal(scene, "Expenditure Charts");
    }

    /**
     * Creates the Add Transaction modal
     */
    private void createAddTransPopup() {
        TransactionPopupController trxnController = new TransactionPopupController();
        Scene scene = new Scene(trxnController);
        this.createModal(scene, "Add Transaction");
    }

    /**
     * Creates the Add Account modal
     */
    private void createAccountPopup() {
        AccountPopupController accountController = new AccountPopupController();
        Scene scene = new Scene(accountController);
        this.createModal(scene, "Add Account");
    }

    /**
     * Exports the database file to the chosen directory.
     */
    private void exportData() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File saveLocation = chooser.showDialog(this.exportDataBtn.getScene().getWindow());
        File currentDbFile = DbController.INSTANCE.getDbFile();
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
        String fileName = timeStamp + currentDbFile.getName();
        File newDbFile = new File(saveLocation.toPath().toString(), fileName);

        int numFiles = 1;
        while (newDbFile.exists()) {
            fileName = timeStamp + "(" + numFiles + ")" + currentDbFile.getName();
            newDbFile = new File(saveLocation.toPath().toString(), fileName);
        }

        try {
            DbController.INSTANCE.shutdown();
            Files.copy(currentDbFile.toPath(), newDbFile.toPath());
        } catch (IOException e) {
            this.setupErrorPopup("Unable to properly copy data.", e);
            e.printStackTrace();
        } catch (StorageException e) {
            this.setupErrorPopup("Unable to properly close database.", e);
            e.printStackTrace();
        }
        this.displayPasswordPrompt();
    }

    private void displayPasswordPrompt() {
        PasswordPromptController promptController = new PasswordPromptController();
        Scene scene = new Scene(promptController);
        this.createModal(scene, "Verify Password");
    }
}
