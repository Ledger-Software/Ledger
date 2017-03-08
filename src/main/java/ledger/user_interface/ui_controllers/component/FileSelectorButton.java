package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Button} for selecting a file. At first it's text is 'Select File' but
 * after Selecting a file it changes it's text the file name.
 */
public class FileSelectorButton extends Button {

    private File file;
    private final List<ExtensionFilter> fileExtensionFilters;

    public FileSelectorButton() {
        super();
        this.setText("Select File");
        this.fileExtensionFilters = new ArrayList<>();

        this.setOnAction(this::OnAction);
    }

    private void OnAction(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(this.fileExtensionFilters);
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
        this.fileExtensionFilters.add(e);
    }

    public void removeFileExtensionFilter(ExtensionFilter e) {
        this.fileExtensionFilters.remove(e);
    }

    public void clearFileExtensionFilter() {
        this.fileExtensionFilters.clear();
    }
}
