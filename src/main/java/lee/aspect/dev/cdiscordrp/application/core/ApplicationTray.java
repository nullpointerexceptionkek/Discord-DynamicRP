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

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lee.aspect.dev.cdiscordrp.Launch;
import lee.aspect.dev.cdiscordrp.application.controller.CallBackController;
import lee.aspect.dev.cdiscordrp.application.controller.ConfigController;
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager;
import lee.aspect.dev.cdiscordrp.exceptions.NoDiscordClientException;
import lee.aspect.dev.cdiscordrp.manager.SceneManager;

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
    private static MenuItem startRPCItem;
    private static MenuItem closeRPCItem;

    private static MenuItem startSwitchItem;
    private static MenuItem closeSwitchItem;

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
            Launch.LOGGER.error("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(Objects.requireNonNull(createImage("/lee/aspect/dev/cdiscordrp/icon/SystemTrayIcon.png", "tray icon")));
        trayIcon.setImageAutoSize(true);
        tray = SystemTray.getSystemTray();

        // Add components to popup menu
        MenuItem aboutItem = new MenuItem("About");
        startRPCItem = new MenuItem("Start RPC");
        closeRPCItem = new MenuItem("Close RPC");
        startSwitchItem = new MenuItem("Start Switch");
        closeSwitchItem = new MenuItem("Close Switch");
        MenuItem showInterface = new MenuItem("Show Interface");
        MenuItem exitItem = new MenuItem("Exit");

        // Add components to popup menu based on the state of the RPC
        if(Settings.getINSTANCE().isAutoSwitch()){
            if (SwitchManager.getRunning()) {
                popup.add(closeSwitchItem);
            } else {
                popup.add(startSwitchItem);
            }
        } else{
            if (RunLoopManager.isRunning()) {
                popup.add(closeRPCItem);
            } else {
                popup.add(startRPCItem);
            }
        }

        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(showInterface);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            Launch.LOGGER.error("TrayIcon could not be added.");
            return;
        }
        trayIcon.addActionListener(e -> LaunchInterface());
        aboutItem.addActionListener(e -> {
            String version = Launch.VERSION;
            String author = Launch.AUTHOR;
            String name = Launch.NAME;
            String license = "MIT License";

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel(name + " " + version, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));

            JTextArea textArea = new JTextArea();
            textArea.setText("Author: " + author + "\nLicense: " + license);
            textArea.setEditable(false);
            textArea.setBackground(panel.getBackground());

            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(textArea, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(null, panel, "About", JOptionPane.PLAIN_MESSAGE);
        });
        startRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning()) {
                JOptionPane.showMessageDialog(null, "Discord RPC is already running", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if (Settings.getINSTANCE().isShutDownInterfaceWhenTray()) {
                        RunLoopManager.startUpdate();
                        return;
                    }
                    Platform.runLater(() -> {
                        try {
                            SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/ReadyConfig.fxml");
                            Parent root = sceneData.getRoot();
                            Scene scene = new Scene(root);
                            CustomDiscordRPC.primaryStage.setScene(scene);
                            ConfigController controller = (ConfigController) sceneData.getController();
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
        startSwitchItem.addActionListener(e -> {
            if (SwitchManager.getRunning()) {
                JOptionPane.showMessageDialog(null, "Auto Switch is already running", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Platform.runLater(SwitchManager::toggleRunning);
            }
        });
        closeSwitchItem.addActionListener(e -> {
            if (SwitchManager.getRunning()) {
                Platform.runLater(SwitchManager::toggleRunning);
            } else {
                JOptionPane.showMessageDialog(null, "Auto Switch is not running", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        closeRPCItem.addActionListener(e -> {
            if (RunLoopManager.isRunning()) {
                if (Settings.getINSTANCE().isShutDownInterfaceWhenTray()) {
                    RunLoopManager.closeCallBack();
                    return;
                }
                Platform.runLater(() -> {
                    try {
                        SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/CallBack.fxml");
                        Parent root = sceneData.getRoot();
                        Scene scene = new Scene(root);
                        CustomDiscordRPC.primaryStage.setScene(scene);
                        CallBackController controller = (CallBackController) sceneData.getController();
                        controller.switchToConfig();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(null, "Discord RPC is not running", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        showInterface.addActionListener(e -> LaunchInterface());

        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            RunLoopManager.onClose();
        });
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = ApplicationTray.class.getResource(path);

        if (imageURL == null) {
            Launch.LOGGER.error("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    private static void LaunchInterface() {
        if (Settings.getINSTANCE().isShutDownInterfaceWhenTray()) {
            trayIcon.displayMessage("CDiscordRPC",
                    "Application cannot start interface when ShutDownInterface is on, please exit and relaunch the program to see interface", TrayIcon.MessageType.ERROR);
            return;
        }
        Platform.runLater(() -> CustomDiscordRPC.primaryStage.show());
        CustomDiscordRPC.isOnSystemTray = false;
        if (Settings.getINSTANCE().isStartTrayOnlyInterfaceClose()) {
            tray.remove(trayIcon);
        }
    }

    public static void updatePopupMenu() {
        PopupMenu popup = trayIcon.getPopupMenu();
        popup.remove(startRPCItem);
        popup.remove(closeRPCItem);
        popup.remove(startSwitchItem);
        popup.remove(closeSwitchItem);

        if(Settings.getINSTANCE().isAutoSwitch()){
            if (SwitchManager.getRunning()) {
                popup.insert(closeSwitchItem,0);
            } else {
                popup.insert(startSwitchItem,0);
            }
        } else{
            if (RunLoopManager.isRunning()) {
                popup.insert(closeRPCItem,0);
            } else {
                popup.insert(startRPCItem,0);
            }
        }

        trayIcon.setPopupMenu(popup);
    }
}
