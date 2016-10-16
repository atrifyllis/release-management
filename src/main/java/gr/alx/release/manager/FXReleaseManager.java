package gr.alx.release.manager;

import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

/**
 * Central point of the application which manages the whole release process using Java FX.
 * Created by alx on 10/2/2016.
 */
@Slf4j
public class FXReleaseManager extends CommonReleaseManager {

    private Label console;

    /**
     * Initialisation constructor which initialise all dependent classes.
     */
    public FXReleaseManager(Label console) {
        this.console = console;
        initialiseManager();
    }

    @Override
    protected void printInConsole(String writeMessage) {

        String previousText = console.getText();
        String newText = previousText + "\n" + writeMessage;
        console.setText(newText);

    }
}

