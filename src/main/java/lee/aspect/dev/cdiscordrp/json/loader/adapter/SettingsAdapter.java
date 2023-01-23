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

package lee.aspect.dev.cdiscordrp.json.loader.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.cdiscordrp.Launch;
import lee.aspect.dev.cdiscordrp.application.core.Settings;
import lee.aspect.dev.cdiscordrp.language.Languages;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class SettingsAdapter extends TypeAdapter<Settings> {

    @Override
    public Settings read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
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
                    case "CDiscordRP":
                        reader.nextString();
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
        writter.beginObject();
        writter.name("CDiscordRP").value(Launch.VERSION);
        writter.name("loadedCfg").value(Settings.getINSTANCE().getLoadedConfig().getPath());
        writter.name("AutoSwitch").value(Settings.getINSTANCE().isAutoSwitch());
        writter.name("theme").value(Settings.getINSTANCE().getTheme().name());
        writter.name("MinimizeMode").value(Settings.getINSTANCE().getMinimizeMode().name());
        writter.name("ShutDownInterfaceWhenTray").value(Settings.getINSTANCE().isShutDownInterfaceWhenTray());
        writter.name("StartTrayOnlyInterfaceClose").value(Settings.getINSTANCE().isStartTrayOnlyInterfaceClose());
        writter.name("NoAnimation").value(Settings.getINSTANCE().isNoAnimation());
        writter.name("StartLaunch").value(Settings.getINSTANCE().isStartLaunch());
        writter.name("Lang").value(Settings.getINSTANCE().getLang().name());
        writter.endObject();

    }


}
