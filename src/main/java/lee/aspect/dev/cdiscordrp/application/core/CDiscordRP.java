/*
 *
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

package lee.aspect.dev.cdiscordrp.application.core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lee.aspect.dev.cdiscordrp.Launch;
import lee.aspect.dev.cdiscordrp.manager.ConfigManager;
import lee.aspect.dev.cdiscordrp.manager.DirectoryManager;
import lee.aspect.dev.cdiscordrp.manager.SceneManager;
import lee.aspect.dev.cdiscordrp.util.WarningManager;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class manages the default interface option and system Tray.
 *
 * <p>The `CDiscordRP` class extends the `Application` class from JavaFX and provides three
 * static methods to launch the application:
 *
 * <ul>
 *   <li>{@link #Launch(String[])}: Launches the application with the given command-line arguments.
 *       If the `setup` flag is set, this method will ask the user to choose a directory for saving
 *       files and then restart the application to apply the changes.
 *       If the `setup` flag is not set, this method will initialize the {@link RunLoopManager}
 *       class and load the main configuration interface. It will also register an event handler to
 *       call the {@link #onclose()} method when the user closes the application window.
 *   <li>{@link #LaunchSetUpDialog(String[])}: Same as {@link #Launch(String[])}, but always sets
 *       the `setup` flag to `true`.
 *   <li>{@link #LaunchSilently()}: Launches the application without showing the main interface.
 *       This method initializes the system Tray and calls the {@link RunLoopManager#runFromStartLunch()}
 *       method to run the application in the background.
 * </ul>
 *
 * <p>The `CDiscordRP` class also provides the {@link #start(Stage)} method to launch the
 * application interface. This method is called by JavaFX when the application is started.
 *
 * <p>TLDR? this is generated by ChatGPT <3, The CDiscordRP class extends the Application
 * class from JavaFX and is used to launch the configuration interface for the application.
 */
public class CDiscordRP extends Application {

    public static Stage primaryStage;
    public static boolean isOnSystemTray = false;
    private static boolean setup = false;

    /**
     * This is the "main" function of the program, this method is getting called on start up
     * Sets the basic property of JavaFX and calls {@link #start(Stage)} to Launch the application interface
     */
    public static void Launch(String[] args) {
        Platform.setImplicitExit(false);
        Launch.LOGGER.debug(Arrays.toString(ConfigManager.getCurrentConfigFiles()));
        if (!Settings.getINSTANCE().isStartTrayOnlyInterfaceClose())
            ApplicationTray.initTray();
        Launch.LOGGER.debug("LaunchArgs: ");
        Launch.LOGGER.debug(Arrays.toString(args));
        launch(args);
    }

    /**
     * This will be called if the program has checked that the required environment variables are not set
     * This means that all the process like file managers cannot be load, in this case we just want to
     * load javaFX so the dialog written in JavaFX can be displayed
     * the setup is the variable that will be set to true, this will make the program to load the setup dialog
     * once {@link #start(Stage)} is called
     */
    public static void LaunchSetUpDialog(String[] args) {
        setup = true;
        Platform.setImplicitExit(true);
        launch(args);
    }

    public static void LaunchSilently() {
        ApplicationTray.initTray();
        RunLoopManager.runFromStartLunch();
    }


    /**
     * Launches the configuration interface for the application.
     *
     * <p>This method is called by JavaFX when the application is started. If the `setup` flag is set,
     * the method will ask the user to choose a directory for saving files, and then restart the
     * application to apply the changes.
     *
     * <p>If the `setup` flag is not set, the method will initialize the {@link RunLoopManager} class
     * and load the main configuration interface using the {@link FXMLLoader} class. The method also
     * sets the scene's stylesheet and registers an event handler to call the {@link #onclose()} method
     * when the user closes the application window.
     *
     * @param pStage the primary stage provided by JavaFX
     * @throws RuntimeException if an error occurs while loading the configuration interface
     */
    @Override
    public void start(Stage pStage) {
        if (setup) {
            //this sets up the environment variables(to access the file save directory)
            DirectoryManager.askForDirectory();

        }
        try {
            primaryStage = pStage;
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lee/aspect/dev/cdiscordrp/icon/SystemTrayIcon.png"))));
            primaryStage.setTitle(Launch.NAME);
            primaryStage.setScene(new Scene(SceneManager.getConfigParent()));
            primaryStage.setMinHeight(540.0);
            primaryStage.setMinWidth(334.0);
            if(Settings.getINSTANCE().getWindowHeight() != -1)
                primaryStage.setHeight(Settings.getINSTANCE().getWindowHeight());
            if(Settings.getINSTANCE().getWindowWidth() != -1)
                primaryStage.setWidth(Settings.getINSTANCE().getWindowWidth());
            //primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest((event) -> {
                event.consume();
                onclose();
            });
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public static void popUpWindow(){
        Platform.runLater(() -> {
            CDiscordRP.primaryStage.show();
            CDiscordRP.primaryStage.setAlwaysOnTop(true);
            CDiscordRP.primaryStage.toFront();
            CDiscordRP.primaryStage.requestFocus();
            CDiscordRP.primaryStage.setIconified(false);
            CDiscordRP.primaryStage.setAlwaysOnTop(false);
        });
    }


    /**
     * Handles application exit.
     *
     * <p>This method is called when the user clicks the exit button. The method checks if the system
     * tray is supported, and then takes appropriate action based on the user's preference for minimizing
     * the application to the system tray. If the application is not minimized to the system tray, the
     * {@link RunLoopManager#onClose()} method is called to perform necessary clean-up before the
     * application is exited.
     *
     * <p>The user's preference for minimizing to the system tray is determined by the value of the
     * {@link Settings.MinimizeMode} enum, which is stored in the {@link Settings} class. If the
     * user's preference is not set, the method will prompt the user to choose whether to minimize to the
     * system tray or exit the application.
     *
     * <p>The application's settings are saved to a file using the {@link Settings} class before
     * the application is exited.
     */
    public void onclose() {
        if (!SystemTray.isSupported()) {
            RunLoopManager.onClose();
            return;
        }
        switch (Settings.getINSTANCE().getMinimizeMode()) {
            case Ask:
                Alert alert = WarningManager.createAlertWithOptOut(
                        Alert.AlertType.CONFIRMATION,
                        "Minimize to system Tray", null,
                        "Do you want to minimize to system Tray?",
                        "Do not show again", param -> Settings.getINSTANCE().setMinimizeMode(param ? Settings.MinimizeMode.WaitAndSee : Settings.MinimizeMode.Ask), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                java.util.Optional<ButtonType> result = alert.showAndWait();
                if (result.filter(buttonType -> buttonType == ButtonType.YES).isPresent()) {
                    if (Settings.getINSTANCE().getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        Settings.getINSTANCE().setMinimizeMode(Settings.MinimizeMode.Always);

                    primaryStage.close();
                    if (Settings.getINSTANCE().isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                    isOnSystemTray = true;
                    if (Settings.getINSTANCE().isShutDownInterfaceWhenTray()) Platform.exit();
                } else if (result.filter(buttonType -> buttonType == ButtonType.NO).isPresent()) {
                    if (Settings.getINSTANCE().getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        Settings.getINSTANCE().setMinimizeMode(Settings.MinimizeMode.Never);
                    RunLoopManager.onClose();
                    return;
                }
                Settings.saveSettingToFile();
                break;
            case Always:
                primaryStage.close();
                isOnSystemTray = true;
                if (Settings.getINSTANCE().isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                if (Settings.getINSTANCE().isShutDownInterfaceWhenTray()) Platform.exit();
                break;
            default:
                RunLoopManager.onClose();
                break;
        }

    }

}