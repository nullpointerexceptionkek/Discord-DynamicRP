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
    private static UpdateManager upm;

    private static Thread runloop;

    private static int CURRENTDISPLAY = 0;

    /**
     * Create an instance of UpdateManager and init setting and file manager
     */
    public static void init() {
        FileManager.init();
        SettingManager.init();
        upm = new UpdateManager();


    }

    public static void runFromStartLunch() {
        FileManager.init();
        SettingManager.init();
        upm = new UpdateManager();
        var delay = 16000;
        do {
            try {
                discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(0));
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
        isRunning = true;
        new Thread(() -> {
            if (upm.getUpdates().getSize() == 1) {
                return;
            }
            for (int i = 1; i < upm.getUpdates().getSize(); i++) {
                executeUpdate(upm.getUpdates().getUpdates(i));
                if (!isRunning) return;
            }
            while (isRunning) {
                for (int i = 0; i < upm.getUpdates().getSize(); i++) {
                    executeUpdate(upm.getUpdates().getUpdates(i));
                    if (!isRunning) return;
                }

            }
        }).start();
    }

    /**
     * Sent each data to DiscordIPC according to the config
     */
    public static void startUpdate() throws NoDiscordClientException {
        if (runloop == null) {
            CURRENTDISPLAY = 0;
            discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(0));
        }
        else {
            discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(getCURRENTDISPLAY()));
        }

        isRunning = true;

        if (runloop == null) {
            System.out.println("Thread is not created, creating a new thread");
            runloop = new Thread("RunLoop") {
                @Override
                public void run() {
                    if (upm.getUpdates().getSize() == 1) {
                        return;
                    }
                    for (int i = 1; i < upm.getUpdates().getSize(); i++) {
                        executeUpdate(upm.getUpdates().getUpdates(i));
                        CURRENTDISPLAY = i;

                        if (!isRunning) return;
                        if (!CustomDiscordRPC.isOnSystemTray)
                            Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay());
                    }
                    while (isRunning) {
                        for (int i = 0; i < upm.getUpdates().getSize(); i++) {
                            executeUpdate(upm.getUpdates().getUpdates(i));
                            CURRENTDISPLAY = i;
                            if (!isRunning) return;
                            if (!CustomDiscordRPC.isOnSystemTray)
                                Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay());
                        }

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
        upm.saveScriptToFile();
        try {
            discordRP.shutdown();
        } catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void saveScripToFile() {
        upm.saveScriptToFile();
    }

    public static int getCURRENTDISPLAY() {
        return CURRENTDISPLAY;
    }

    public static void setRunloop(Thread runloop) {
        RunLoopManager.runloop = runloop;
    }
}