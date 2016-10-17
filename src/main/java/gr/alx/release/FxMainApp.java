package gr.alx.release;

import com.sun.javafx.application.LauncherImpl;
import gr.alx.FxReleaseApplication;
import gr.alx.SplashScreenLoader;

/**
 * Created by TRIFYLLA on 17/10/2016.
 */
public class FxMainApp {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(FxReleaseApplication.class, SplashScreenLoader.class, args);
    }
}
