package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by richarel on 10/16/2016.
 */
public class MainPageController extends GridPane implements Initializable {
    @FXML
    private Button addAccountBtn;
    @FXML
    private Button importTransactionsBtn;
    @FXML
    private Button trackSpendingBtn;


    MainPageController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/MainPage.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on main page startup: " + e);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.addAccountBtn.setOnAction((event) -> {
            try {
                AccountPopupController accountController = new AccountPopupController();
                Scene scene = new Scene(accountController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering add account screen: " + e);
            }
        });

        this.importTransactionsBtn.setOnAction((event) -> {
            try {
                TransactionPopupController trxnController = new TransactionPopupController();
                Scene scene = new Scene(trxnController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering add transaction screen: " + e);
            }
        });

        this.trackSpendingBtn.setOnAction((event) -> {
            try {
                ExpenditureChartsController chartController = new ExpenditureChartsController();
                Scene scene = new Scene(chartController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering expenditure charts screen: " + e);
            }
        });
    }
}
