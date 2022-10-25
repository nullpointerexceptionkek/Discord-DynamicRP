package lee.aspect.dev.application;

import javafx.application.Platform;
import lee.aspect.dev.application.interfaceGui.LoadingController;
import lee.aspect.dev.discordrpc.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

public class ApplicationTray {

    public ApplicationTray() {
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
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(Objects.requireNonNull(createImage("/lee/aspect/dev/icon/SystemTrayIcon.png", "tray icon")));
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem showInterface = new MenuItem("Show Interface");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
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

        trayIcon.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "This dialog box is run from System Tray"));

        showInterface.addActionListener(e -> {
            if (Settings.isShutDownInterfaceWhenTray()) {
                trayIcon.displayMessage("CDiscordRPC",
                        "Application cannot start interface when ShutDownInterface is on, please exit and relaunch the program to see interface", TrayIcon.MessageType.ERROR);
                return;
            }
            CustomDiscordRPC.isOnSystemTray = false;
            Platform.runLater(() -> {
                CustomDiscordRPC.primaryStage.show();
                if (RunLoopManager.isRunning)
                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay());
            });
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

}
