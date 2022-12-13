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

package lee.aspect.dev.jsonreader;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.Launch;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.discordrpc.settings.Settings;

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
            Settings settings = new Settings();
            while (reader.hasNext()) {
                String name = null;
                JsonToken token = reader.peek();
                if (token == JsonToken.NAME) {
                    name = reader.nextName();
                }
                switch (Objects.requireNonNull(name)) {
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
                    case "Apikey":
                        settings.setDiscordAPIKey(reader.nextString());
                        break;

                }
            }
            reader.endObject();

            return settings;
        } catch (Exception e) {
            e.printStackTrace();
            Launch.LOGGER.error("Invalid Settings file detected");
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
        writter.name("theme");
        writter.value(SettingManager.SETTINGS.getTheme().name());
        writter.name("MinimizeMode");
        writter.value(SettingManager.SETTINGS.getMinimizeMode().name());
        writter.name("ShutDownInterfaceWhenTray");
        writter.value(SettingManager.SETTINGS.isShutDownInterfaceWhenTray());
        writter.name("StartTrayOnlyInterfaceClose");
        writter.value(SettingManager.SETTINGS.isStartTrayOnlyInterfaceClose());
        writter.name("NoAnimation");
        writter.value(SettingManager.SETTINGS.isNoAnimation());
        writter.name("StartLaunch");
        writter.value(SettingManager.SETTINGS.isStartLaunch());
        writter.name("Apikey");
        writter.value(SettingManager.SETTINGS.getDiscordAPIKey());
        writter.endObject();

    }


}
