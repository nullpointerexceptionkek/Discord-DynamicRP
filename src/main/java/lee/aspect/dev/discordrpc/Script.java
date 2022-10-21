package lee.aspect.dev.discordrpc;

import lee.aspect.dev.discordrpc.settings.options.TimeStampMode;

import java.util.ArrayList;

public class Script {
	private static ArrayList<Updates> totalupdates;
	
	private static TimeStampMode timestampmode = TimeStampMode.applaunch;
	
	public Script() {
		totalupdates = new ArrayList<Updates>();
	}
	
	public static void setTimestampmode(TimeStampMode timestampmode) {
		Script.timestampmode = timestampmode;
	}
	
	public static TimeStampMode getTimestampmode() {
		return timestampmode;
	}
	
	
	public static ArrayList<Updates> getTotalupdates() {
		return totalupdates;
	}
	
	
	public static void setTotalupdates(ArrayList<Updates> u) {
		totalupdates = u;
	}
	
	public static void addUpdates(Updates... updates) {
		for(Updates u : updates) {
			totalupdates.add(u);
		}

	}
	public static void setUpdates(Updates u, int index) {
		totalupdates.set(index, u);
	}
	
	public void removeUpdates(int list) {
		totalupdates.remove(list);

	}
	
	public Updates getUpdates(int list) {
		return totalupdates.get(list);
	}
	
	public int getSize() {
		return totalupdates.size();
		
	}
	
	public static Script fromTotalUpdates() {
		return new Script();
	}
	
	@Override
	public String toString() {
		
		String ts = "";
		
		for(Updates u: totalupdates) {
			ts += u + "-";
		}
		
		return ts;
		
	}
	
}
