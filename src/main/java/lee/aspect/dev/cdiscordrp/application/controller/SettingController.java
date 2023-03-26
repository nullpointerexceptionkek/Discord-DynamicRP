/*
 * 2022-
 * MIT License
 *
 * Copyright (c) 2023 lee
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

package lee.aspect.dev.cdiscordrp.application.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC;
import lee.aspect.dev.cdiscordrp.exceptions.Debug;
import lee.aspect.dev.cdiscordrp.manager.SceneManager;
import lee.aspect.dev.cdiscordrp.Launch;
import lee.aspect.dev.cdiscordrp.animatefx.SlideOutLeft;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.manager.ConfigManager;
import lee.aspect.dev.cdiscordrp.language.LanguageManager;
import lee.aspect.dev.cdiscordrp.util.WarningManager;
import lee.aspect.dev.cdiscordrp.util.system.StartLaunch;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML
    private Button goBack, ChangeLanguageButton, ManageScriptButton;

    @FXML
    private ChoiceBox<String> themeChoiceBox;

    @FXML
    private ChoiceBox<Settings.MinimizeMode> minimizeModeChoiceBox;

    @FXML
    private CheckBox shutDownInterfaceCheckBox,
            noAnimationCheckBox,
            startTrayOnlyCloseCheckBox,
            startLaunchCheckBox,
            AutoSwitchCheckBox;

    @FXML
    private AnchorPane settingsAnchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private Label SettingsLabel, ThemeLabel, titleLabel, ReleaseLabel, MinToTrayLabel, PerformanceLabel, LanguageLabel, AdvanceConfigLabel;

    public void switchBack() throws IOException {
        goBack.setDisable(true);
        stackPane.getChildren().add(0, SceneManager.getConfigParent());
        SlideOutLeft animation = new SlideOutLeft(settingsAnchorPane);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(settingsAnchorPane));
        animation.play();

    }

    public void changeLanguage() {
        LanguageManager.showDialog();
    }

    public void openConfigManager() {
        ConfigManager.showDialog(true, null);
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
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/cdiscordrp/icon/back.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        goBack.setGraphic(imageView);
        //add theme to theme choice box
        EnumSet.allOf(Settings.Theme.class).forEach((theme) -> themeChoiceBox.getItems().add(theme.getDisplayName()));
        themeChoiceBox.setValue((Settings.getINSTANCE().getTheme().getDisplayName()));
        themeChoiceBox.setOnAction((event) -> EnumSet.allOf(Settings.Theme.class).forEach((theme) -> {
            if (themeChoiceBox.getValue().equals(theme.getDisplayName())) {
                Settings.getINSTANCE().setTheme(theme);
                CustomDiscordRPC.primaryStage.setScene(new Scene(SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/Settings.fxml").getRoot()));
            }
        }));
        //add min to min choice box
        minimizeModeChoiceBox.setDisable(!SystemTray.isSupported());
        for (Settings.MinimizeMode mode : Settings.MinimizeMode.values()) {
            if (mode != Settings.MinimizeMode.WaitAndSee)
                minimizeModeChoiceBox.getItems().add(mode);
        }
        minimizeModeChoiceBox.setValue((Settings.getINSTANCE().getMinimizeMode()));
        minimizeModeChoiceBox.setOnAction((event) -> Settings.getINSTANCE().setMinimizeMode(minimizeModeChoiceBox.getValue()));
        //add for booleans
        shutDownInterfaceCheckBox.setDisable(!SystemTray.isSupported());
        shutDownInterfaceCheckBox.setSelected(Settings.getINSTANCE().isShutDownInterfaceWhenTray());
        shutDownInterfaceCheckBox.setOnAction((actionEvent -> Settings.getINSTANCE().setShutDownInterfaceWhenTray(shutDownInterfaceCheckBox.isSelected())));

        noAnimationCheckBox.setSelected(Settings.getINSTANCE().isNoAnimation());
        noAnimationCheckBox.setOnAction((actionEvent -> Settings.getINSTANCE().setNoAnimation(noAnimationCheckBox.isSelected())));

        startTrayOnlyCloseCheckBox.setSelected(!SystemTray.isSupported());
        startTrayOnlyCloseCheckBox.setSelected(Settings.getINSTANCE().isStartTrayOnlyInterfaceClose());
        startTrayOnlyCloseCheckBox.setOnAction((actionEvent -> {
            Settings.getINSTANCE().setStartTrayOnlyInterfaceClose(startTrayOnlyCloseCheckBox.isSelected());
            WarningManager.forceRestart();
        }));
        startLaunchCheckBox.setDisable(!StartLaunch.isOnWindows());
        startLaunchCheckBox.setSelected(Settings.getINSTANCE().isStartLaunch());
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
            Settings.getINSTANCE().setStartLaunch(startLaunchCheckBox.isSelected());
        }));

        AutoSwitchCheckBox.setSelected(Settings.getINSTANCE().isAutoSwitch());
        AutoSwitchCheckBox.setOnAction((actionEvent -> Settings.getINSTANCE().setAutoSwitch(AutoSwitchCheckBox.isSelected())));


        Settings.saveSettingToFile();

    }
}
