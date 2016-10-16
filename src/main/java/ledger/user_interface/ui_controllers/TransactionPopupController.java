package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ledger.database.enity.*;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by richarel on 10/16/2016.
 */
public class TransactionPopupController extends GridPane implements Initializable {

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

    TransactionPopupController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/AddTransactionPopup.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on transaction popup startup: " +  e);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.addTrnxnSubmitButton.setOnAction((event) -> {
            try {
                getTransactionSubmission();
            } catch (Exception e) {
                System.out.println("Error on transaction submission: " + e);
            }
        });
    }

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
