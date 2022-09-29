package lee.aspect.dev.discordrpc;

import java.util.Calendar;

import lee.aspect.dev.discordrpc.settings.Settings;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {
	
	private long created = -1;
	
	private boolean useStartTimeStamp = true;
	
	public static String discordName;
	
	
	public void LaunchReadyCallBack(){
		Calendar calendar = Calendar.getInstance();
		switch(Script.getTimestampmode()) {
		case "Default":
			useStartTimeStamp = true;
			this.created = System.currentTimeMillis();
			break;
		
		case "None":
			created = -1;
			break;
			
		case "Local time":
			useStartTimeStamp = true;
			calendar.set(Calendar.HOUR_OF_DAY, 0);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);
			this.created = calendar.getTimeInMillis();
			break;
		
		case "Custom":
			useStartTimeStamp = false;
			calendar.set(Calendar.HOUR_OF_DAY, 24);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	        calendar.set(Calendar.MILLISECOND, 0);
			this.created = calendar.getTimeInMillis();
			break;
		}
		

		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
			
			@Override
			public void apply(DiscordUser user) {
				discordName = user.username + "#" + user.discriminator;
				System.out.println("Welcome " + user.username + "#" + user.discriminator + ".");
			}
		}).build();
		DiscordRPC.discordInitialize(Settings.getDiscordAPIKey(), handlers, true);
	}
	
	
	
	public void shutdown() {
		DiscordRPC.discordShutdown();
	}
	

	public void update(String image, String imagetext, String smallimage, String smalltext, String firstLine, String secondLine) {
		DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(secondLine);
		if(image != null)
			presence.setBigImage(image, imagetext);
		if(smallimage!=null)
			presence.setSmallImage(smallimage, smalltext);
		presence.setDetails(firstLine);
		//presence.setSecrets("hello","google");
		if(created != -1) {
			if(useStartTimeStamp) {
				presence.setStartTimestamps(created);
			} 
			else {
				presence.setEndTimestamp(created);
			}
		}	
		//presence.setParty("party", 2, 3);
		DiscordRPC.discordUpdatePresence(presence.build());
		
	}
	
	
	
}

