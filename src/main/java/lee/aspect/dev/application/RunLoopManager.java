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
import lee.aspect.dev.application.interfaceGui.LoadingController;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;
import org.jetbrains.annotations.NotNull;


public abstract class RunLoopManager {

    private static final DiscordRP discordRP = new DiscordRP();
    public static boolean isRunning = false;
    private static Thread runloop;

    public static int CURRENTDISPLAY = 0;


    /**
     * Create an instance of UpdateManager and init setting and file manager
     */
    public static void init() {
        FileManager.init();
        SettingManager.init();
        UpdateManager.init();
    }

    public static void runFromStartLunch() {
        init();
        var delay = 16000;
        do {
            try {
                startUpdate();
                break;
            } catch (NoDiscordClientException | RuntimeException ex) {
                try {
                    Thread.sleep(Math.min(delay, 60000));
                    delay += 3000;
                } catch (InterruptedException e) {
                    throw new RuntimeException(ex);
                }
            }
        } while (true); //everyone likes while true
    }

    /**
     * Sent each data to DiscordIPC according to the config
     */
    public static void startUpdate() throws NoDiscordClientException {
        if (runloop == null) {
            discordRP.LaunchReadyCallBack(UpdateManager.SCRIPT.getUpdates(0));
        }
        else {
            discordRP.LaunchReadyCallBack(UpdateManager.SCRIPT.getUpdates(getCURRENTDISPLAY()));
        }

        isRunning = true;

        if (runloop == null) {
            System.out.println("Thread is not created, creating a new thread");
            runloop = new Thread("RunLoop") {
                @Override
                public void run() {
                    if (UpdateManager.SCRIPT.getSize() == 1) {
                        return;
                    }
                    switch(UpdateManager.SCRIPT.getUpdateType()){
                        case Loop:
                            for (int i = 1; i < UpdateManager.SCRIPT.getSize(); i++) {
                                executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                CURRENTDISPLAY = i;
                                if (!isRunning) return;
                                if (!CustomDiscordRPC.isOnSystemTray) {
                                    int finalI = i >= UpdateManager.SCRIPT.getSize() - 1 ? 0 : i + 1;
                                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                }
                            }
                            while (isRunning) {
                                for (int i = 0; i < UpdateManager.SCRIPT.getSize(); i++) {
                                    executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                    if (!isRunning) return;
                                    if (!CustomDiscordRPC.isOnSystemTray){
                                        int finalI = i >= UpdateManager.SCRIPT.getSize() - 1 ? 0 : i + 1;
                                        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                    }
                                }

                            }
                            break;
                        case Stop:
                            for (int i = 1; i < UpdateManager.SCRIPT.getSize(); i++) {
                                executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                CURRENTDISPLAY = i;
                                if (!isRunning) return;
                                if (!CustomDiscordRPC.isOnSystemTray){
                                    int finalI = i >= UpdateManager.SCRIPT.getSize() - 1 ? 0 : i + 1;
                                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                }
                            }
                        case Random:
                            while (isRunning) {
                                int i = (int) (Math.random() * UpdateManager.SCRIPT.getSize());
                                executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                CURRENTDISPLAY = i;
                                if (!isRunning) return;
                                if (!CustomDiscordRPC.isOnSystemTray)
                                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(i)));
                            }
                            break;
                        case Reverse:
                            for (int i = 1; i < UpdateManager.SCRIPT.getSize(); i++) {
                                executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                CURRENTDISPLAY = i;
                                if (!isRunning) return;
                                if (!CustomDiscordRPC.isOnSystemTray){
                                    int finalI = i >= UpdateManager.SCRIPT.getSize() - 1 ? i-1 : i + 1;
                                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                }
                            }
                            while (isRunning) {
                                for (int i = UpdateManager.SCRIPT.getSize()-2; i >= 0; i--) {
                                    executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                    CURRENTDISPLAY = i;
                                    if (!isRunning) return;
                                    if (!CustomDiscordRPC.isOnSystemTray){
                                        int finalI = i == 0 ? 1 : i - 1;
                                        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                    }
                                }
                                for (int i = 0; i < UpdateManager.SCRIPT.getSize(); i++) {
                                    executeUpdate(UpdateManager.SCRIPT.getUpdates(i));
                                    CURRENTDISPLAY = i;
                                    if (!isRunning) return;
                                    if (!CustomDiscordRPC.isOnSystemTray){
                                        int finalI = i >= UpdateManager.SCRIPT.getSize() - 1 ? i-1 : i + 1;
                                        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(UpdateManager.SCRIPT.getUpdates(finalI)));
                                    }
                                }

                            }
                            break;

                    }
                }
            };
            runloop.start();
        }
    }

    /**
     * Shut down DiscordRP, this method must be called to stop displaying the RP
     */
    public static void closeCallBack() {
        System.out.println();
        discordRP.shutdown();
        isRunning = false;
    }


    private static void executeUpdate(@NotNull Updates update) {
        try {
            if (update.getWait() == -1) {
                discordRP.update(update);
                return;
            }
            Thread.sleep(update.getWait());
            discordRP.update(update);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void onClose() {
        SettingManager.saveSettingToFile();
        UpdateManager.saveScriptToFile();
        try {
            discordRP.shutdown();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void saveScripToFile() {
        UpdateManager.saveScriptToFile();
    }

    public static int getCURRENTDISPLAY() {
        return CURRENTDISPLAY;
    }

    public static void setRunloop(Thread runloop) {
        RunLoopManager.runloop = runloop;
    }
}