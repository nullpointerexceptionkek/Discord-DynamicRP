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

package lee.aspect.dev.dynamicrp.application.core;

import javafx.application.Platform;
import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.application.controller.LoadingController;
import lee.aspect.dev.dynamicrp.autoswitch.SwitchManager;
import lee.aspect.dev.dynamicrp.exceptions.NoDiscordClientException;
import org.jetbrains.annotations.NotNull;


public class RunLoopManager {

    private static final DiscordRP discordRP = new DiscordRP();
    private static final Object startLock = new Object();
    private static final Object updateLock = new Object();
    private static boolean running = false;
    public static int currentDisplay = 0;
    private static Thread runLoop;

    private RunLoopManager() {
    } // Prevents instantiation

    /**
     * Create an instance of Script and initManagers setting and file manager
     */

    public static void runFromStartLunch() {
        try{
            Thread.sleep(5000); // 5 second should be enough for discord to start
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(Settings.getINSTANCE().isAutoSwitch()){
            SwitchManager.initAutoSwitchSilent();
            return;
        }
        do {
            try {
                startUpdate();
                break;
            } catch (NoDiscordClientException | RuntimeException ex) {
                synchronized (startLock) {
                    try {
                        startLock.wait(10000); //check every 10 seconds
                    } catch (InterruptedException e) {
                        //this should never happen
                        throw new RuntimeException(ex);
                    }
                }
            }
        } while (true);
    }

    /**
     * Sent each data to DiscordIPC according to the manager
     */
    public static void startUpdate() throws NoDiscordClientException {
        if (runLoop == null) {
            if (Script.getINSTANCE().getUpdateType().equals(Script.UpdateType.Random)) {
                int i = (int) (Math.random() * Script.getINSTANCE().getSize());
                currentDisplay = i;
                discordRP.LaunchReadyCallBack(Script.getINSTANCE().getUpdates(i));
            } else discordRP.LaunchReadyCallBack(Script.getINSTANCE().getUpdates(0));
        } else {
            discordRP.LaunchReadyCallBack(Script.getINSTANCE().getUpdates(getCurrentDisplay()));
        }

        setRunning(true);

        if (runLoop == null || runLoop.isInterrupted()) {
            Launch.LOGGER.debug("Thread is not created, creating a new thread");
            runLoop = new Thread("RunLoop") {
                @Override
                public void run() {
                    if (Script.getINSTANCE().getSize() == 1) {
                        return;
                    }
                    switch (Script.getINSTANCE().getUpdateType()) {
                        case Loop:
                            if (forwardLoop()) return;
                            while (running) {
                                for (int i = 0; i < Script.getINSTANCE().getSize(); i++) {
                                    executeUpdate(Script.getINSTANCE().getUpdates(i));
                                    if (!running) return;
                                    if (!DynamicRP.isOnSystemTray) {
                                        int finalI = i >= Script.getINSTANCE().getSize() - 1 ? 0 : i + 1;
                                        updateDisplay(finalI);
                                    }
                                }

                            }
                            break;
                        case Stop:
                            if (forwardLoop()) return;
                        case Random:
                            while (running) {
                                int i = (int) (Math.random() * Script.getINSTANCE().getSize());
                                executeUpdate(Script.getINSTANCE().getUpdates(i));
                                currentDisplay = i;
                                if (!running) return;
                                if (!DynamicRP.isOnSystemTray)
                                    updateDisplay(i);
                            }
                            break;
                        case Reverse:
                            for (int i = 1; i < Script.getINSTANCE().getSize(); i++) {
                                if (executeIndex(i)) return;
                            }
                            while (running) {
                                for (int i = Script.getINSTANCE().getSize() - 2; i >= 0; i--) {
                                    executeUpdate(Script.getINSTANCE().getUpdates(i));
                                    currentDisplay = i;
                                    if (!running) return;
                                    if (!DynamicRP.isOnSystemTray) {
                                        int finalI = i == 0 ? 1 : i - 1;
                                        updateDisplay(finalI);
                                    }
                                }
                                for (int i = 0; i < Script.getINSTANCE().getSize(); i++) {
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
        setRunning(false);
        if (runLoop != null) {
            runLoop.interrupt();
        }
    }

    private static boolean executeIndex(int i) {
        executeUpdate(Script.getINSTANCE().getUpdates(i));
        currentDisplay = i;
        if (!running) return true;
        if (!DynamicRP.isOnSystemTray) {
            int finalI = i >= Script.getINSTANCE().getSize() - 1 ? i - 1 : i + 1;
            updateDisplay(finalI);
        }
        return false;
    }

    private static boolean forwardLoop() {
        for (int i = 1; i < Script.getINSTANCE().getSize(); i++) {
            executeUpdate(Script.getINSTANCE().getUpdates(i));
            currentDisplay = i;
            if (!running) return true;
            if (!DynamicRP.isOnSystemTray) {
                int finalI = i >= Script.getINSTANCE().getSize() - 1 ? 0 : i + 1;
                updateDisplay(finalI);
            }
        }
        return false;
    }

    /**
     * Shut down DiscordRP, this method must be called to stop displaying the RP
     */
    public static void closeCallBack() {
        shutDownRP();
        terminate();
    }

    public static void shutDownRP(){
        discordRP.shutdown();
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
        Settings.saveSettingToFile();
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

    public static void setRunning(boolean running) {
        RunLoopManager.running = running;
        try{
            ApplicationTray.updatePopupMenu();
        } catch (Exception e){
            Launch.LOGGER.error("Error while updating the popup menu" + e);
        }
    }
    private static void updateDisplay(int finalI) {
        if(DynamicRP.isOnSystemTray || Settings.getINSTANCE().isShutDownInterfaceWhenTray() || Launch.isLaunchedUsingStartLaunch) return;
        Platform.runLater(() -> LoadingController.callBackController.updateCurrentDisplay(Script.getINSTANCE().getUpdates(finalI)));
    }

    public static boolean isRunning() {
        return running;
    }
}