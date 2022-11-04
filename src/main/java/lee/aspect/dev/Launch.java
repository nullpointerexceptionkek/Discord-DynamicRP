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
     * This program is used to customize Discord rich perference via a interface by JavaFX
     * It connects to Discord via IPC by the libary {@link lee.aspect.dev.discordipc}
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
                String message = "\"Custom Discord RP\"\n"
                        + "It looks like you are trying to create\n"
                        + "mutiple instance of the program";
                JOptionPane.showMessageDialog(new JFrame(), message, "Application Running",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
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

    public static void unlock() {
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
