package gr.alx;

import gr.alx.release.manager.FXReleaseManager;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * Created by TRIFYLLA on 14/10/2016.
 */
@Slf4j
public class FxReleaseApplication extends Application {


    /**
     * Main method that loads the java fx application.
     *
     * @param args user run arguments
     */
    public static void main(String[] args) {
        log.info("---------------------------------------------------------------------------------");
        log.info("Java fx manager application started");
        log.info("---------------------------------------------------------------------------------");
        launch(args);
        log.info("---------------------------------------------------------------------------------");
        log.info("Java fx manager application ended");
        log.info("---------------------------------------------------------------------------------");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        URL resource = getClass().getClassLoader().getResource("fxApp.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent root = (Parent) fxmlLoader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Release Manager");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
        primaryStage.setResizable(false);

        createManagerAndPassToController(fxmlLoader, scene);
        notifyPreloader(new Preloader.StateChangeNotification(
                Preloader.StateChangeNotification.Type.BEFORE_START));

        primaryStage.show();
    }

    private void createManagerAndPassToController(FXMLLoader fxmlLoader, Scene scene) {
        ScrollPane lookup = (ScrollPane) scene.lookup("#scrollPane");
        FXReleaseController fxReleaseController = fxmlLoader.<FXReleaseController>getController();
        fxReleaseController.setReleaseManager(new FXReleaseManager((Label) lookup.getContent().lookup("#outputText")));
    }
}
