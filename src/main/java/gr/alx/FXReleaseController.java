package gr.alx;

import gr.alx.release.manager.FXReleaseManager;
import gr.alx.release.manager.Version;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main java fx controller class.
 * <p>
 * Created by alx on 10/13/2016.
 */
@Slf4j
public class FXReleaseController implements Initializable {

    @FXML
    private TextField commandTxt;

    /**
     * Release tab elements
     */
    @FXML
    Spinner<Integer> majorSpinner;
    @FXML
    Spinner<Integer> minorSpinner;
    @FXML
    Spinner<Integer> buildSpinner;
    @FXML
    CheckBox snapshotCheckBox;

    /**
     * Bump tabs elements
     */
    @FXML
    RadioButton majorRadio;
    @FXML
    RadioButton minorRadio;
    @FXML
    RadioButton buildRadio;
    @FXML
    RadioButton prodRadio;
    @FXML
    RadioButton snapshotRadio;
    @FXML
    ToggleGroup actionGroup;


    @FXML
    private Label outputText;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane outputPane;

    private FXReleaseManager rm;

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

        majorSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            commitEditorText(majorSpinner);
        });
        minorSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            commitEditorText(minorSpinner);
        });
        buildSpinner.focusedProperty().addListener((s, ov, nv) -> {
            if (nv) return;
            commitEditorText(buildSpinner);
        });

        updateSpinners();
    }


    /**
     * Action for 'Execute' button click.
     */
    @FXML
    public void doRelease() {
        release();
    }

    /**
     * Action for 'command' input text when pressing Enter key.
     */
    @FXML
    public void onEnter() {
        release();
    }

    /**
     * Action for 'Clear' button click.
     */
    public void doClear() {
        outputText.setText("");
    }

    /**
     * This method:
     * 1. Resets the 'console'.
     * 2. Executes the release action.
     * 3. Outputs the outcome in the 'console'
     * 4. Updates spinner values
     */
    private void release() {
        String command = commandTxt.getText();
        executeCommonReleaseActions(command);
        commandTxt.setText("");
    }


    /**
     * Makes the scroll pane that contains the 'console'
     * to always scroll to bottom.
     */
    private void setScrollPaneToScrollToBottom() {
        scrollPane.vvalueProperty().bind(outputPane.heightProperty());
    }

    public void doManualRelease() {

        String command = "release " +
                majorSpinner.getValue() + "." +
                minorSpinner.getValue() + "." +
                buildSpinner.getValue() +
                (snapshotCheckBox.isSelected() ? "-SNAPSHOT" : "");
        executeCommonReleaseActions(command);
    }

    public void doAutomaticRelease() {
        RadioButton selectedToggle = (RadioButton) actionGroup.getSelectedToggle();
        String command = "bump " + selectedToggle.getText().toLowerCase();
        executeCommonReleaseActions(command);
    }

    private void executeCommonReleaseActions(String command) {
        outputText.setText("");
        rm.doRelease(command);
        updateSpinners();
        showAlert();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Version Updated");
        alert.setContentText("The version has been update to " + rm.retrievePomVersion());
        alert.showAndWait();
    }

    private void updateSpinners() {
        Version currentVersion = rm.retrievePomVersion();

        majorSpinner.getValueFactory().setValue(currentVersion.getMajor());
        minorSpinner.getValueFactory().setValue(currentVersion.getMinor());
        buildSpinner.getValueFactory().setValue(currentVersion.getBuild());
        snapshotCheckBox.setSelected(currentVersion.isSnapshot());
    }

    /**
     * This method is used in the listener so that the spinners update the value on focus-out
     */
    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }
}
