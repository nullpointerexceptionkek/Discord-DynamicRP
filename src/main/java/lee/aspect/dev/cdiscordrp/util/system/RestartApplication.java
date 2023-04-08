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

import lee.aspect.dev.cdiscordrp.application.core.RunLoopManager;
import lee.aspect.dev.cdiscordrp.application.core.Script;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager;
import lee.aspect.dev.cdiscordrp.exceptions.FileNotAJarException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class RestartApplication {

    /**
     * Restarts the current application.
     *
     * <p>This method uses the current jar file to start a new process that runs the application. The
     * current process is exited after calling the {@link RunLoopManager#onClose()} method to perform
     * necessary clean-up.
     * <p>
     * However, this method does not work if the application is started from an IDE or if the jar file
     * You should always make a exception handler to catch the exception and show a prompt to the user
     *
     * @throws URISyntaxException   if the current jar file cannot be found
     * @throws IOException          if an error occurs while starting the new process
     * @throws FileNotAJarException if the current file is not a jar file
     */
    public static void FullRestart() throws URISyntaxException, IOException, FileNotAJarException {
        try{
            RunLoopManager.shutDownRP();
        } catch (NullPointerException | IllegalStateException e) {
            //shutDownRP() will throw either NullPointException or IllegalStateException if it is already closed
            //the purpose if this is obviously a way to notify if the RP is already closed.
            //There is no harm on ignoring this exception
        }
        Settings.saveSettingToFile();
        Script.saveScriptToFile();
        SwitchManager.saveToFile();
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!currentJar.getName().endsWith(".jar")) {
            throw new FileNotAJarException();
        }

        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
    }


}
