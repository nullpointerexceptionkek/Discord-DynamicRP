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

package lee.aspect.dev.cdiscordrp.util.system;

import lee.aspect.dev.cdiscordrp.exceptions.FileNotAJarException;
import lee.aspect.dev.cdiscordrp.exceptions.UnsupportedOSException;

import java.io.*;
import java.net.URISyntaxException;


public class StartLaunch {

    private static final File STARTUPDIR_WINDOWS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
    private static final File STARTUPDIR_LINUX = new File(System.getProperty("user.home") + "/.config/autostart/");
    private static final File STARTUPDIR_MAC = new File(System.getProperty("user.home") + "/Library/LaunchAgents/");

    private static final String APP_NAME = "CDiscordRP";
    private static final String APP_SCRIPT_WINDOWS = APP_NAME + ".bat";
    private static final String APP_SCRIPT_LINUX = APP_NAME + ".desktop";
    private static final String APP_SCRIPT_MAC = APP_NAME + ".plist";

    public static void createStartupScript() throws IOException, UnsupportedOSException, FileNotAJarException, URISyntaxException {
        final File currentJar = new File(StartLaunch.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!currentJar.getName().endsWith(".jar")) {
            throw new FileNotAJarException();
        }

        if (isOnWindows()) {
            File batFile = new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS);
            try (PrintWriter writer = new PrintWriter(new FileWriter(batFile))) {
                writer.println("start \"\" javaw -jar " + currentJar + " --StartLaunch");
            }
        } else if (isOnLinux()) {
            File scriptFile = new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX);
            try (PrintWriter writer = new PrintWriter(new FileWriter(scriptFile))) {
                writer.println("[Desktop Entry]");
                writer.println("Type=Application");
                writer.println("Name=CDiscordRP");
                writer.println("Exec=/usr/bin/java -jar " + currentJar + " --StartLaunch");
                writer.println("Terminal=false");
                writer.println("Version=1.0");
                writer.println("X-GNOME-Autostart-enabled=true");
            }
            scriptFile.setExecutable(true);
        } else if (isOnMac()) {
            File plistFile = new File(STARTUPDIR_MAC, APP_SCRIPT_MAC);
            try (PrintWriter writer = new PrintWriter(new FileWriter(plistFile))) {
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.println("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
                writer.println("<plist version=\"1.0\">");
                writer.println("<dict>");
                writer.println("    <key>Label</key>");
                writer.println("    <string>" + APP_NAME + "</string>");
                writer.println("    <key>ProgramArguments</key>");
                writer.println("    <array>");
                writer.println("        <string>java</string>");
                writer.println("        <string>-jar</string>");
                writer.println("        <string>" + currentJar + "</string>");
                writer.println("        <string>--StartLaunch</string>");
                writer.println("    </array>");
                writer.println("    <key>RunAtLoad</key>");
                writer.println("    <true/>");
                writer.println("</dict>");
                writer.println("</plist>");
            }
            plistFile.setExecutable(true);
        } else {
            throw new UnsupportedOSException("Start Launch currently only supports Windows, Linux, and macOS");
        }
    }



    public static boolean isOnWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }
    public static boolean isOnLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }

    public static boolean isOnMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }


    public static boolean isStartupScriptCreated() {
        if (isOnWindows()) {
            return new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS).exists();
        }
        if (isOnLinux()) {
            return new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX).exists();
        }
        if (isOnMac()) {
            return new File(STARTUPDIR_MAC, APP_SCRIPT_MAC).exists();
        }
        return false;

    }

    public static void deleteStartupScript() {
        if (isOnWindows()) {
            File batFile = new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS);
            if (batFile.exists()) {
                batFile.delete();
            }
        } else if (isOnLinux()) {
            File scriptFile = new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX);
            if (scriptFile.exists()) {
                scriptFile.delete();
            }
        } else if (isOnMac()) {
            File plistFile = new File(STARTUPDIR_MAC, APP_SCRIPT_MAC);
            if (plistFile.exists()) {
                plistFile.delete();
            }
        }
    }
    public static boolean isJar(){
        final File currentJar;
        try {
            currentJar = new File(StartLaunch.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            return false;
        }
        return currentJar.getName().endsWith(".jar");
    }
}


