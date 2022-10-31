package lee.aspect.dev.jsonreader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class FileManager {
    private static final File ROOT_DIR = new File(System.getProperty("user.home") + "\\CustomDiscordRPC");
    private static Gson gson;

    public static void init() {
        System.out.println(ROOT_DIR);
        if (!ROOT_DIR.exists()) {
            ROOT_DIR.mkdir();
        }

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Script.class, new ScriptAdapter());
        builder.registerTypeAdapter(Settings.class, new SettingsAdapter());
        builder.registerTypeAdapter(Updates.class, new UpdatesAdapter());
        gson = builder.setPrettyPrinting().create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static boolean writeJsonTofile(File file, Object obj) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T readFromJson(File file, Class<T> c) {

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();

            return gson.fromJson(builder.toString(), c);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static File getROOT_DIR() {
        return ROOT_DIR;
    }

    public static String toGson(Object o){
        return gson.toJson(o);

    }

    public static <T> T readFromJson(String json, Class<T> c){
        return gson.fromJson(json, c);
    }


}
