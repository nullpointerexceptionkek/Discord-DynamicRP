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
			Settings settings = new Settings();
			String readenum = reader.nextString();
			Settings.setTheme(Theme.valueOf(readenum));
			String discordapikey = reader.nextString();
			Settings.setDiscordAPIKey(discordapikey);

			
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
		writter.value(Settings.getTheme().name());
		writter.value(Settings.getDiscordAPIKey());

		
	}



}
