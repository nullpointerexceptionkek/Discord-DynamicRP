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

package lee.aspect.dev.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lee.aspect.dev.DirectoryManager;
import lee.aspect.dev.application.interfaceGui.WarningManager;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.sysUtil.RestartApplication;
import lee.aspect.dev.sysUtil.StartLaunch;
import lee.aspect.dev.sysUtil.exceptions.FileNotAJarException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class manages the default interface option and System Tray
 */
public class CustomDiscordRPC extends Application {

    private static boolean setup = false;
    public static Stage primaryStage;

    public static boolean isOnSystemTray = false;

    /**
     * This is the "main" function of the program, this method is getting called on start up
     * Sets the basic property of JavaFX and calls {@link #start(Stage)} to Launch the application interface
     *
     */
    public static void Launch(String[] args) {
        Platform.setImplicitExit(false);
        RunLoopManager.init();
        if(!SettingManager.SETTINGS.isStartTrayOnlyInterfaceClose())
            ApplicationTray.initTray();
        System.out.println("LaunchArgs: ");
        System.out.println(Arrays.toString(args));
        launch(args);
    }

    public static void LaunchSetUpDialog(String[] args){
        setup = true;
        Platform.setImplicitExit(true);
        launch(args);
    }

    public static void LaunchSilently() {
        ApplicationTray.initTray();
        RunLoopManager.runFromStartLunch();
    }

    /**
     * Launches the config interface
     * This method should be only called by javaFX
     * inits {@link RunLoopManager}
     *
     */
    @Override
    public void start(Stage pStage) {
        if(setup){
            DirectoryManager.askForDirectory();
            //create a dialog says you need to restart the program
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Restart");
            alert.setHeaderText("Restart the program");
            alert.setContentText("You need to restart the program to set up the directory");
            alert.showAndWait();
            try {
                RestartApplication.FullRestart();
            } catch (URISyntaxException | IOException | FileNotAJarException e) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Error");
                alert1.setHeaderText("Error while restarting");
                alert1.setContentText("Error while restarting the program, please restart it manually");
                alert1.showAndWait();
                e.printStackTrace();
                System.exit(0);
            }
        }
        try {
            primaryStage = pStage;
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lee/aspect/dev/icon/SystemTrayIcon.png"))));
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(SettingManager.SETTINGS.getTheme().getThemepass())).toExternalForm());
            primaryStage.setTitle("Custom Discord RPC");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest((event) -> {
                event.consume();
                onclose();
            });
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void stop() {
        //RunLoopManager.onClose();
    }

    /**
     * This method is the method that the program calls when the exit button is hit
     * it will detect whether if System Tray is supported on the system and gives you
     * an option to minimize to System Tray when it is supported
     * System Tray is defined in {@link ApplicationTray}
     */
    public void onclose() {
        if (!SystemTray.isSupported()) {
            RunLoopManager.onClose();
            return;
        }
        switch (SettingManager.SETTINGS.getMinimizeMode()) {
            case Ask:
                Alert alert = WarningManager.createAlertWithOptOut(
                        Alert.AlertType.CONFIRMATION,
                        "Minimize to System Tray",null,
                        "Do you want to minimize to System Tray?",
                        "Do not show again", param -> SettingManager.SETTINGS.setMinimizeMode(param ? Settings.MinimizeMode.WaitAndSee : Settings.MinimizeMode.Ask),ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
                var result = alert.showAndWait();
                if (result.filter(buttonType -> buttonType == ButtonType.YES).isPresent()) {
                    if(SettingManager.SETTINGS.getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        SettingManager.SETTINGS.setMinimizeMode(Settings.MinimizeMode.Always);

                    primaryStage.close();
                    if(SettingManager.SETTINGS.isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                    isOnSystemTray = true;
                    if (SettingManager.SETTINGS.isShutDownInterfaceWhenTray()) Platform.exit();
                } else if (result.filter(buttonType -> buttonType == ButtonType.NO).isPresent()) {
                    if(SettingManager.SETTINGS.getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        SettingManager.SETTINGS.setMinimizeMode(Settings.MinimizeMode.Never);
                    RunLoopManager.onClose();
                    return;
                }
                SettingManager.saveSettingToFile();
                break;
            case Always:
                primaryStage.close();
                isOnSystemTray = true;
                if(SettingManager.SETTINGS.isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                if (SettingManager.SETTINGS.isShutDownInterfaceWhenTray()) Platform.exit();
                break;
            default:
                RunLoopManager.onClose();
                break;
        }

    }

}