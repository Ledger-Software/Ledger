package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ledger.database.enity.*;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Controls and manipulates the input given by the user when manually adding a transaction
 */
public class TransactionPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox clearedCheckBox;
    @FXML
    private TextField payeeText;
    @FXML
    private TextField accountText;
    @FXML
    private TextField categoryText;
    @FXML
    private TextField amountText;
    @FXML
    private TextArea notesText;
    @FXML
    private TextField typeText;
    @FXML
    private Button addTrnxnSubmitButton;

    private Date date;
    private boolean cleared;
    private Payee payee;
    private Account account;
    private List<Tag> category;
    private int amount;
    private Note notes;
    private Type type;

    private static String pageLoc = "/fxml_files/AddTransactionPopup.fxml";

    TransactionPopupController() {
        this.initController(pageLoc, this, "Error on transaction popup startup: ");
    }

    /**
     * Sets up action listener for the button on the page
     *
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.addTrnxnSubmitButton.setOnAction((event) -> {
            try {
                getTransactionSubmission();
            } catch (Exception e) {
                this.setupErrorPopup("Error on transaction submission: " + e);
            }
            this.getScene().getWindow().hide();
        });
    }

    /**
     * Get the user input from the add Transaction popup and returns a new Transaction object.
     *
     * @return a new Transaction object consisting of user input
     */
    public Transaction getTransactionSubmission () {
        LocalDate localDate = this.datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        this.date = Date.from(instant);

        this.cleared = this.clearedCheckBox.isSelected();

        // TODO: make this a dropdown
        this.payee = new Payee(this.payeeText.getText(), "");

        // TODO: make this a dropdown
        this.account = new Account(this.accountText.getText(), "");

        this.category = new ArrayList<Tag>() {{
            add(new Tag(categoryText.getText(), ""));
        }};

        this.amount = Integer.parseInt(this.amountText.getText());

        this.notes = new Note(this.notesText.getText());

        // TODO: make this a dropdown
        this.type = new Type(this.typeText.getText(), "");

        Transaction t = new Transaction(this.date, this.type, this.amount, this.account,
                this.payee, this.cleared, this.category, this.notes);

        return t;
    }
}
