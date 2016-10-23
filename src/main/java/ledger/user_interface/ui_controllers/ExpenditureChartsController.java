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
 * Controls how the charts render with user given information.
 */
public class ExpenditureChartsController extends GridPane implements Initializable, IUIController {

    @FXML
    private PieChart expenditurePieChart;
    @FXML
    private StackedBarChart expenditureBarChart;

    private static String pageLoc = "/fxml_files/ExpenditureCharts.fxml";

    ExpenditureChartsController() {
        this.initController(pageLoc, this, "Error on expenditure chart page startup: ");
    }

    /**
     * Will be used to set up the charts on this page
     *
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    }
}
