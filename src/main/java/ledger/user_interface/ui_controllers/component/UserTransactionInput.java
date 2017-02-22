package ledger.user_interface.ui_controllers.component;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.*;
import ledger.io.input.TypeConversion;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.InputSanitization;
import ledger.user_interface.utils.TypeComparator;
import ledger.user_interface.utils.TypeStringConverter;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Control that allows for user input of transactions. On calling getAccountSubmission it does sanitation and tells the
 * user what is wrong. For code it will return null.
 */
public class UserTransactionInput extends GridPane implements IUIController, Initializable {
    private static final String pageLog = "/fxml_files/UserTransactionInput.fxml";

    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox pendingCheckBox;
    @FXML
    private PayeeDropdown payeeText;
    @FXML
    private AccountDropdown accountText;
    @FXML
    private TextField categoryText;
    @FXML
    private TextField amountText;
    @FXML
    private TextArea notesText;
    @FXML
    private ComboBox<Type> typeText;
    @FXML
    private Text checkLabel;
    @FXML
    private TextField checkField;


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
        this.typeText.setDisable(true);

        ChangeListener<Type> checkListener = (observable, oldValue, newValue) -> {
            if (new TypeComparator().compare(TypeConversion.CHECK, newValue) == 0) {
                checkLabel.setVisible(true);
                checkField.setVisible(true);
            } else {
                checkField.setText("");
                checkLabel.setVisible(false);
                checkField.setVisible(false);
            }
        };

        this.notesText.setText("");
        TaskWithReturn<List<Type>> typeTask = DbController.INSTANCE.getAllTypes();
        typeTask.RegisterFailureEvent(Throwable::printStackTrace);
        typeTask.RegisterSuccessEvent((list) -> {
            this.typeText.setItems(FXCollections.observableArrayList(list));
            this.typeText.setConverter(new TypeStringConverter());
            this.typeText.setDisable(false);
            this.typeText.setEditable(true);
        });
        typeTask.startTask();
        this.datePicker.setValue(LocalDate.now());
        this.typeText.valueProperty().addListener(checkListener);
    }

    /**
     * Get the user input from the add Transaction popup and returns a new Transaction object.
     *
     * @return a new Transaction object consisting of user input
     */
    public Transaction getTransactionSubmission() {


        LocalDate localDate = this.datePicker.getValue();
        if (localDate == null) {
            this.setupErrorPopup("No Date selected");
            return null;
        }

        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);

        boolean pending = this.pendingCheckBox.isSelected();

        Payee payee = this.payeeText.getSelectedPayee();
        if (InputSanitization.isInvalidPayee(payee)) {
            this.setupErrorPopup("Invalid Payee entry.");
            return null;
        }

        Account account = this.accountText.getSelectedAccount();
        if (account == null) {
            this.setupErrorPopup("No account selected.");
            return null;
        }

        List<Tag> category = new ArrayList<Tag>() {{
            add(new Tag(categoryText.getText(), ""));
        }};

        if (InputSanitization.isInvalidAmount(this.amountText.getText())) {
            this.setupErrorPopup("Invalid amount entry.");
            return null;
        }
        String amountString = this.amountText.getText();
        if (amountString.charAt(0) == '$') {
            amountString = amountString.substring(1);
        }
        double amountToSetDecimal = Double.parseDouble(amountString);
        long amount = Math.round(amountToSetDecimal * 100);


        Note notes = new Note(this.notesText.getText());

        String checkNo = "";
        if(checkField.visibleProperty().get()){
            checkNo = checkField.getText();
            if(checkNo==null||checkNo.isEmpty()||InputSanitization.isInvalidCheckNumber(checkNo)){
                this.setupErrorPopup("Please Enter a Check Number", new Exception());
                return null;
            }
        }
        Type type = this.typeText.getValue();
        if (type == null) {
            this.setupErrorPopup("No type selected.", new Exception());
            return null;
        }

        if (checkNo.equals("")) {
            return new Transaction(date, type, amount, account,
                    payee, pending, category, notes);
        } else {
            return new Transaction(date, type, amount, account,
                    payee, pending, category, notes, Integer.parseInt(checkNo));
        }


    }

    /**
     * Sets the fields in this control to their corresponding values in the given transaction
     *
     * @param currentTrans non-null Transaction to set the data in this control too
     */
    public void setTransaction(Transaction currentTrans) {
        this.typeText.valueProperty();
        this.datePicker.setValue(currentTrans.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        this.typeText.setValue(currentTrans.getType());
        this.amountText.setText(Double.toString(currentTrans.getAmount() / 100.0));
        this.accountText.setValue(currentTrans.getAccount());
        this.payeeText.setValue(currentTrans.getPayee());
        this.pendingCheckBox.setSelected(currentTrans.isPending());

        List<Tag> tags = currentTrans.getTags();

        if (tags.size() > 0)
            this.categoryText.setText(tags.get(0).toString());
        else
            this.categoryText.setText("");

        Note note = currentTrans.getNote();
        if (note != null)
            this.notesText.setText(note.getNoteText());
        else
            this.notesText.setText("");
    }
}
