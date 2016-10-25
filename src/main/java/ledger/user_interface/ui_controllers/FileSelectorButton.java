package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by CJ on 10/24/2016.
 */
public class FileSelectorButton extends Button {

    private File file;

    public FileSelectorButton() {
        super();
        this.setText("Select File");

        this.setOnAction(this::OnAction);

    }

    private void OnAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(this.getScene().getWindow());
        if(selectedFile != null){
            this.file = selectedFile;
            this.setText(selectedFile.getName());
        }
    }

    public File getFile() {
        return file;
    }
}
