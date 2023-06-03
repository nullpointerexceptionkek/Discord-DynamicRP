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

package lee.aspect.dev.dynamicrp.json.loader.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.application.core.Settings;
import lee.aspect.dev.dynamicrp.language.Languages;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class SettingsAdapter extends TypeAdapter<Settings> {

    @Override
    public Settings read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            Settings.setup();
            return null;
        }

        try {
            reader.beginObject();
            Settings settings = Settings.getINSTANCE();
            while (reader.hasNext()) {
                String name = null;
                JsonToken token = reader.peek();
                if (token == JsonToken.NAME) {
                    name = reader.nextName();
                }
                switch (Objects.requireNonNull(name)) {
                    case Launch.NAME:
                        reader.nextString();
                        break;
                    case "window":
                        reader.beginArray();
                        settings.setWindowHeight(reader.nextDouble());
                        settings.setWindowWidth(reader.nextDouble());
                        reader.endArray();
                        break;
                    case "loadedCfg":
                        settings.setLoadedConfig(new File(reader.nextString()));
                        break;
                    case "AutoSwitch":
                        settings.setAutoSwitch(reader.nextBoolean());
                        break;
                    case "theme":
                        settings.setTheme(Settings.Theme.valueOf(reader.nextString()));
                        break;
                    case "MinimizeMode":
                        settings.setMinimizeMode(Settings.MinimizeMode.valueOf(reader.nextString()));
                        break;
                    case "ShutDownInterfaceWhenTray":
                        settings.setShutDownInterfaceWhenTray(reader.nextBoolean());
                        break;
                    case "StartTrayOnlyInterfaceClose":
                        settings.setStartTrayOnlyInterfaceClose(reader.nextBoolean());
                        break;
                    case "NoAnimation":
                        settings.setNoAnimation(reader.nextBoolean());
                        break;
                    case "StartLaunch":
                        settings.setStartLaunch(reader.nextBoolean());
                        break;
                    case "Lang":
                        settings.setLang(Languages.valueOf(reader.nextString()));
                        break;

                }
            }
            reader.endObject();

            return settings;
        } catch (Exception e) {
            Launch.LOGGER.error("Invalid Settings file detected");
            Settings.setup();
            return null;
        }


    }

    @Override
    public void write(JsonWriter writter, Settings settings) throws IOException {
        if (settings == null) {
            writter.nullValue();
            return;
        }
        Settings cachedSettings = Settings.getINSTANCE();
        writter.beginObject();
        writter.name(Launch.NAME).value(Launch.VERSION);
        writter.name("window").beginArray().value(cachedSettings.getWindowHeight()).value(cachedSettings.getWindowWidth()).endArray();
        writter.name("loadedCfg").value(cachedSettings.getLoadedConfig().getPath());
        writter.name("AutoSwitch").value(cachedSettings.isAutoSwitch());
        writter.name("theme").value(cachedSettings.getTheme().name());
        writter.name("MinimizeMode").value(cachedSettings.getMinimizeMode().name());
        writter.name("ShutDownInterfaceWhenTray").value(cachedSettings.isShutDownInterfaceWhenTray());
        writter.name("StartTrayOnlyInterfaceClose").value(cachedSettings.isStartTrayOnlyInterfaceClose());
        writter.name("NoAnimation").value(cachedSettings.isNoAnimation());
        writter.name("StartLaunch").value(cachedSettings.isStartLaunch());
        writter.name("Lang").value(cachedSettings.getLang().name());
        writter.endObject();

    }


}
