package lee.aspect.dev.jsonreader;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lee.aspect.dev.discordrpc.Updates;


public class UpdatesAdapter extends TypeAdapter<Updates>{

	@Override
	public Updates read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
	         reader.nextNull();
	         return null;
	         }
		
		String v = reader.nextString();
		String[] parts = v.split(",");
		
		long wait = Long.parseLong(parts[0]);
		String image = parts[1];
		String imagetext = parts[2];
	    String smallimage = parts[3];
	    String smalltext = parts[4];
	    String F1 = parts[5];
	    String Sl = parts[6];
	    
		return new Updates(wait, image, imagetext,smallimage,smalltext,F1,Sl);
		
		
	}

	@Override
	public void write(JsonWriter writter, Updates value) throws IOException {
		if(value == null) {
			writter.nullValue();
			return;
		}
		
		writter.value(value.toString());
		
	}

}
