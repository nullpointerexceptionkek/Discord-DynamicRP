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
			Script tu = new Script();
			Script.setTimestampmode(reader.nextString());
			String v = reader.nextString();
			String[] parts = v.split("-");
			for(String u : parts) {
				String[] uParts = u.split(", ");
				long wait = Long.parseLong(uParts[0]);
				String image = uParts[1];
				String imagetext = uParts[2];
			    String smallimage = uParts[3];
			    String smalltext = uParts[4];
			    String F1 = uParts[5];
			    String Sl = uParts[6];
			    Script.addUpdates(new Updates(wait, image, imagetext,smallimage,smalltext,F1,Sl));
			}
			return tu;
		} catch (Exception e) {
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
		writter.value(Script.getTimestampmode());
		writter.value(tu.toString());

		
	}



}
