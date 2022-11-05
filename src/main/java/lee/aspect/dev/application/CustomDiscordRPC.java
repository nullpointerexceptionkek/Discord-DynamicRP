package lee.aspect.dev.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lee.aspect.dev.application.interfaceGui.WarningManager;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class manages the default interface option and System Tray
 */
public class CustomDiscordRPC extends Application {
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
        if(!Settings.isStartTrayOnlyInterfaceClose())
            ApplicationTray.initTray();
        System.out.println("LaunchArgs: ");
        System.out.println(Arrays.toString(args));
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
        try {
            primaryStage = pStage;
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/lee/aspect/dev/icon/SystemTrayIcon.png"))));
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(Settings.getTheme().getThemepass())).toExternalForm());
            primaryStage.setTitle("Custom Discord RPC");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest((event) -> {
                event.consume();
                onclose();
            });
            primaryStage.show();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            System.out.println(sStackTrace);
            String message = "\"Custom Discord RP\"\n"
                    + "it looks like we cannot connect to javaFX\n"
                    + "Please check if javaFX is installed";
            JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);

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
        switch (Settings.getMinimizeMode()) {
            case Ask:
                Alert alert = WarningManager.createAlertWithOptOut(
                        Alert.AlertType.CONFIRMATION,
                        "Minimize to System Tray",null,
                        "Do you want to minimize to System Tray?",
                        "Do not show again", param -> Settings.setMinimizeMode(param ? Settings.MinimizeMode.WaitAndSee : Settings.MinimizeMode.Ask),ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
                var result = alert.showAndWait();
                if (result.filter(buttonType -> buttonType == ButtonType.YES).isPresent()) {
                    if(Settings.getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        Settings.setMinimizeMode(Settings.MinimizeMode.Always);

                    primaryStage.close();
                    if(Settings.isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                    isOnSystemTray = true;
                    if (Settings.isShutDownInterfaceWhenTray()) Platform.exit();
                } else if (result.filter(buttonType -> buttonType == ButtonType.NO).isPresent()) {
                    if(Settings.getMinimizeMode() == Settings.MinimizeMode.WaitAndSee)
                        Settings.setMinimizeMode(Settings.MinimizeMode.Never);
                    RunLoopManager.onClose();
                    return;
                }
                SettingManager.saveSettingToFile();
                break;
            case Always:
                primaryStage.close();
                isOnSystemTray = true;
                if(Settings.isStartTrayOnlyInterfaceClose()) ApplicationTray.initTray();
                if (Settings.isShutDownInterfaceWhenTray()) Platform.exit();
                break;
            default:
                RunLoopManager.onClose();
                break;
        }

    }

}