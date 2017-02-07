package ledger.user_interface.ui_controllers.window;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.IgnoredExpression;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.InputSanitization;
import ledger.user_interface.utils.MatchOrContainsStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gert on 2/7/17.
 */
public class IgnoredTransactionPopupController  extends GridPane implements Initializable, IUIController {
    private static final String pageLoc = "/fxml_files/IgnoredTransactionPopup.fxml";
    @FXML
    private TextField newExpText;
    @FXML
    private ComboBox<Boolean> newExpRule;
    @FXML
    private Button addExpButton;
    public IgnoredTransactionPopupController(){

        this.initController(pageLoc, this, "Unable to load Ignored Transaction window");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.newExpRule.setEditable(false);
        this.newExpRule.setItems(FXCollections.observableArrayList(Boolean.FALSE, Boolean.TRUE));
        this.newExpRule.setConverter(new MatchOrContainsStringConverter());
        this.addExpButton.setOnAction(event -> addExpression());

    }

    private void addExpression(){
        if(InputSanitization.isStringInvalid(newExpText.getText())){
            this.setupErrorPopup("Invalid Expression", new Exception());
        }
        String exp = newExpText.getText();
        if(this.newExpRule.getValue()==null){
            this.setupErrorPopup("Please select a rule", new Exception());
        }
        Boolean rule = newExpRule.getValue();
        TaskWithArgs<IgnoredExpression> insertIgExpTask
                = DbController.INSTANCE.insertIgnoredExpression(new IgnoredExpression(exp, rule));
        insertIgExpTask.RegisterSuccessEvent(() -> Startup.INSTANCE.runLater(() ->{
            this.newExpText.clear();
            this.newExpRule.setValue(Boolean.TRUE);
        }));
        insertIgExpTask.startTask();
        insertIgExpTask.waitForComplete();
    }
}
