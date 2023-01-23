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

package lee.aspect.dev.cdiscordrp.json.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager;
import lee.aspect.dev.cdiscordrp.application.core.Script;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.application.core.Updates;
import lee.aspect.dev.cdiscordrp.json.loader.adapter.ScriptAdapter;
import lee.aspect.dev.cdiscordrp.json.loader.adapter.SettingsAdapter;
import lee.aspect.dev.cdiscordrp.json.loader.adapter.SwitchAdapter;
import lee.aspect.dev.cdiscordrp.json.loader.adapter.UpdatesAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static lee.aspect.dev.cdiscordrp.manager.DirectoryManager.getRootDir;


public class FileManager {
    private static Gson gson;

    public static void init() {
        if (!getRootDir().exists()) {
            //TODO: DO smth here, not sure rn
        }

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Script.class, new ScriptAdapter());
        builder.registerTypeAdapter(Settings.class, new SettingsAdapter());
        builder.registerTypeAdapter(Updates.class, new UpdatesAdapter());
        builder.registerTypeAdapter(SwitchManager.Companion.LoadSwitchFromFile.class, new SwitchAdapter());
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
            throw new RuntimeException(e);
        }

    }

    public static String toGson(Object o) {
        return gson.toJson(o);

    }

    public static <T> T readFromJson(String json, Class<T> c) {
        return gson.fromJson(json, c);
    }


}
