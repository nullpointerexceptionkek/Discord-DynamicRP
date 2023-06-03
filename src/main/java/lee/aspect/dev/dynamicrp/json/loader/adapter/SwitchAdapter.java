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
import lee.aspect.dev.dynamicrp.autoswitch.Switch;
import lee.aspect.dev.dynamicrp.autoswitch.SwitchManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class SwitchAdapter extends TypeAdapter<SwitchManager.Companion.LoadSwitchFromFile> {

    @Override
    public SwitchManager.Companion.LoadSwitchFromFile read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        try {
            reader.beginObject();
            ArrayList<Switch> switches = new ArrayList<>();
            while (reader.hasNext()) {
                String name = null;
                JsonToken token = reader.peek();
                if (token == JsonToken.NAME) {
                    name = reader.nextName();
                }
                if(Objects.requireNonNull(name).equals("version")){
                    reader.nextString();
                }
                if (Objects.requireNonNull(name).equals("SwitchScript")) {
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY) {
                        reader.beginObject();
                        JsonToken token1 = reader.peek();
                        String name1 = null;
                        Switch switchInfo = new Switch();
                        while (reader.hasNext()) {
                            if (token1 == JsonToken.NAME) {
                                name1 = reader.nextName();
                            }
                            switch (Objects.requireNonNull(name1)) {
                                case "file":
                                    switchInfo.setConfig(new File(reader.nextString()));
                                    break;
                                case "process":
                                    switchInfo.setCheckName(reader.nextString());
                                    break;
                            }
                        }
                        switches.add(switchInfo);
                        reader.endObject();
                    }
                    reader.endArray();
                }
            }
            reader.endObject();
            SwitchManager.Companion.LoadSwitchFromFile loadSwitchFromFile = new SwitchManager.Companion.LoadSwitchFromFile();
            loadSwitchFromFile.setSwitch(switches);
            return loadSwitchFromFile;

        } catch (Exception e) {
            e.printStackTrace();
            Launch.LOGGER.error("Invalid switch file detected");
            return null;
        }
    }

    @Override
    public void write(JsonWriter writter, SwitchManager.Companion.LoadSwitchFromFile file) throws IOException {
        if (file == null) {
            writter.nullValue();
            return;
        }
        writter.beginObject();
        writter.name("version").value(file.getCfgVer());
        writter.name("SwitchScript").beginArray();
        for (int i = 0; i < file.getSwitch().size(); i++) {
            writter.beginObject();
            writter.name("file").value(file.getSwitch().get(i).getConfig().getPath());
            writter.name("process").value(file.getSwitch().get(i).getCheckName());
            writter.endObject();
        }
        writter.endArray();
        writter.endObject();

    }


}
