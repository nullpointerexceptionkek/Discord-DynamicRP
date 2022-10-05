package lee.aspect.dev.jsonreader;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.discordrpc.Script;
import lee.aspect.dev.discordrpc.Updates;


public class ScriptAdapter extends TypeAdapter<Script>{

	@Override
	public Script read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		}
		try {
			reader.beginObject();
			Script tu = new Script();
			while (reader.hasNext()) {
				String name = null;
				JsonToken token = reader.peek();
				if (token == JsonToken.NAME) {
					name = reader.nextName();
				}
				switch (name) {
					case "TimeStampMode":
						Script.setTimestampmode(reader.nextString());
						break;
					case "Updates":
						reader.beginArray();
						while(reader.peek() != JsonToken.END_ARRAY) {
							reader.beginObject();
							JsonToken token1 = reader.peek();
							String updateid = null;
							Updates updates = new Updates();
							while(reader.hasNext()) {
								if (token1 == JsonToken.NAME) {
									updateid = reader.nextName();
								}
								switch (updateid) {
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
							Script.addUpdates(updates);
							reader.endObject();
						}
						reader.endArray();
						break;
				}

			}
			reader.endObject();
			return tu;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Invalid File");
			return null;
		}
			
		
	}

	@Override
	public void write(JsonWriter writter, Script tu) throws IOException {
		if(tu == null || tu.getSize() == 0) {
			writter.nullValue();
			return;
		}
		writter.beginObject();
		writter.name("TimeStampMode");
		writter.value(Script.getTimestampmode());
		writter.name("Updates");
		writter.beginArray();
		//time, String image, String imagetext, String smallimage, String smalltext, String f1, String sl
		for(int i = 0; i < tu.getSize(); i ++) {
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
