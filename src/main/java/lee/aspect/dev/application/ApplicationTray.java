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

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lee.aspect.dev.Launch;
import lee.aspect.dev.application.interfaceGui.CallBackController;
import lee.aspect.dev.application.interfaceGui.ConfigController;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * This class manages the system tray
 */
public class ApplicationTray {

    private static TrayIcon trayIcon;

    private static SystemTray tray;

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
        MenuItem startRPCItem = new MenuItem("Start RPC");
        MenuItem closeRPCItem = new MenuItem("Close RPC");
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
                "Author: " + Launch.AUTHOR + '\n' +
                        Launch.NAME + '\n' +
                        "Version: " + Launch.VERSION,
                "About", JOptionPane.INFORMATION_MESSAGE
        ));

        startRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning) {
                JOptionPane.showMessageDialog(null, "Discord RPC is already running", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if(Settings.isShutDownInterfaceWhenTray()) {
                        RunLoopManager.startUpdate();
                        return;
                    }
                    Platform.runLater(()-> {
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
                    });
                } catch (NoDiscordClientException ex) {
                    JOptionPane.showMessageDialog(null, "Fail to start call back", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        closeRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning) {
                if(Settings.isShutDownInterfaceWhenTray()) {
                    RunLoopManager.closeCallBack();
                    return;
                }
                Platform.runLater(()-> {
                    try {
                        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(CustomDiscordRPC.class.getResource("/lee/aspect/dev/scenes/CallBack.fxml")));
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(Objects.requireNonNull(ApplicationTray.class.getResource(Settings.getTheme().getThemepass())).toExternalForm());
                        CustomDiscordRPC.primaryStage.setScene(scene);
                        CallBackController controller = loader.getController();
                        controller.switchToConfig();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
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
            CustomDiscordRPC.primaryStage.show();
        });
        CustomDiscordRPC.isOnSystemTray = false;
        if (Settings.isStartTrayOnlyInterfaceClose()) tray.remove(trayIcon);
    }

}
