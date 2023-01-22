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
import lee.aspect.dev.Launch;
import lee.aspect.dev.application.interfaceGui.LoadingController;
import lee.aspect.dev.autoswitch.SwitchManager;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;
import lee.aspect.dev.language.LanguageManager;
import org.jetbrains.annotations.NotNull;


public class RunLoopManager {

    private RunLoopManager() {} // Prevents instantiation

    private static final DiscordRP discordRP = new DiscordRP();
    private static final Object startLock = new Object();
    private static final Object updateLock = new Object();
    public static boolean isRunning = false;
    public static int currentDisplay = 0;
    private static Thread runLoop;

    /**
     * Create an instance of Script and init setting and file manager
     */
    public static void init() {
        FileManager.init();
        SettingManager.loadKeyFromJson();
        Script.loadScriptFromJson();
        LanguageManager.init();
        SwitchManager.loadFromFile();
    }

    public static void runFromStartLunch() {
        init();
        do {
            try {
                startUpdate();
                break;
            } catch (NoDiscordClientException | RuntimeException ex) {
                synchronized (startLock) {
                    try {
                        // Wait for a notification from another thread before continuing
                        startLock.wait();
                    } catch (InterruptedException e) {
                        //this should never happen
                        throw new RuntimeException(ex);
                    }
                }
            }
        } while (true);
    }

    /**
     * Sent each data to DiscordIPC according to the config
     */
    public static void startUpdate() throws NoDiscordClientException {
        if (runLoop == null) {
            if (Script.getScript().getUpdateType().equals(Script.UpdateType.Random)) {
                int i = (int) (Math.random() * Script.getScript().getSize());
                currentDisplay = i;
                discordRP.LaunchReadyCallBack(Script.getScript().getUpdates(i));
            } else discordRP.LaunchReadyCallBack(Script.getScript().getUpdates(0));
        } else {
            discordRP.LaunchReadyCallBack(Script.getScript().getUpdates(getCurrentDisplay()));
        }

        isRunning = true;

        if (runLoop == null || runLoop.isInterrupted()) {
            Launch.LOGGER.debug("Thread is not created, creating a new thread");
            runLoop = new Thread("RunLoop") {
                @Override
                public void run() {
                    if (Script.getScript().getSize() == 1) {
                        return;
                    }
                    switch (Script.getScript().getUpdateType()) {
                        case Loop:
                            if (forwardLoop()) return;
                            while (isRunning) {
                                for (int i = 0; i < Script.getScript().getSize(); i++) {
                                    executeUpdate(Script.getScript().getUpdates(i));
                                    if (!isRunning) return;
                                    if (!CustomDiscordRPC.isOnSystemTray) {
                                        int finalI = i >= Script.getScript().getSize() - 1 ? 0 : i + 1;
                                        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getScript().getUpdates(finalI)));
                                    }
                                }

                            }
                            break;
                        case Stop:
                            if (forwardLoop()) return;
                        case Random:
                            while (isRunning) {
                                int i = (int) (Math.random() * Script.getScript().getSize());
                                executeUpdate(Script.getScript().getUpdates(i));
                                currentDisplay = i;
                                if (!isRunning) return;
                                if (!CustomDiscordRPC.isOnSystemTray)
                                    Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getScript().getUpdates(i)));
                            }
                            break;
                        case Reverse:
                            for (int i = 1; i < Script.getScript().getSize(); i++) {
                                if (executeIndex(i)) return;
                            }
                            while (isRunning) {
                                for (int i = Script.getScript().getSize() - 2; i >= 0; i--) {
                                    executeUpdate(Script.getScript().getUpdates(i));
                                    currentDisplay = i;
                                    if (!isRunning) return;
                                    if (!CustomDiscordRPC.isOnSystemTray) {
                                        int finalI = i == 0 ? 1 : i - 1;
                                        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getScript().getUpdates(finalI)));
                                    }
                                }
                                for (int i = 0; i < Script.getScript().getSize(); i++) {
                                    if (executeIndex(i)) return;
                                }

                            }
                            break;

                    }
                }
            };
            runLoop.start();
        }
    }

    public static void terminate() {
        isRunning = false;
        if (runLoop != null) {
            runLoop.interrupt();
        }
    }

    private static boolean executeIndex(int i) {
        executeUpdate(Script.getScript().getUpdates(i));
        currentDisplay = i;
        if (!isRunning) return true;
        if (!CustomDiscordRPC.isOnSystemTray) {
            int finalI = i >= Script.getScript().getSize() - 1 ? i - 1 : i + 1;
            Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getScript().getUpdates(finalI)));
        }
        return false;
    }

    private static boolean forwardLoop() {
        for (int i = 1; i < Script.getScript().getSize(); i++) {
            executeUpdate(Script.getScript().getUpdates(i));
            currentDisplay = i;
            if (!isRunning) return true;
            if (!CustomDiscordRPC.isOnSystemTray) {
                int finalI = i >= Script.getScript().getSize() - 1 ? 0 : i + 1;
                Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getScript().getUpdates(finalI)));
            }
        }
        return false;
    }

    /**
     * Shut down DiscordRP, this method must be called to stop displaying the RP
     */
    public static void closeCallBack() {
        discordRP.shutdown();
        terminate();
    }

    private static void executeUpdate(@NotNull Updates update) {
        if (update.getWait() == -1) {
            discordRP.update(update);
            Launch.LOGGER.debug("Executing update: " + update);
            return;
        }
        synchronized (updateLock) {
            try {
                // Wait for a notification from another thread before continuing
                updateLock.wait(update.getWait());
            } catch (InterruptedException e) {
                //this exception is ignored
                //e.printStackTrace();
            }
        }
        discordRP.update(update);
        Launch.LOGGER.debug("Executing update: " + update);
    }

    public static void onClose() {
        SettingManager.saveSettingToFile();
        Script.saveScriptToFile();
        SwitchManager.saveToFile();
        try {
            discordRP.shutdown();
        } catch (NullPointerException | IllegalStateException e) {
            //This exception is ignored, it is thrown when the RP is not running
            //the RP does not need to be close when its running
            //e.printStackTrace();
        }
        System.exit(0);
    }

    public static void saveScripToFile() {
        Script.saveScriptToFile();
    }

    public static int getCurrentDisplay() {
        return currentDisplay;
    }

    public static void setRunLoop(Thread runLoop) {
        RunLoopManager.runLoop = runLoop;
    }
}