package ledger.user_interface.ui_controllers.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ledger.controller.ImportController;
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
            IgnoredTransactionPopupController ignoredTransactionPopupController = new IgnoredTransactionPopupController();
            Scene scene = new Scene(ignoredTransactionPopupController);
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

        if (importFailures.duplicateTransactions.size() > 0)
            Startup.INSTANCE.runLater(() -> {
                DuplicateTransactionPopup popup = new DuplicateTransactionPopup(importFailures.duplicateTransactions);
                Scene scene = new Scene(popup);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setResizable(false);
                newStage.setTitle("Duplicate!");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            });

        closeWindow();
    }

    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
    }

}
