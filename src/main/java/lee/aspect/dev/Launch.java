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

package lee.aspect.dev;

import lee.aspect.dev.application.CustomDiscordRPC;
import lee.aspect.dev.jsonreader.FileManager;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Launch {

    public static File f;
    public static FileChannel channel;
    public static FileLock lock;


    /**
     * Redirect main Class to {@link CustomDiscordRPC Launch}
     * This program is used to customize Discord rich preference via interface by JavaFX
     * It connects to Discord via IPC by the library {@link lee.aspect.dev.discordipc}
     *
     * @param args
     * @author Aspect
     */
    public static void main(String[] args) {
        try {
            f = new File(FileManager.getROOT_DIR(), "runtime");

            if (!FileManager.getROOT_DIR().exists()) FileManager.getROOT_DIR().mkdir();

            if (f.exists()) f.delete();
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();

            if (lock == null) {
                channel.close();
                Object[] options = { "Yes, I want to start the application", "Quit Application" };
                String message = "\"Custom Discord RP\"\n"
                        + "It looks like you are trying to create\n"
                        + "multiple instance of the program\n"
                        + "Do you want to start the application anyway?";
                int result =JOptionPane.showOptionDialog(new JFrame(), message, "Application Running", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options,null);
                if(result == JOptionPane.YES_OPTION) {
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
        try {
            if (lock != null) {
                lock.release();
                channel.close();
               return f.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
