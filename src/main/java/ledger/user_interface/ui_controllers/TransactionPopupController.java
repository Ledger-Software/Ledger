package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.*;
import ledger.exception.StorageException;
import ledger.user_interface.utils.InputSanitization;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls and manipulates the input given by the user when manually adding a transaction
 */
public class TransactionPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox clearedCheckBox;
    @FXML
    private ComboBox<String> payeeText;
    @FXML
    private ComboBox<Account> accountText;
    @FXML
    private TextField categoryText;
    @FXML
    private TextField amountText;
    @FXML
    private TextArea notesText;
    @FXML
    private ComboBox<String> typeText;
    @FXML
    private Button addTrnxnSubmitButton;

    private Date date;
    private boolean pending;
    private List<Payee> existingPayees;
    private Payee payee;
    private List<Account> existingAccounts;
    private Account account;
    private List<Tag> category;
    private int amount;
    private Note notes;
    private List<Type> existingTypes;
    private Type type;

    private final static String pageLoc = "/fxml_files/AddTransactionPopup.fxml";

    TransactionPopupController() {
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
        try {
            TaskWithReturn<List<Payee>> payeesTask = DbController.INSTANCE.getAllPayees();
            payeesTask.RegisterFailureEvent((e) -> printStackTrace(e));
            payeesTask.RegisterSuccessEvent((list) -> {
                this.payeeText.setItems(FXCollections.observableArrayList(toStringListPayee(list)));
                this.payeeText.setEditable(true);

            });
            payeesTask.startTask();
            this.existingPayees = payeesTask.waitForResult();

        } catch (StorageException e) {
            this.setupErrorPopup("Error on payee submission.", e);
        }
        try {
            TaskWithReturn<List<Account>> accountsTask = DbController.INSTANCE.getAllAccounts();
            accountsTask.RegisterFailureEvent((e) -> printStackTrace(e));

            accountsTask.RegisterSuccessEvent((list) -> {
                this.accountText.setItems((FXCollections.observableArrayList(list)));
            });
            accountsTask.startTask();
            this.existingAccounts = accountsTask.waitForResult();
        } catch (StorageException e) {
            this.setupErrorPopup("Error on account submission.", e);
        }
        try {
            TaskWithReturn<List<Type>> typeTask = DbController.INSTANCE.getAllTypes();
            typeTask.RegisterFailureEvent((e) -> printStackTrace(e));

            typeTask.RegisterSuccessEvent((list) -> {
                this.typeText.setItems(FXCollections.observableArrayList(toStringListType(list)));
            });
            typeTask.startTask();
            this.existingTypes = typeTask.waitForResult();
        } catch (StorageException e) {
            this.setupErrorPopup("Error on type submission.", e);
        }
        this.typeText.setEditable(true);
        this.addTrnxnSubmitButton.setOnAction((event) -> {
            try {
                Transaction transaction = getTransactionSubmission();

                TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(transaction);
                task.RegisterSuccessEvent(() -> closeWindow());
                task.RegisterFailureEvent((e) -> printStackTrace(e));

                task.startTask();
            } catch (StorageException e) {
                this.setupErrorPopup("Error on transaction submission.", e);
            }
        });
    }

    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
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
        try {
            LocalDate localDate = this.datePicker.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            this.date = Date.from(instant);

            this.pending = !this.clearedCheckBox.isSelected();

            this.payee = fromBoxPayee(this.payeeText.getValue());

            this.account = this.accountText.getValue();
            this.category = new ArrayList<Tag>() {{
                add(new Tag(categoryText.getText(), ""));
            }};

            if (InputSanitization.isInvalidAmount(this.amountText.getText())) {
                this.setupErrorPopup("Invalid amount entry.", new Exception());
            }
            this.amount = (int) (Double.parseDouble(this.amountText.getText()) * 100);

            this.notes = new Note(this.notesText.getText());


            this.type = fromBoxType(this.typeText.getValue());
        } catch (NullPointerException e) {
            this.setupErrorPopup("Error getting transaction information - ensure all fields are populated.", e);
        }
        Transaction t = new Transaction(this.date, this.type, this.amount, this.account,
                this.payee, this.pending, this.category, this.notes);

        return t;
    }

    private Payee fromBoxPayee(String name) {

        for (Payee pay : this.existingPayees) {
            if (pay.getName().equals(name))
                return pay;

        }
        return new Payee(name, "Auto Generated from Add Transaction");

    }

    private Type fromBoxType(String name) {

        for (Type type : this.existingTypes) {
            if (type.getName().equals(name))
                return type;

        }
        return new Type(name, "Auto Generated from Add Transaction");

    }

    private List<String> toStringListPayee(List<Payee> payees) {
        List<String> listy = new ArrayList<>();
        for (Payee payee : payees
                ) {
            listy.add(payee.getName());
        }
        return listy;
    }

    private List<String> toStringListType(List<Type> types) {
        List<String> listy = new ArrayList<>();
        for (Type type : types
                ) {
            listy.add(type.getName());
        }
        return listy;
    }
}
