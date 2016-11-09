package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.*;
import ledger.user_interface.utils.InputSanitization;
import ledger.user_interface.utils.PayeeStringConverter;
import ledger.user_interface.utils.TypeStringConverter;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 11/9/2016.
 */
public class UserTransactionInput extends GridPane implements IUIController, Initializable {
    private static final String pageLog = "/fxml_files/UserTransactionInput.fxml";

    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox pendingCheckBox;
    @FXML
    private ComboBox<Payee> payeeText;
    @FXML
    private ComboBox<Account> accountText;
    @FXML
    private TextField categoryText;
    @FXML
    private TextField amountText;
    @FXML
    private TextArea notesText;
    @FXML
    private ComboBox<Type> typeText;

    public UserTransactionInput() {
        this.initController(pageLog, this, "Unable to load User Transaction Input");
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
        this.payeeText.setDisable(true);
        this.accountText.setDisable(true);
        this.typeText.setDisable(true);

        TaskWithReturn<List<Payee>> payeesTask = DbController.INSTANCE.getAllPayees();
        payeesTask.RegisterFailureEvent((e) -> printStackTrace(e));
        payeesTask.RegisterSuccessEvent((list) -> {
            ObservableList<Payee> payees = FXCollections.observableList(list);
            this.payeeText.setItems(payees);
            this.payeeText.setConverter(new PayeeStringConverter());
            this.payeeText.setEditable(true);
            this.payeeText.setDisable(false);
        });
        payeesTask.startTask();

        TaskWithReturn<List<Account>> accountsTask = DbController.INSTANCE.getAllAccounts();
        accountsTask.RegisterFailureEvent((e) -> printStackTrace(e));

        accountsTask.RegisterSuccessEvent((list) -> {
            this.accountText.setItems((FXCollections.observableArrayList(list)));
            this.accountText.setDisable(false);
        });
        accountsTask.startTask();

        TaskWithReturn<List<Type>> typeTask = DbController.INSTANCE.getAllTypes();
        typeTask.RegisterFailureEvent((e) -> printStackTrace(e));

        typeTask.RegisterSuccessEvent((list) -> {
            this.typeText.setItems(FXCollections.observableArrayList(list));
            this.typeText.setConverter(new TypeStringConverter());
            this.typeText.setDisable(false);
        });
        typeTask.startTask();

        this.typeText.setEditable(true);
    }


    private void printStackTrace(Exception e) {
        e.printStackTrace();
    }

    /**
     * Get the user input from the add Transaction popup and returns a new Transaction object.
     *
     * @return a new Transaction object consisting of user input
     */
    public Transaction getTransactionSubmission() {

        LocalDate localDate = this.datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);

        boolean pending = this.pendingCheckBox.isSelected();

        Payee payee = this.payeeText.getValue();
        if (InputSanitization.isInvalidPayee(payee)) {
            this.setupErrorPopup("Invalid Payee entry.", new Exception());
            return null;
        }
        if (this.accountText.getSelectionModel().isEmpty()) {
            this.setupErrorPopup("No account selected.", new Exception());

            return null;
        }
        Account account = this.accountText.getValue();

        List<Tag> category = new ArrayList<Tag>() {{
            add(new Tag(categoryText.getText(), ""));
        }};

        if (InputSanitization.isInvalidAmount(this.amountText.getText())) {
            this.setupErrorPopup("Invalid amount entry.", new Exception());
            return null;
        }
        String amountString = this.amountText.getText();
        if (amountString.charAt(0) == '$') {
            amountString = amountString.substring(1);
        }
        double amountToSetDecimal = Double.parseDouble(amountString);
        int amount = (int) Math.round(amountToSetDecimal * 100);

        Note notes = new Note(this.notesText.getText());

        if (this.typeText.getSelectionModel().isEmpty()) {
            this.setupErrorPopup("No type selected.", new Exception());
            return null;
        }
        Type type = this.typeText.getValue();

        Transaction t = new Transaction(date, type, amount, account,
                payee, pending, category, notes);

        return t;
    }

    public void setTransaction(Transaction currentTrans) {
        this.datePicker.setValue(currentTrans.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
