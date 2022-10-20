package lee.aspect.dev.StartUpLaunch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class StartLaunch {

    public StartLaunch() throws URISyntaxException, IOException {

        var path = new File(StartLaunch.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();


        File startupbat = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
        System.out.println(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");

        File file = new File(startupbat,"cdrp.bat");
        if(!startupbat.exists()) {
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(new String("start \"\" javaw -jar --module-path \"C:\\Program Files\\Java\\JavaFX\\openjfx-18.0.2_windows-x64_bin-sdk\\javafx-sdk-18.0.2\\lib\" --add-modules javafx.controls,javafx.fxml D:\\lee17\\Documents\\customdiscordrpc\\FxGui.jar").getBytes());
        outputStream.flush();
        outputStream.close();



    }

}
