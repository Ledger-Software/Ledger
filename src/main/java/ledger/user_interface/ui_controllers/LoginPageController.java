package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

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
