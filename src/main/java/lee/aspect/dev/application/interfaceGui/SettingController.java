package lee.aspect.dev.application.interfaceGui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lee.aspect.dev.sysUtil.exceptions.FileNotAJarException;
import lee.aspect.dev.sysUtil.RestartApplication;
import lee.aspect.dev.sysUtil.StartLaunch;
import lee.aspect.dev.animationengine.animation.SlideOutLeft;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML
    private Button goBack;

    @FXML
    private ChoiceBox<String> themeChoiceBox;

    @FXML
    private ChoiceBox<Settings.MinimizeMode> minimizeModeChoiceBox;

    @FXML
    private CheckBox shutDownInterfaceCheckBox;

    @FXML
    private CheckBox noAnimationCheckBox;

    @FXML
    private CheckBox startTrayOnlyCloseCheckBox;

    @FXML
    private CheckBox startLaunchCheckBox;

    @FXML
    private AnchorPane settingsAnchorPane;

    @FXML
    private StackPane stackPane;

    public void switchBack() throws IOException {
        goBack.setDisable(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
        stackPane.getChildren().add(0, root);
        var animation = new SlideOutLeft(settingsAnchorPane);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(settingsAnchorPane));
        animation.play();

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        goBack.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/icon/back.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        goBack.setGraphic(imageView);
        //add theme to theme choice box
        EnumSet.allOf(Settings.Theme.class).forEach((theme) -> themeChoiceBox.getItems().add(theme.getDisplayName()));
        themeChoiceBox.setValue((Settings.getTheme().getDisplayName()));
        themeChoiceBox.setOnAction((event) -> EnumSet.allOf(Settings.Theme.class).forEach((theme) -> {
            if (themeChoiceBox.getValue().equals(theme.getDisplayName())) {
                Settings.setTheme(theme);
                Parent root = settingsAnchorPane.getParent();
                root.getStylesheets().removeAll();
                root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
                applyChange();
            }
        }));
        //add min to min choice box
        minimizeModeChoiceBox.setDisable(!SystemTray.isSupported());
        for(Settings.MinimizeMode mode : Settings.MinimizeMode.values()){
            if(mode != Settings.MinimizeMode.WaitAndSee)
                minimizeModeChoiceBox.getItems().add(mode);
        }
        minimizeModeChoiceBox.setValue((Settings.getMinimizeMode()));
        minimizeModeChoiceBox.setOnAction((event) -> Settings.setMinimizeMode(minimizeModeChoiceBox.getValue()));
        //add for booleans
        shutDownInterfaceCheckBox.setDisable(!SystemTray.isSupported());
        shutDownInterfaceCheckBox.setSelected(Settings.isShutDownInterfaceWhenTray());
        shutDownInterfaceCheckBox.setOnAction((actionEvent -> Settings.setShutDownInterfaceWhenTray(shutDownInterfaceCheckBox.isSelected())));

        noAnimationCheckBox.setSelected(Settings.isNoAnimation());
        noAnimationCheckBox.setOnAction((actionEvent -> Settings.setNoAnimation(noAnimationCheckBox.isSelected())));

        startTrayOnlyCloseCheckBox.setSelected(!SystemTray.isSupported());
        startTrayOnlyCloseCheckBox.setSelected(Settings.isStartTrayOnlyInterfaceClose());
        startTrayOnlyCloseCheckBox.setOnAction((actionEvent -> {
            Settings.setStartTrayOnlyInterfaceClose(startTrayOnlyCloseCheckBox.isSelected());
            mustRestart();
        }));
        startLaunchCheckBox.setDisable(!StartLaunch.isOnWindows());
        startLaunchCheckBox.setSelected(Settings.isStartLaunch());
        startLaunchCheckBox.setOnAction((actionEvent -> {
            if (startLaunchCheckBox.isSelected()) {
                try {
                    StartLaunch.CreateBat();
                } catch (Exception e) {
                    var alertException = new Alert(Alert.AlertType.ERROR);
                    alertException.setTitle("Exception");
                    alertException.setHeaderText("We have encounter an exception");
                    alertException.setContentText("Start up Launch cannot be created");
                    alertException.show();
                    startLaunchCheckBox.setSelected(false);
                }
            } else {
                StartLaunch.deleteBat();
            }
            Settings.setStartLaunch(startLaunchCheckBox.isSelected());
        }));


        SettingManager.saveSettingToFile();

    }

    private void applyChange() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        ButtonType yesButton = new ButtonType("Yes restart now", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);
        alert.setTitle("Apply changes");
        alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
        alert.setContentText("would you like to restart now?");
        ButtonType result = alert.showAndWait().get();
        if (result.equals(yesButton)) {
            try {
                RestartApplication.FullRestart();
            } catch (URISyntaxException | IOException | FileNotAJarException e) {
                var alertException = new Alert(Alert.AlertType.ERROR);
                alertException.setTitle("Exception");
                alertException.setHeaderText("We have encounter an exception");
                alertException.setContentText("CannotRestartException");
                alertException.show();
            }
        }

    }

    private void mustRestart(){
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Apply changes");
        alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
        alert.setContentText("restart now?");
        ButtonType result = alert.showAndWait().get();
        if (result.equals(ButtonType.OK)) {
            try {
                RestartApplication.FullRestart();
            } catch (URISyntaxException | IOException | FileNotAJarException e) {
                var alertException = new Alert(Alert.AlertType.ERROR);
                alertException.setTitle("Exception");
                alertException.setHeaderText("Cannot restart");
                alertException.setContentText("The application will be force closed");
                alertException.showAndWait();
                System.exit(-1);
            }
        }
    }
}
