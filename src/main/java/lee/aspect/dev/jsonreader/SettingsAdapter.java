package lee.aspect.dev.jsonreader;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.discordrpc.settings.Settings;
import lee.aspect.dev.discordrpc.settings.Theme;


public class SettingsAdapter extends TypeAdapter<Settings>{

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
				switch(name){
					case "Theme":
						Settings.setTheme(Theme.valueOf(reader.nextString()));
						break;
					case "Apikey":
						Settings.setDiscordAPIKey(reader.nextString());

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
		if(settings == null) {
			writter.nullValue();
			return;
		}
		writter.beginObject();
		writter.name("Theme");
		writter.value(Settings.getTheme().name());
		writter.name("Apikey");
		writter.value(Settings.getDiscordAPIKey());
		writter.endObject();
		
	}



}
