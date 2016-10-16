package gr.alx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by TRIFYLLA on 14/10/2016.
 */
public class FxReleaseApplication extends Application {


    /**
     * Main method that loads the java fx application.
     *
     * @param args user run arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        URL resource = getClass().getClassLoader().getResource("fxApp.fxml");
        Parent root = FXMLLoader.load(resource);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Release Manager");
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
