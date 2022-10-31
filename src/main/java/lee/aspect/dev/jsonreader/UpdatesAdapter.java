package lee.aspect.dev.jsonreader;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.Settings;

import java.io.IOException;
import java.util.Objects;


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
