/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.application.interfaceGui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lee.aspect.dev.Launch;
import lee.aspect.dev.language.LanguageManager;
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
    private Button goBack,ChangeLanguageButton;

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

    @FXML
    private Label SettingsLabel, ThemeLabel, titleLabel, ReleaseLabel, MinToTrayLabel, PerformanceLabel,LanguageLabel;

    public void switchBack() throws IOException {
        goBack.setDisable(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml"));
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
        stackPane.getChildren().add(0, root);
        SlideOutLeft animation = new SlideOutLeft(settingsAnchorPane);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(settingsAnchorPane));
        animation.play();

    }

    public void changeLanguage(){
        LanguageManager.showDialog();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //set the languages to current languages
        LanguageLabel.setText(LanguageManager.getLang().getString("Language"));
        SettingsLabel.setText(LanguageManager.getLang().getString("Settings"));
        ThemeLabel.setText(LanguageManager.getLang().getString("Theme"));
        titleLabel.setText("CDiscordRPC");
        ReleaseLabel.setText("Release " + Launch.VERSION);
        MinToTrayLabel.setText(LanguageManager.getLang().getString("MinToTray"));
        PerformanceLabel.setText(LanguageManager.getLang().getString("Performance"));
        //checkbox
        shutDownInterfaceCheckBox.setText(LanguageManager.getLang().getString("ShutDownInterface"));
        noAnimationCheckBox.setText(LanguageManager.getLang().getString("NoAnimation"));
        startTrayOnlyCloseCheckBox.setText(LanguageManager.getLang().getString("StartTrayOnlyClose"));
        startLaunchCheckBox.setText(LanguageManager.getLang().getString("StartLaunch"));
        ChangeLanguageButton.setText(LanguageManager.getLang().getString("ChangeLanguage"));




        goBack.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/icon/back.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        goBack.setGraphic(imageView);
        //add theme to theme choice box
        EnumSet.allOf(Settings.Theme.class).forEach((theme) -> themeChoiceBox.getItems().add(theme.getDisplayName()));
        themeChoiceBox.setValue((SettingManager.SETTINGS.getTheme().getDisplayName()));
        themeChoiceBox.setOnAction((event) -> EnumSet.allOf(Settings.Theme.class).forEach((theme) -> {
            if (themeChoiceBox.getValue().equals(theme.getDisplayName())) {
                SettingManager.SETTINGS.setTheme(theme);
                Parent root = settingsAnchorPane.getParent();
                root.getStylesheets().removeAll();
                root.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
                applyChange();
            }
        }));
        //add min to min choice box
        minimizeModeChoiceBox.setDisable(!SystemTray.isSupported());
        for(Settings.MinimizeMode mode : Settings.MinimizeMode.values()){
            if(mode != Settings.MinimizeMode.WaitAndSee)
                minimizeModeChoiceBox.getItems().add(mode);
        }
        minimizeModeChoiceBox.setValue((SettingManager.SETTINGS.getMinimizeMode()));
        minimizeModeChoiceBox.setOnAction((event) -> SettingManager.SETTINGS.setMinimizeMode(minimizeModeChoiceBox.getValue()));
        //add for booleans
        shutDownInterfaceCheckBox.setDisable(!SystemTray.isSupported());
        shutDownInterfaceCheckBox.setSelected(SettingManager.SETTINGS.isShutDownInterfaceWhenTray());
        shutDownInterfaceCheckBox.setOnAction((actionEvent -> SettingManager.SETTINGS.setShutDownInterfaceWhenTray(shutDownInterfaceCheckBox.isSelected())));

        noAnimationCheckBox.setSelected(SettingManager.SETTINGS.isNoAnimation());
        noAnimationCheckBox.setOnAction((actionEvent -> SettingManager.SETTINGS.setNoAnimation(noAnimationCheckBox.isSelected())));

        startTrayOnlyCloseCheckBox.setSelected(!SystemTray.isSupported());
        startTrayOnlyCloseCheckBox.setSelected(SettingManager.SETTINGS.isStartTrayOnlyInterfaceClose());
        startTrayOnlyCloseCheckBox.setOnAction((actionEvent -> {
            SettingManager.SETTINGS.setStartTrayOnlyInterfaceClose(startTrayOnlyCloseCheckBox.isSelected());
            mustRestart();
        }));
        startLaunchCheckBox.setDisable(!StartLaunch.isOnWindows());
        startLaunchCheckBox.setSelected(SettingManager.SETTINGS.isStartLaunch());
        startLaunchCheckBox.setOnAction((actionEvent -> {
            if (startLaunchCheckBox.isSelected()) {
                try {
                    StartLaunch.CreateBat();
                } catch (Exception e) {
                    Alert alertException = new Alert(Alert.AlertType.ERROR);
                    alertException.setTitle("Exception");
                    alertException.setHeaderText("We have encounter an exception");
                    alertException.setContentText("Start up Launch cannot be created");
                    alertException.show();
                    startLaunchCheckBox.setSelected(false);
                }
            } else {
                StartLaunch.deleteBat();
            }
            SettingManager.SETTINGS.setStartLaunch(startLaunchCheckBox.isSelected());
        }));


        SettingManager.saveSettingToFile();

    }

    private void applyChange() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
                Alert alertException = new Alert(Alert.AlertType.ERROR);
                alertException.setTitle("Exception");
                alertException.setHeaderText("We have encounter an exception");
                alertException.setContentText("CannotRestartException");
                alertException.show();
            }
        }

    }

    private void mustRestart(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Apply changes");
        alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
        alert.setContentText("restart now?");
        ButtonType result = alert.showAndWait().get();
        if (result.equals(ButtonType.OK)) {
            try {
                RestartApplication.FullRestart();
            } catch (URISyntaxException | IOException | FileNotAJarException e) {
                Alert alertException = new Alert(Alert.AlertType.ERROR);
                alertException.setTitle("Exception");
                alertException.setHeaderText("Cannot restart");
                alertException.setContentText("The application will be force closed");
                alertException.showAndWait();
                System.exit(-1);
            }
        }
    }
}
