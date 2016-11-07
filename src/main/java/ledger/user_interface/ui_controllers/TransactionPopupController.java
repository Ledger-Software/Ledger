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
import ledger.user_interface.utils.InputSanitization;
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
 * Controls and manipulates the input given by the user when manually adding a transaction
 */
public class TransactionPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox pendingCheckBox;
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
    private ComboBox<Type> typeText;
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
        TaskWithReturn<List<Payee>> payeesTask = DbController.INSTANCE.getAllPayees();
        payeesTask.RegisterFailureEvent((e) -> printStackTrace(e));
        payeesTask.RegisterSuccessEvent((list) -> {
            this.payeeText.setItems(FXCollections.observableArrayList(toStringListPayee(list)));
            this.payeeText.setEditable(true);

        });
        payeesTask.startTask();
        this.existingPayees = payeesTask.waitForResult();

        TaskWithReturn<List<Account>> accountsTask = DbController.INSTANCE.getAllAccounts();
        accountsTask.RegisterFailureEvent((e) -> printStackTrace(e));

        accountsTask.RegisterSuccessEvent((list) -> {
            this.accountText.setItems((FXCollections.observableArrayList(list)));
        });
        accountsTask.startTask();
        this.existingAccounts = accountsTask.waitForResult();
        TaskWithReturn<List<Type>> typeTask = DbController.INSTANCE.getAllTypes();
        typeTask.RegisterFailureEvent((e) -> printStackTrace(e));

        typeTask.RegisterSuccessEvent((list) -> {
            this.typeText.setItems(FXCollections.observableArrayList(list));
            this.typeText.setConverter(new TypeStringConverter());
        });
        typeTask.startTask();
        this.existingTypes = typeTask.waitForResult();
        this.typeText.setEditable(true);
        this.addTrnxnSubmitButton.setOnAction((event) -> {
            Transaction transaction = getTransactionSubmission();
            if(!(transaction==null)) {
                TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(transaction);
                task.RegisterSuccessEvent(() -> closeWindow());
                task.RegisterFailureEvent((e) -> printStackTrace(e));

                task.startTask();
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

            LocalDate localDate = this.datePicker.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            this.date = Date.from(instant);

            this.pending = this.pendingCheckBox.isSelected();

            this.payee = fromBoxPayee(this.payeeText.getValue());
            if (InputSanitization.isInvalidPayee(this.payee)){
                this.setupErrorPopup("Invalid Payee entry.", new Exception());
                return null;
            }
            if (this.accountText.getSelectionModel().isEmpty()){
                this.setupErrorPopup("No account selected.", new Exception());

                return null;
            }
            this.account = this.accountText.getValue();

            this.category = new ArrayList<Tag>() {{
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
            this.amount = (int) Math.round(amountToSetDecimal * 100);

            this.notes = new Note(this.notesText.getText());

            if( this.typeText.getSelectionModel().isEmpty()) {
                this.setupErrorPopup("No type selected.", new Exception());
                return null;
            }
            this.type = this.typeText.getValue();

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

    private List<String> toStringListPayee(List<Payee> payees) {
        List<String> listy = new ArrayList<>();
        for (Payee payee : payees
                ) {
            listy.add(payee.getName());
        }
        return listy;
    }
}
