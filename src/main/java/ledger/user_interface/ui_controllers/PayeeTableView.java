package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.IdenityCellValueCallback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/22/2017.
 */
public class PayeeTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/PayeeTableView.fxml";

    @FXML
    public TableColumn<Payee,String> nameColumn;
    @FXML
    public TableColumn<Payee,String> descriptionColumn;
    @FXML
    public TableColumn<Payee, Payee> tagColumn;


    public PayeeTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskWithReturn<List<Payee>> task = DbController.INSTANCE.getAllPayees();
        task.startTask();

        nameColumn.setCellValueFactory(new PropertyValueFactory<Payee, String>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Payee, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Payee, String> event) {
                String name = event.getNewValue();
                Payee payee = event.getRowValue();
                payee.setName(name);

                TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
                task.startTask();
                task.waitForComplete();
            }
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Payee, String>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Payee, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Payee, String> event) {
                String description = event.getNewValue();
                Payee payee = event.getRowValue();
                payee.setDescription(description);

                TaskWithArgs task = DbController.INSTANCE.editPayee(payee);
                task.startTask();
                task.waitForComplete();
            }
        });

        tagColumn.setCellValueFactory(new IdenityCellValueCallback<>());
        tagColumn.setCellFactory(new Callback<TableColumn<Payee, Payee> , TableCell<Payee, Payee> >() {
            @Override
            public TableCell<Payee, Payee>  call(TableColumn<Payee, Payee> param) {
                return new TableCell<Payee, Payee>() {
                    @Override
                    protected void updateItem(Payee model, boolean empty) {
                        super.updateItem(model, empty);

                        if(model == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            TagFlowPane flow = new TagFlowPane(model);
                            setGraphic(flow);
                        }
                    }
                };
            }
        });
        tagColumn.setEditable(false);


        List<Payee> payees = task.waitForResult();
        this.setItems(FXCollections.observableList(payees));
    }
}
