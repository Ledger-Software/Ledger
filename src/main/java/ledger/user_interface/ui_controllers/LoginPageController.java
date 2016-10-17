package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;

/**
 * Created by richarel on 10/16/2016.
 */
public class LoginPageController extends GridPane implements Initializable {

    @FXML
    private Button chooseFileBtn;
    @FXML
    private TextField password;
    @FXML
    private Button loginBtn;

    private String pwd;
    private File file;

    LoginPageController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/LoginPage.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on login startup: " +  e);
        }
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.loginBtn.setOnAction((event) -> {
            try {
                MainPageController mainPageController = new MainPageController();
                Scene scene = new Scene(mainPageController);
                //Stage newStage = new Stage();
                Stage newStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on login submission: " + e);
            }
        });
    this.chooseFileBtn.setOnAction((event -> selectFile()));
    }

    private void selectFile() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if(selectedFile != null){
            this.file = selectedFile;
            chooseFileBtn.setText(selectedFile.getName());
        }
    }
}
