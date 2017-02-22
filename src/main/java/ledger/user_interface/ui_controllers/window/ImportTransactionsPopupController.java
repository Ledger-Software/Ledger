package ledger.user_interface.ui_controllers.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ledger.controller.ImportController;
import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.controller.register.TaskWithArgsReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.AccountDropdown;
import ledger.user_interface.ui_controllers.component.ConverterDropdown;
import ledger.user_interface.ui_controllers.component.FileSelectorButton;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls the input gathered from the Import Transaction UI (gets the file to import and type)
 */
public class ImportTransactionsPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private AccountDropdown accountDropdown;

    @FXML
    private Button importButton;

    @FXML
    private FileSelectorButton fileSelector;

    @FXML
    private ConverterDropdown converterSelector;

    @FXML
    private Button ignoreEditorButton;

    private final static String pageLoc = "/fxml_files/ImportTransactionsPopup.fxml";

    ImportTransactionsPopupController() {
        this.initController(pageLoc, this, "Error on transaction popup startup: ");
    }

    /**
     * Sets up action listener for the button on the page
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
        this.ignoreEditorButton.setOnAction(event -> {
            IgnoredExpressionPopupController ignoredExpressionPopupController = new IgnoredExpressionPopupController();
            Scene scene = new Scene(ignoredExpressionPopupController);
            this.createModal(this.getScene().getWindow(), scene, "Ignored Transactions");
        });
        fileSelector.addFileExtensionFilter(new ExtensionFilter("All files (*.*)", "*.*"));
        fileSelector.addFileExtensionFilter(new ExtensionFilter("CSV files (*.csv)", "*.csv"));
        fileSelector.addFileExtensionFilter(new ExtensionFilter("QFX files (*.qfx)", "*.qfx"));
        importButton.setOnAction(this::importFile);
        // TODO Hook up insert
    }

    private void importFile(ActionEvent actionEvent) {
        File file = fileSelector.getFile();
        if (file == null) {
            this.setupErrorPopup("Please make sure a file is selected.", new NullPointerException("Required field is empty."));
            return;
        }

        Account account = accountDropdown.getSelectedAccount();
        if (account == null) {
            this.setupErrorPopup("Please make sure an account is selected.", new NullPointerException("Required field is empty."));
            return;
        }

        ImportController.Converter converter = converterSelector.getFileConverter();
        if (converter == null) {
            this.setupErrorPopup("Please make sure a conversion type is selected.", new NullPointerException("Required field is empty."));
            return;
        }

        TaskWithArgsReturn<Account, ImportController.ImportFailures> task = ImportController.INSTANCE.importTransactions(converter, file, account);
        task.RegisterFailureEvent((e) -> Startup.INSTANCE.runLater(() -> {
            importButton.setDisable(false);
            this.setupErrorPopup("Unable to import data.", e);
        }));
        task.RegisterSuccessEvent(this::handleReturn);
        importButton.setDisable(true);

        task.startTask();
    }

    private void handleReturn(ImportController.ImportFailures importFailures) {
        for (Transaction fail : importFailures.failedTransactions) {
            // Todo: Do we even want to show the user?
        }
        ignoredTransactionsDialog(importFailures.ignoredTransactions,
                ()-> genericTransactionsWindow(importFailures.duplicateTransactions,()->{}, "Duplicate Transactions", "Duplicate!"));



        closeWindow();
    }
    private void genericTransactionsWindow(List<Transaction> transactions, CallableMethodVoidNoArgs method, String title, String topTitle){
        if (transactions.size() > 0)
            Startup.INSTANCE.runLater(() -> {
                GenericImportTransactionPopup popup = new GenericImportTransactionPopup(transactions, title);
                Scene scene = new Scene(popup);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setResizable(false);
                newStage.setTitle(topTitle);
                newStage.initModality(Modality.APPLICATION_MODAL);

                newStage.setOnCloseRequest(event -> {

                    try {
                        method.call();
                    } catch (Exception e) {
                        setupErrorPopup("Failed to run method", e);
                    }
                    newStage.close();

                });
                newStage.show();

            });
    }

    private void ignoredTransactionsDialog(List<Transaction> transactionList, CallableMethodVoidNoArgs method){
        if (transactionList.size() > 0)
            Startup.INSTANCE.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Ignored Transactions");
                alert.setHeaderText("There were " + transactionList.size() + " automatically ignored transactions .");
                alert.setContentText("Would you like to review them?");
                ButtonType reviewIgnored = new ButtonType("Review Transactions");
                ButtonType discardIgnored = new ButtonType("Discard Transactions");
                alert.getButtonTypes().setAll(reviewIgnored, discardIgnored);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == reviewIgnored) {
                    genericTransactionsWindow(transactionList,
                            method, "Ignored Transactions", "Ignored!");
                } else {
                    try {
                        method.call();
                    } catch (Exception e) {
                        setupErrorPopup("Failed to run method", e);
                    }
                }
            });
            else {
            try {
                method.call();
            } catch (Exception e) {
                setupErrorPopup("Failed to run method", e);
            }
        }
    }

    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> this.getScene().getWindow().fireEvent(new WindowEvent(this.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)));
    }

}
