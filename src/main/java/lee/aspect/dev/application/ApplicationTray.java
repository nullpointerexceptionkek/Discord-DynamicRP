package lee.aspect.dev.application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lee.aspect.dev.application.interfaceGui.ConfigController;
import lee.aspect.dev.application.interfaceGui.LoadingController;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ApplicationTray {

    private static TrayIcon trayIcon;

    private static SystemTray tray;

    private static Screen currentScreen = Screen.UnSpecified;

    public static void initTray() {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(ApplicationTray::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        trayIcon =
                new TrayIcon(Objects.requireNonNull(createImage("/lee/aspect/dev/icon/SystemTrayIcon.png", "tray icon")));
        trayIcon.setImageAutoSize(true);
        tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem startRPCItem = new MenuItem("StartRPC");
        MenuItem closeRPCItem = new MenuItem("closeRPC");
        MenuItem showInterface = new MenuItem("Show Interface");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(startRPCItem);
        popup.add(closeRPCItem);
        popup.addSeparator();
        popup.add(showInterface);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
        trayIcon.addActionListener(e -> LaunchInterface());
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(null,
                Settings.AUTHOR + '\n' +
                        Settings.NAME + '\n' +
                        "Version: " + Settings.VERSION,
                "About", JOptionPane.INFORMATION_MESSAGE
        ));

        startRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning) {
                JOptionPane.showMessageDialog(null, "Discord RPC is already running", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if(CustomDiscordRPC.isOnSystemTray) {
                        RunLoopManager.startUpdate();
                        currentScreen = Screen.CallBackScreen;
                    } else {
                        Platform.runLater(()-> {
                            refreshCallBack();
                        });
                    }
                } catch (NoDiscordClientException ex) {
                    JOptionPane.showMessageDialog(null, "Fail to start call back", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        closeRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning) {
                RunLoopManager.closeCallBack();
                currentScreen = Screen.ConfigScreen;
            } else {
                JOptionPane.showMessageDialog(null, "Discord RPC is not running", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        showInterface.addActionListener(e -> {
            LaunchInterface();
        });

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            RunLoopManager.onClose();
        });
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = ApplicationTray.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    private static void LaunchInterface() {
        if (Settings.isShutDownInterfaceWhenTray()) {
            trayIcon.displayMessage("CDiscordRPC",
                    "Application cannot start interface when ShutDownInterface is on, please exit and relaunch the program to see interface", TrayIcon.MessageType.ERROR);
            return;
        }
        Platform.runLater(()->{
            switch (currentScreen) {
                case ConfigScreen:
                        try {
                            Parent root = FXMLLoader.load(Objects.requireNonNull(CustomDiscordRPC.class.getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                            CustomDiscordRPC.primaryStage.getScene().setRoot(root);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    break;
                case CallBackScreen:
                    refreshCallBack();
                    break;
                default:
                    if (RunLoopManager.isRunning) LoadingController.callBackController.updateCurrentDisplay();
            }
            currentScreen = Screen.UnSpecified;
            CustomDiscordRPC.primaryStage.show();
        });
        CustomDiscordRPC.isOnSystemTray = false;
        if (Settings.isStartTrayOnlyInterfaceClose()) tray.remove(trayIcon);
    }

    private enum Screen {
        ConfigScreen,
        CallBackScreen,

        UnSpecified
    }

    private static void refreshCallBack(){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(CustomDiscordRPC.class.getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(ApplicationTray.class.getResource(Settings.getTheme().getThemepass())).toExternalForm());
            CustomDiscordRPC.primaryStage.setScene(scene);
            ConfigController controller = loader.getController();
            controller.switchToCallBack();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
