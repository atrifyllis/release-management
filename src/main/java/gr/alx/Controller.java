package gr.alx;

import gr.alx.release.FXReleaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class Controller {

    @FXML
    private TextField commandTxt;

    @FXML
    private Label outputText;
    private FXReleaseManager rm;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Pane outputPane;

    @FXML
    public void initialize() {
        this.rm = new FXReleaseManager(outputText);
        setScrollPaneToScrollToBottom();
    }

    @FXML
    public void doRelease(ActionEvent actionEvent) {
        release();
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) {
        release();
    }

    public void doClear(ActionEvent actionEvent) {
        outputText.setText("");
    }

    private void release() {
        outputText.setText("");
        String command = commandTxt.getText();
        rm.doRelease(command);
        commandTxt.setText("");

    }

    private void setScrollPaneToScrollToBottom() {
        scrollPane.vvalueProperty().bind(outputPane.heightProperty());
    }
}
