package ledger.user_interface.ui_controllers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.exception.StorageException;
import ledger.updater.GitHubChecker;
import ledger.updater.OldVersionFinder;

import java.io.File;

/**
 * Handles any tasks relevant for init of the Program.
 */
public class Startup extends Application {

    public static Startup INSTANCE;

    private Stage stage;

    public Startup() {
        INSTANCE = this;
    }

    /**
     * Main entry point into the GUI for Ledger Software.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Kicks off the application flow
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> shutdown());
        this.stage = primaryStage;

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
            if(release.getDownloadURL() != null) {
                UpdateConfirmation uc = new UpdateConfirmation(release);
                Scene scene = new Scene(uc);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setResizable(false);
                newStage.setTitle("Update");
                newStage.initModality(Modality.WINDOW_MODAL);
                newStage.showAndWait();
            }
        }
    }

    private void checkForOldVersions() {
        OldVersionFinder finder = new OldVersionFinder();
        File[] files = finder.oldVersions();

        for(File f: files) {
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
        this.stage.show();
        this.stage.setMinHeight(stage.getHeight());
        this.stage.setMinWidth(stage.getWidth());
    }

    public void newStage(Scene scene, String title) {
        this.stage.close();
        this.stage = new Stage();
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