package gr.alx;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by TRIFYLLA on 17/10/2016.
 */
public class SplashScreenLoader extends Preloader {

    private Stage splashScreen;

    @Override
    public void start(Stage stage) throws Exception {
        splashScreen = stage;
        splashScreen.setScene(createScene());
        splashScreen.show();
    }

    public Scene createScene() {
        ImageView imageView  = new ImageView(new Image("loading.png"));
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root, 200, 100);
        return scene;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof Preloader.StateChangeNotification) {
            splashScreen.hide();
        }
    }

}
