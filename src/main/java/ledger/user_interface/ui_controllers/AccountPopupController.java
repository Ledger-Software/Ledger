package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ledger.database.enity.Account;
import ledger.database.enity.AccountBalance;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by richarel on 10/16/2016.
 */
public class AccountPopupController extends GridPane implements Initializable {

    @FXML
    private Button submitAccountInfo;
    @FXML
    private TextField accountAmtText;
    @FXML
    private TextField accountDescription;
    @FXML
    private TextField accountNameText;

    private Account act = null;


    AccountPopupController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/AddAccountPopup.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on account popup startup: " + e);
        }
    }

    /**
     * Sets up action listener for the button on the page
     *
     * @param fxmlFileLocation
     * @param resources
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.submitAccountInfo.setOnAction((event) -> {
            try {
                getAccountSubmission();
                getAccountBalance();
            } catch (Exception e) {
                System.out.println("Error on account submission: " + e);
            }
        });
    }

    /**
     * Takes the user input and creates a new Account object
     *
     * @return a new Account object
     */
    public Account getAccountSubmission() {
        if (act == null) {
            this.act = new Account(accountNameText.getText(), accountDescription.getText());
        }
        return this.act;
    }

    /**
     * Takes the user input and creates a new AccountBalance object
     *
     * @return a new AccountBalance object
     */
    public AccountBalance getAccountBalance() {
        return new AccountBalance(this.act, new Date(), Integer.parseInt(accountAmtText.getText()));
    }
}
