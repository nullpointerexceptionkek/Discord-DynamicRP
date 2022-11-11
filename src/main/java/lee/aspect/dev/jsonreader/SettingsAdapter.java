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
                        Settings.setTheme(Settings.Theme.valueOf(reader.nextString()));
                        break;
                    case "MinimizeMode":
                        Settings.setMinimizeMode(Settings.MinimizeMode.valueOf(reader.nextString()));
                        break;
                    case "ShutDownInterfaceWhenTray":
                        Settings.setShutDownInterfaceWhenTray(reader.nextBoolean());
                        break;
                    case "StartTrayOnlyInterfaceClose":
                        Settings.setStartTrayOnlyInterfaceClose(reader.nextBoolean());
                        break;
                    case "NoAnimation":
                        Settings.setNoAnimation(reader.nextBoolean());
                        break;
                    case "StartLaunch":
                        Settings.setStartLaunch(reader.nextBoolean());
                        break;
                    case "Apikey":
                        Settings.setDiscordAPIKey(reader.nextString());
                        break;

                }
            }
            reader.endObject();

            return settings;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Invalid Settings");
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
        writter.value(Settings.getTheme().name());
        writter.name("MinimizeMode");
        writter.value(Settings.getMinimizeMode().name());
        writter.name("ShutDownInterfaceWhenTray");
        writter.value(Settings.isShutDownInterfaceWhenTray());
        writter.name("StartTrayOnlyInterfaceClose");
        writter.value(Settings.isStartTrayOnlyInterfaceClose());
        writter.name("NoAnimation");
        writter.value(Settings.isNoAnimation());
        writter.name("StartLaunch");
        writter.value(Settings.isStartLaunch());
        writter.name("Apikey");
        writter.value(Settings.getDiscordAPIKey());
        writter.endObject();

    }


}
