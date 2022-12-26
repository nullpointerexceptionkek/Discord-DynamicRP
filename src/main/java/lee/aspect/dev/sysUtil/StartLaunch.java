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

package lee.aspect.dev.sysUtil;

import lee.aspect.dev.Launch;
import lee.aspect.dev.sysUtil.exceptions.FileNotAJarException;
import lee.aspect.dev.sysUtil.exceptions.UnsupportedOSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class StartLaunch {

    private static final File STARTUPDIR = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");

    private static final File CDRP = new File(STARTUPDIR, "CDRP.bat");

    public static void CreateBat() throws IOException, UnsupportedOSException, FileNotAJarException, URISyntaxException {
        final File currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!currentJar.getName().endsWith(".jar")) {
            throw new FileNotAJarException();
        }

        if (!isOnWindows()) throw new UnsupportedOSException("Start Launch currently only support windows");

        Launch.LOGGER.debug(STARTUPDIR.toString());

        if (!CDRP.exists()) {
            CDRP.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(CDRP);
        //"start \"\" javaw -jar " + currentJar + " --StartLaunch"
        outputStream.write(("start \"\" javaw -jar " + currentJar + " --StartLaunch").getBytes());
        outputStream.flush();
        outputStream.close();


    }

    public static boolean isJar() {
        final File currentJar;
        try {
            currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            return false;
        }
        return currentJar.getName().endsWith(".jar");
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

    public static boolean isBatCreated() {
        return CDRP.exists();
    }

    public static void deleteBat() {
        if (isBatCreated()) CDRP.delete();
    }


}
