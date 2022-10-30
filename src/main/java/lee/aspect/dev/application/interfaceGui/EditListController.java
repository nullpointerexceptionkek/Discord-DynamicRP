package lee.aspect.dev.application.interfaceGui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lee.aspect.dev.application.CustomDiscordRPC;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditListController extends ConfigController implements Initializable {

    Stage stage;
    @FXML
    private TextField Wait;
    @FXML
    private TextField image;
    @FXML
    private TextField imagetext;
    @FXML
    private TextField smallimage;
    @FXML
    private TextField smalltext;
    @FXML
    private TextField firstline;
    @FXML
    private TextField secondline;
    @FXML
    private TextField button1Text;
    @FXML
    private TextField button1Url;
    @FXML
    private TextField button2Text;
    @FXML
    private TextField button2Url;
    @FXML
    private Button CancelButton;
    @FXML
    private Button SaveButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private AnchorPane anchorPane;

    private ImageView delayTooSmall;

    private int numberInList = -1;

    public void cancelSaves() throws IOException {
        stage = (Stage) anchorPane.getScene().getWindow();
        gobacktoConfig();
    }

    public void saveChanges() throws IOException {
        Script.setUpdates(numberInList, new Updates(Long.parseLong(Wait.getText()), image.getText(), imagetext.getText(), smallimage.getText(),
                smalltext.getText(), firstline.getText(), secondline.getText(), button1Text.getText(),
                button1Url.getText(), button2Text.getText(), button2Url.getText()));
        gobacktoConfig();
    }

    public void deleteThisItem() throws IOException {
        Script.getTotalupdates().remove(numberInList);
        gobacktoConfig();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Wait.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                Wait.setText(newValue.replaceAll("\\D", ""));
            if(Wait.getText().isBlank() || Wait.getText().isEmpty()) return;
            if (Long.parseLong(Wait.getText()) < 16000) {
                if (!anchorPane.getChildren().contains(delayTooSmall)) {
                    delayTooSmall =
                            WarningManager.setWarning(Wait, 12, "It is recommended to set the delay above 16 second", WarningManager.Mode.Up,15,2);
                    anchorPane.getChildren().add(delayTooSmall);
                }
            } else anchorPane.getChildren().remove(delayTooSmall);


        });
    }

    public void setnumberInList(int numberInList) {
        this.numberInList = numberInList;
        Wait.setText(String.valueOf(Script.getTotalupdates().get(numberInList).getWait()));
        image.setText(Script.getTotalupdates().get(numberInList).getImage());
        imagetext.setText(Script.getTotalupdates().get(numberInList).getImagetext());
        smallimage.setText(Script.getTotalupdates().get(numberInList).getSmallimage());
        smalltext.setText(Script.getTotalupdates().get(numberInList).getSmalltext());
        firstline.setText(Script.getTotalupdates().get(numberInList).getFl());
        secondline.setText(Script.getTotalupdates().get(numberInList).getSl());
        button1Text.setText(Script.getTotalupdates().get(numberInList).getButton1Text());
        button1Url.setText(Script.getTotalupdates().get(numberInList).getButton1Url());
        button2Text.setText(Script.getTotalupdates().get(numberInList).getButton2Text());
        button2Url.setText(Script.getTotalupdates().get(numberInList).getButton2Url());

    }

    private void gobacktoConfig() throws IOException {
        stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
        CustomDiscordRPC.primaryStage.setScene(new Scene(root));
        numberInList = -1;
    }


}
