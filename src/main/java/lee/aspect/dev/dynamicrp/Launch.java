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

package lee.aspect.dev.dynamicrp;

import lee.aspect.dev.dynamicrp.application.core.DynamicRP;
import lee.aspect.dev.dynamicrp.application.core.Script;
import lee.aspect.dev.dynamicrp.application.core.Settings;
import lee.aspect.dev.dynamicrp.autoswitch.SwitchManager;
import lee.aspect.dev.dynamicrp.exceptions.ExceptionHandler;
import lee.aspect.dev.dynamicrp.json.loader.FileManager;
import lee.aspect.dev.dynamicrp.language.LanguageManager;
import lee.aspect.dev.dynamicrp.manager.DirectoryManager;
import lee.aspect.dev.dynamicrp.system.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class checks whether if the application is already launched and add shutdown hook;
 *
 * @author lee
 */
public class Launch {
    public final static String VERSION = "1.0.1";
    public final static String AUTHOR = "lee";
    public final static String NAME = "DynamicRP";

    public final static String LICENSE = "MIT License";

    public static final Logger LOGGER = LoggerFactory.getLogger(Launch.class);
    public static boolean isOnIDE = false;

    public static boolean isLaunchedUsingStartLaunch = false;

    public static File runtimeDir;

    public static File runtime;


    private Launch() {
    }

    /**
     * Redirect main Class to {@link DynamicRP Launch}
     * This program is used to customize Discord rich preference via interface by JavaFX
     * It connects to Discord via IPC by the library {@link lee.aspect.dev.dynamicrp.discordipc}
     *
     * @author Aspect
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        if (!DirectoryManager.initDirectory()) {
            DynamicRP.LaunchSetUpDialog(args);
            return;
        }
        try {
            runtimeDir = new File(DirectoryManager.getROOT_DIR(), "runtime");
            if (!runtimeDir.exists()) runtimeDir.mkdir();
            runtime = new File(runtimeDir, "port.rnt");


            if (runtime.exists()) {
                try {
                    int port;
                    BufferedReader br = new BufferedReader(new FileReader(runtime));
                    port = Integer.parseInt(br.readLine());
                    String message = "DynamicRP.ShowCurrentInstance";
                    Socket socket = new Socket("localhost", port);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(message.getBytes());
                    outputStream.close();
                    socket.close();
                    br.close();
                    System.exit(0);
                } catch (IOException e) {
                    //port failed. start a new instance is fine
                }
            }

            isOnIDE = !SystemHandler.StartLaunch.isJar();
            if (isOnIDE) {
                LOGGER.info("This program is running on or not build into a jar file, The following features will not work.");
                LOGGER.info("Automated restart application, Create start launch script");
                LOGGER.info("The environment variable might not be updated unless you restart the IDE.");
            }

            initManagers();
            ServerSocket serverSocket = new ServerSocket(0);
            BufferedWriter bw = new BufferedWriter(new FileWriter(runtime));
            Launch.LOGGER.info("DynamicRP is running on port " + serverSocket.getLocalPort());
            bw.write(Integer.toString(serverSocket.getLocalPort()));
            bw.close();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> FileManager.deleteFolder(runtimeDir)));

            new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead = inputStream.read(buffer);
                        String message = new String(buffer, 0, bytesRead);
                        if (message.equals("DynamicRP.ShowCurrentInstance")) {
                            DynamicRP.popUpWindow();
                        }
                        inputStream.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String arg : args) {
            if (arg.contains("--StartLaunch")) {
                isLaunchedUsingStartLaunch = true;
                DynamicRP.LaunchSilently();
                return;
            } else if (arg.contains("--unsetenv")) {
                System.out.println("Unset environment variable");
                DirectoryManager.deleteDirectoryEnvironmentVar();
                return;
            } else if (arg.contains("--forceunsetbash")) {
                System.out.println("Unset environment variable - bashrc and zshrc");
                DirectoryManager.unsetEnvBash();
            }
        }
        DynamicRP.Launch(args);
    }

    public static void initManagers() {
        FileManager.init();
        Settings.loadKeyFromJson();
        LanguageManager.init();
        Script.loadScriptFromJson();
        SwitchManager.loadFromFile();
    }


}
