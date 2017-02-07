package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJ on 10/24/2016.
 */
public class FileSelectorButton extends Button {

    private File file;
    private List<ExtensionFilter> fileExtentionFilters;

    public FileSelectorButton() {
        super();
        this.setText("Select File");
        this.fileExtentionFilters = new ArrayList<>();

        this.setOnAction(this::OnAction);
    }

    private void OnAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(this.fileExtentionFilters);
        File selectedFile = chooser.showOpenDialog(this.getScene().getWindow());
        if (selectedFile != null) {
            this.file = selectedFile;
            this.setText(selectedFile.getName());
        }
    }

    public File getFile() {
        return file;
    }

    public void addFileExtensionFilter(ExtensionFilter e) {
        this.fileExtentionFilters.add(e);
    }

    public void removeFileExtensionFilter(ExtensionFilter e) {
        this.fileExtentionFilters.remove(e);
    }

    public void clearFileExtensionFilter() {
        this.fileExtentionFilters.clear();
    }
}
