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
        // gets the java bin so we can access it
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        // gets the current jar file
        final File currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            throw new FileNotAJarException();
        }

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        //run the builder and close current process
        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        RunLoopManager.onClose();
    }


}
