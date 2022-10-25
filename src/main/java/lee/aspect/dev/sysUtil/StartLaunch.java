package lee.aspect.dev.sysUtil;

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

        System.out.println(STARTUPDIR);

        if (!CDRP.exists()) {
            CDRP.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(CDRP);
        //"start \"\" javaw -jar " + currentJar + " --StartLaunch"
        outputStream.write(("start \"\" javaw -jar " + currentJar + " --StartLaunch").getBytes());
        outputStream.flush();
        outputStream.close();


    }

    public static boolean isOnWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }

    public static boolean isBatCreated() {
        return CDRP.exists();
    }

    public static void deleteBat() {
        if (isBatCreated()) CDRP.delete();
    }


}
