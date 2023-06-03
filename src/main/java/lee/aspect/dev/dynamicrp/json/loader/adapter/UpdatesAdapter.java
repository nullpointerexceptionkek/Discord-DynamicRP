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
import lee.aspect.dev.dynamicrp.application.core.Updates;

import java.io.IOException;


public class UpdatesAdapter extends TypeAdapter<Updates> {


    @Override
    public void write(JsonWriter jsonWriter, Updates updates) throws IOException {
        if (updates == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();
        jsonWriter.name("wait").value(updates.getWait());
        jsonWriter.name("image").value(updates.getImage());
        jsonWriter.name("imagetext").value(updates.getImagetext());
        jsonWriter.name("smallimage").value(updates.getSmallimage());
        jsonWriter.name("smalltext").value(updates.getSmalltext());
        jsonWriter.name("firstline").value(updates.getFl());
        jsonWriter.name("secondline").value(updates.getSl());
        jsonWriter.name("button1Text").value(updates.getButton1Text());
        jsonWriter.name("button1Url").value(updates.getButton1Url());
        jsonWriter.name("button2Text").value(updates.getButton2Text());
        jsonWriter.name("button2Url").value(updates.getButton2Url());
        jsonWriter.endObject();
    }

    @Override
    public Updates read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        Updates updates = new Updates();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "wait":
                    updates.setWait(jsonReader.nextLong());
                    break;
                case "image":
                    updates.setImage(jsonReader.nextString());
                    break;
                case "imagetext":
                    updates.setImagetext(jsonReader.nextString());
                    break;
                case "smallimage":
                    updates.setSmallimage(jsonReader.nextString());
                    break;
                case "smalltext":
                    updates.setSmalltext(jsonReader.nextString());
                    break;
                case "firstline":
                    updates.setFl(jsonReader.nextString());
                    break;
                case "secondline":
                    updates.setSl(jsonReader.nextString());
                    break;
                case "button1Text":
                    updates.setButton1Text(jsonReader.nextString());
                    break;
                case "button1Url":
                    updates.setButton1Url(jsonReader.nextString());
                    break;
                case "button2Text":
                    updates.setButton2Text(jsonReader.nextString());
                    break;
                case "button2Url":
                    updates.setButton2Url(jsonReader.nextString());
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return updates;
    }
}
