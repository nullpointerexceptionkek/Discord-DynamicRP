/*
 * 2022-
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

package lee.aspect.dev.cdiscordrp;

import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC;
import lee.aspect.dev.cdiscordrp.application.core.Script;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager;
import lee.aspect.dev.cdiscordrp.exceptions.ExceptionHandler;
import lee.aspect.dev.cdiscordrp.json.loader.FileManager;
import lee.aspect.dev.cdiscordrp.language.LanguageManager;
import lee.aspect.dev.cdiscordrp.manager.DirectoryManager;
import lee.aspect.dev.cdiscordrp.util.system.StartLaunch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * This class checks whether if the application is already launched and add shutdown hook;
 *
 * @author lee
 */
public class Launch {
    public final static String VERSION = "Pre 0.5.0";
    public final static String AUTHOR = "lee";
    public final static String NAME = "CDiscordRP";

    public static final Logger LOGGER = LoggerFactory.getLogger(Launch.class);
    public static boolean isOnIDE = false;
    public static File runtime;
    public static FileChannel channel;
    public static FileLock lock;

    private Launch() {
    }

    /**
     * Redirect main Class to {@link CustomDiscordRPC Launch}
     * This program is used to customize Discord rich preference via interface by JavaFX
     * It connects to Discord via IPC by the library {@link lee.aspect.dev.cdiscordrp.discordipc}
     *
     * @author Aspect
     */
    public static void main(String[] args) {
        isOnIDE = !StartLaunch.isJar();
        if (isOnIDE) {
            LOGGER.info("This program is running on or not build into a jar file, The following features will not work.");
            LOGGER.info("Automated restart application, Create start launch script");
            LOGGER.info("The environment variable might not be updated unless you restart the IDE.");
        }

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        if (!DirectoryManager.isSetUp()) {
            CustomDiscordRPC.LaunchSetUpDialog(args);

            return;
        }
        init();
        try {
            runtime = new File(DirectoryManager.getRootDir(), "runtime");

            //if (!DirectoryManager.getRootDir().exists()) DirectoryManager.getRootDir().mkdir();

            if (runtime.exists()) runtime.delete();
            channel = new RandomAccessFile(runtime, "rw").getChannel();
            lock = channel.tryLock();

            if (lock == null) {
                channel.close();
                Object[] options = {"Yes, I want to start the application", "Quit Application"};
                String message = "\"Custom Discord RP\"\n"
                        + "It looks like you are trying to create\n"
                        + "multiple instance of the program\n"
                        + "Do you want to start the application anyway?";
                int result = JOptionPane.showOptionDialog(new JFrame(), message, "Application Running", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, null);
                if (result == JOptionPane.YES_OPTION) {
                    CustomDiscordRPC.Launch(args);
                } else {
                    System.exit(0);
                }
            }

            Thread shutdown = new Thread(Launch::unlock);

            Runtime.getRuntime().addShutdownHook(shutdown);
            for (String arg : args) {
                if (arg.contains("--StartLaunch")) {
                    CustomDiscordRPC.LaunchSilently();
                    return;
                }
            }
            CustomDiscordRPC.Launch(args);
        } catch (IOException e) {
            throw new RuntimeException("Could not start application", e);
        }

    }

    public static boolean unlock() {
        LOGGER.info("Author: " + AUTHOR + " Project: " + NAME + " " + VERSION);
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                return runtime.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void init() {
        FileManager.init();
        Settings.loadKeyFromJson();
        Script.loadScriptFromJson();
        LanguageManager.init();
        SwitchManager.loadFromFile();
    }

}
