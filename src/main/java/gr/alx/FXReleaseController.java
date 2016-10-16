package gr.alx;

import gr.alx.release.manager.FXReleaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main java fx controller class.
 * <p>
 * Created by alx on 10/13/2016.
 */
public class FXReleaseController implements Initializable {

    @FXML
    private TextField commandTxt;

    @FXML
    private Label outputText;
    private FXReleaseManager rm;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane outputPane;

    /**
     * Initializes application by creating a {@link FXReleaseManager}
     * passing the java fx label as a 'console'.
     * Any other initialisation can be done here.
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rm = new FXReleaseManager(outputText);
        setScrollPaneToScrollToBottom();
    }

    /**
     * Action for 'Execute' button click.
     *
     */
    @FXML
    public void doRelease() {
        release();
    }

    /**
     * Action for 'command' input text when pressing Enter key.
     *
     */
    @FXML
    public void onEnter() {
        release();
    }

    /**
     * Action for 'Clear' button click.
     *
     */
    public void doClear() {
        outputText.setText("");
    }

    /**
     * This method:
     * 1. Resets the 'console'.
     * 2. Executes the release action.
     * 3. Outputs the outcome in the 'console'
     */
    private void release() {
        outputText.setText("");
        String command = commandTxt.getText();
        rm.doRelease(command);
        commandTxt.setText("");

    }

    /**
     * Makes the scroll pane that contains the 'console'
     * to always scroll to bottom.
     */
    private void setScrollPaneToScrollToBottom() {
        scrollPane.vvalueProperty().bind(outputPane.heightProperty());
    }

    public void doOptionsRelease() {

    }
}
