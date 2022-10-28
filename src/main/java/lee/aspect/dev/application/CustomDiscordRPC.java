package lee.aspect.dev.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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
     * @param args
     */
    public static void Launch(String[] args) {
        Platform.setImplicitExit(false);
        RunLoopManager.init();
        if(!Settings.isStartTrayOnlyInterfaceClose())
            new ApplicationTray();
        System.out.println("LaunchArgs: ");
        System.out.println(Arrays.toString(args));
        launch(args);
    }

    public static void LaunchSlient() {
        new ApplicationTray();
        RunLoopManager.runFromStartLunch();
    }

    /**
     * Launches the config interface
     * This method should be only called by javaFX
     * inits {@link RunLoopManager}
     *
     * @param pstage
     */
    @Override
    public void start(Stage pstage) {
        try {
            primaryStage = pstage;
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
                var alert = new Alert(Alert.AlertType.CONFIRMATION);
                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(yesButton, noButton);
                alert.setTitle("Close");
                alert.setHeaderText("SystemTray is supported");
                alert.setContentText("minimize to System tray instead of being closed?");
                ButtonType result = alert.showAndWait().get();
                if (result.equals(yesButton)) {
                    primaryStage.close();
                    if(Settings.isStartTrayOnlyInterfaceClose()) new ApplicationTray();
                    isOnSystemTray = true;
                    if (Settings.isShutDownInterfaceWhenTray()) Platform.exit();
                } else if (result.equals(noButton)) {
                    RunLoopManager.onClose();
                }
                break;
            case Always:
                primaryStage.close();
                isOnSystemTray = true;
                if(Settings.isStartTrayOnlyInterfaceClose()) new ApplicationTray();
                if (Settings.isShutDownInterfaceWhenTray()) Platform.exit();
                break;
            case Never:
                RunLoopManager.onClose();
                break;
        }

    }

}
