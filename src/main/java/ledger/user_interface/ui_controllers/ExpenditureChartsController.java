package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by richarel on 10/16/2016.
 */
public class ExpenditureChartsController extends GridPane implements Initializable {

    @FXML
    private PieChart expenditurePieChart;
    @FXML
    private StackedBarChart expenditureBarChart;

    ExpenditureChartsController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/ExpenditureCharts.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on expenditure chart page startup: " +  e);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    }
}
