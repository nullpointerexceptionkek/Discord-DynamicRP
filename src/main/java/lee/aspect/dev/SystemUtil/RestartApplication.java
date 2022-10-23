package lee.aspect.dev.SystemUtil;

import lee.aspect.dev.SystemUtil.Exceptions.CannotRestartException;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordrpc.settings.SettingManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class RestartApplication {

    public static void FullRestart() throws URISyntaxException, IOException, CannotRestartException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if(!currentJar.getName().endsWith(".jar")){
            throw new CannotRestartException();
        }

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        RunLoopManager.onClose();
    }


}
