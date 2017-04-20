package ledger.user_interface.ui_controllers.window;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Type;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AccountDropdown;
import ledger.user_interface.ui_controllers.component.PayeeDropdown;
import ledger.user_interface.utils.TypeStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Add Recurring Transaction Window.
 */
public class RecurringBillPaymentWindow extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/RecurringBillPaymentWindow.fxml";

    @FXML
    DatePicker startDatePicker;
    @FXML
    DatePicker endDatePicker;
    @FXML
    PayeeDropdown payeeDropdown;
    @FXML
    TextField amountField;
    @FXML
    AccountDropdown accountDropdown;
    @FXML
    ChoiceBox<Type> typeText;
    @FXML
    TextField tagField;
    @FXML
    ChoiceBox<String> frequencyText;
    @FXML
    TextArea notesField;
    @FXML
    Button addButton;

    public RecurringBillPaymentWindow() {
        this.initController(pageLoc, this, "Unable to load recurring bill payment window");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.frequencyText.getItems().addAll(new ArrayList<String>(){{
            add("Daily");
            add("Weekly");
            add("Monthly");
            add("Yearly");
        }});
        this.frequencyText.setVisible(true);

        this.typeText.setDisable(true);
        TaskWithReturn<List<Type>> typeTask = DbController.INSTANCE.getAllTypes();
        typeTask.RegisterFailureEvent(Throwable::printStackTrace);
        typeTask.RegisterSuccessEvent((list) -> {
            this.typeText.setItems(FXCollections.observableArrayList(list));
            this.typeText.setConverter(new TypeStringConverter());
            this.typeText.setDisable(false);
        });
        typeTask.startTask();
    }
}