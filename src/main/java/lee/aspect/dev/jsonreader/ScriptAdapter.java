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
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;

import java.io.IOException;
import java.util.Objects;


public class ScriptAdapter extends TypeAdapter<Script> {

    @Override
    public Script read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        try {
            reader.beginObject();
            Script script = new Script();
            while (reader.hasNext()) {
                String name = null;
                JsonToken token = reader.peek();
                if (token == JsonToken.NAME) {
                    name = reader.nextName();
                }
                switch (Objects.requireNonNull(name)) {
                    case "APIkey":
                        script.setDiscordAPIKey(reader.nextString());
                        break;
                    case "TimeStampMode":
                        script.setTimestampmode(Script.TimeStampMode.valueOf(reader.nextString()));
                        break;
                    case "CustomTimeStamp":
                        script.setCustomTimestamp(reader.nextString());
                        break;
                    case "UpdateType":
                        script.setUpdateType(Script.UpdateType.valueOf(reader.nextString()));
                        break;
                    case "Updates":
                        reader.beginArray();
                        while (reader.peek() != JsonToken.END_ARRAY) {
                            reader.beginObject();
                            JsonToken token1 = reader.peek();
                            String updateid = null;
                            Updates updates = new Updates();
                            while (reader.hasNext()) {
                                if (token1 == JsonToken.NAME) {
                                    updateid = reader.nextName();
                                }
                                switch (Objects.requireNonNull(updateid)) {
                                    case "Delay":
                                        updates.setWait(reader.nextLong());
                                        break;
                                    case "Image":
                                        updates.setImage(reader.nextString());
                                        break;
                                    case "ImageText":
                                        updates.setImagetext(reader.nextString());
                                        break;
                                    case "SmallImage":
                                        updates.setSmallimage(reader.nextString());
                                        break;
                                    case "SmallText":
                                        updates.setSmalltext(reader.nextString());
                                        break;
                                    case "Firstline":
                                        updates.setFl(reader.nextString());
                                        break;
                                    case "SecondLine":
                                        updates.setSl(reader.nextString());
                                        break;
                                    case "Button1Text":
                                        updates.setButton1Text(reader.nextString());
                                        break;
                                    case "Button1Url":
                                        updates.setButton1Url(reader.nextString());
                                        break;
                                    case "Button2Text":
                                        updates.setButton2Text(reader.nextString());
                                        break;
                                    case "Button2Url":
                                        updates.setButton2Url(reader.nextString());
                                        break;
                                }
                            }
                            script.addUpdates(updates);
                            reader.endObject();
                        }
                        reader.endArray();
                        break;
                }

            }
            reader.endObject();
            return script;
        } catch (Exception e) {
            e.printStackTrace();
            Launch.LOGGER.error("Invalid File Config File detected");
            return null;
        }


    }

    @Override
    public void write(JsonWriter writter, Script tu) throws IOException {
        if (tu == null || tu.getSize() == 0) {
            writter.nullValue();
            return;
        }
        writter.beginObject();
        writter.name("APIkey").value(UpdateManager.SCRIPT.getDiscordAPIKey());
        writter.name("TimeStampMode");
        writter.value(UpdateManager.SCRIPT.getTimestampmode().name());
        writter.name("CustomTimeStamp");
        writter.value(UpdateManager.SCRIPT.getCustomTimestamp());
        writter.name("UpdateType");
        writter.value(UpdateManager.SCRIPT.getUpdateType().name());
        writter.name("Updates");
        writter.beginArray();
        //time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl
        for (int i = 0; i < tu.getSize(); i++) {
            writter.beginObject();
            writter.name("Delay");
            writter.value(tu.getUpdates(i).getWait());
            writter.name("Image");
            writter.value(tu.getUpdates(i).getImage());
            writter.name("ImageText");
            writter.value(tu.getUpdates(i).getImagetext());
            writter.name("SmallImage");
            writter.value(tu.getUpdates(i).getSmallimage());
            writter.name("SmallText");
            writter.value(tu.getUpdates(i).getSmalltext());
            writter.name("Firstline");
            writter.value(tu.getUpdates(i).getFl());
            writter.name("SecondLine");
            writter.value(tu.getUpdates(i).getSl());
            writter.name("Button1Text");
            writter.value(tu.getUpdates(i).getButton1Text());
            writter.name("Button1Url");
            writter.value(tu.getUpdates(i).getButton1Url());
            writter.name("Button2Text");
            writter.value(tu.getUpdates(i).getButton2Text());
            writter.name("Button2Url");
            writter.value(tu.getUpdates(i).getButton2Url());
            writter.endObject();
        }
        writter.endArray();
        writter.endObject();

    }


}
