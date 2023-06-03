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

package lee.aspect.dev.dynamicrp.application.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.animatefx.SlideOutDown;
import lee.aspect.dev.dynamicrp.application.core.ApplicationTray;
import lee.aspect.dev.dynamicrp.application.core.DynamicRP;
import lee.aspect.dev.dynamicrp.application.core.Settings;
import lee.aspect.dev.dynamicrp.exceptions.FileNotAJarException;
import lee.aspect.dev.dynamicrp.language.LanguageManager;
import lee.aspect.dev.dynamicrp.manager.ConfigManager;
import lee.aspect.dev.dynamicrp.manager.SceneManager;
import lee.aspect.dev.dynamicrp.system.SystemHandler;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private AnchorPane anchorRoot;

    @FXML
    private VBox content;

    @FXML
    private StackPane stackPane;


    @FXML
    private Label SettingsLabel, ThemeLabel, titleLabel, MinToTrayLabel, PerformanceLabel, LanguageLabel, AdvanceConfigLabel;

    public void switchBack() {
        goBack.setDisable(true);
        stackPane.getChildren().add(0, SceneManager.getConfigParent());
        SlideOutDown animation = new SlideOutDown(anchorRoot);
        animation.setOnFinished((actionEvent) -> stackPane.getChildren().remove(anchorRoot));
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
        titleLabel.setText(Launch.NAME + " " + Launch.VERSION);
        goBack.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        ImageView imageView = new ImageView(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/dynamicrp/icon/back.png")).toExternalForm());
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        goBack.setGraphic(imageView);
        //add theme to theme choice box
        EnumSet.allOf(Settings.Theme.class).forEach((theme) -> themeChoiceBox.getItems().add(theme.getDisplayName()));
        themeChoiceBox.setValue((Settings.getINSTANCE().getTheme().getDisplayName()));
        themeChoiceBox.setOnAction((event) -> EnumSet.allOf(Settings.Theme.class).forEach((theme) -> {
            if (themeChoiceBox.getValue().equals(theme.getDisplayName())) {
                Settings.getINSTANCE().setTheme(theme);
                DynamicRP.primaryStage.setScene(new Scene(SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/Settings.fxml").getRoot()));
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Apply changes");
            alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
            alert.setContentText("restart now?");
            ButtonType result = alert.showAndWait().get();
            if (result.equals(ButtonType.OK)) {
                Settings.getINSTANCE().setStartTrayOnlyInterfaceClose(startTrayOnlyCloseCheckBox.isSelected());
                try {
                    SystemHandler.fullRestart();
                } catch (URISyntaxException | IOException | FileNotAJarException e) {
                    Alert alertException = new Alert(Alert.AlertType.ERROR);
                    alertException.setTitle("Exception");
                    alertException.setHeaderText("Cannot restart");
                    alertException.setContentText("The application will be force closed");
                    alertException.showAndWait();
                    System.exit(-1);
                }
            }
            startTrayOnlyCloseCheckBox.setSelected(!startTrayOnlyCloseCheckBox.isSelected());
        }));
        startLaunchCheckBox.setSelected(Settings.getINSTANCE().isStartLaunch());
        startLaunchCheckBox.setOnAction((actionEvent -> {
            if (startLaunchCheckBox.isSelected()) {
                try {
                    SystemHandler.StartLaunch.createStartupScript();
                } catch (Exception e) {
                    Alert alertException = new Alert(Alert.AlertType.ERROR);
                    alertException.setTitle("Error");
                    alertException.setHeaderText("Unable to create startup script");

                    TextArea textArea = new TextArea(e.getMessage());
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    textArea.setMaxWidth(Double.MAX_VALUE);
                    textArea.setMaxHeight(Double.MAX_VALUE);

                    GridPane.setVgrow(textArea, Priority.ALWAYS);
                    GridPane.setHgrow(textArea, Priority.ALWAYS);

                    GridPane content = new GridPane();
                    content.setMaxWidth(Double.MAX_VALUE);
                    content.add(new Label("The following error occurred:"), 0, 0);
                    content.add(textArea, 0, 1);

                    alertException.getDialogPane().setExpandableContent(content);
                    alertException.showAndWait();
                    startLaunchCheckBox.setSelected(false);

                }
            } else {
                SystemHandler.StartLaunch.deleteStartupScript();
            }
            Settings.getINSTANCE().setStartLaunch(startLaunchCheckBox.isSelected());
        }));

        AutoSwitchCheckBox.setSelected(Settings.getINSTANCE().isAutoSwitch());
        AutoSwitchCheckBox.setOnAction((actionEvent -> {
            Settings.getINSTANCE().setAutoSwitch(AutoSwitchCheckBox.isSelected());
            if(!Settings.getINSTANCE().isStartTrayOnlyInterfaceClose())
                ApplicationTray.updatePopupMenu();
        }
        ));


        Settings.saveSettingToFile();

    }
}
