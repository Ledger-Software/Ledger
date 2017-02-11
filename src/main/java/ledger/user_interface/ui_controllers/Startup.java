package ledger.user_interface.ui_controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.exception.StorageException;
import ledger.updater.GitHubChecker;
import ledger.updater.OldVersionFinder;
import ledger.user_interface.ui_controllers.window.LoginPageController;
import ledger.user_interface.ui_controllers.window.OldVersionArchiver;
import ledger.user_interface.ui_controllers.window.UpdateConfirmation;

import java.io.File;

/**
 * Handles any tasks relevant for init of the Program.
 */
public class Startup extends Application {

    public static Startup INSTANCE;
    private static Image image;
    private Stage stage;

    public Startup() {
        INSTANCE = this;
        image = new Image("/images/Letter-T-blue-icon.png");
    }

    /**
     * Main entry point into the GUI for Ledger Software.
     *
     * @param args No args are used
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Kicks off the application flow
     *
     * @param primaryStage The primary staged used to run the app
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> shutdown());
        this.stage = primaryStage;
        this.stage.getIcons().add(image);

        checkForUpdates();
        checkForOldVersions();

        createLoginPage();
    }

    private void shutdown() {
        try {
            DbController.INSTANCE.shutdown();
        } catch (StorageException e) {
        }

        Platform.exit();
    }

    /**
     * Creates the login page to be the first thing seen in the application
     */
    public void createLoginPage() {
        LoginPageController loginController = new LoginPageController();
        Scene scene = new Scene(loginController);
        switchScene(scene, "Ledger Login");
    }

    private void checkForUpdates() {
        GitHubChecker checker = new GitHubChecker();
        if (checker.isUpdateAvaliable()) {
            GitHubChecker.Release release = checker.getNewerRelease();
            if (release.getDownloadURL() != null) {
                UpdateConfirmation uc = new UpdateConfirmation(release);
                Scene scene = new Scene(uc);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setResizable(false);
                newStage.setTitle("Update");
                newStage.initModality(Modality.WINDOW_MODAL);
                newStage.getIcons().addAll(image);
                newStage.showAndWait();
            }
        }
    }

    private void checkForOldVersions() {
        OldVersionFinder finder = new OldVersionFinder();
        File[] files = finder.oldVersions();

        for (File f : files) {
            OldVersionArchiver a = new OldVersionArchiver(f);
            Scene scene = new Scene(a);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setResizable(false);
            newStage.setTitle("Archiver");
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.showAndWait();
        }
    }

    public void switchScene(Scene scene, String title) {
        this.stage.setScene(scene);
        this.stage.setTitle(title);
        this.stage.getIcons().addAll(image);
        this.stage.show();
        this.stage.setMinHeight(stage.getHeight());
        this.stage.setMinWidth(stage.getWidth());
    }

    public void newStage(Scene scene, String title) {
        this.stage.close();
        this.stage = new Stage();
        this.stage.getIcons().add(image);
        this.switchScene(scene, title);
    }

    public void runLater(CallableMethodVoidNoArgs method) {
        Platform.runLater(() -> {
            try {
                method.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}