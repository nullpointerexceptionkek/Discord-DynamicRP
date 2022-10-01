package lee.aspect.dev.discordrpc;

import java.time.OffsetDateTime;
import java.util.Calendar;

import lee.aspect.dev.discordipc.IPCClient;
import lee.aspect.dev.discordipc.IPCListener;
import lee.aspect.dev.discordipc.entities.RichPresence;
import lee.aspect.dev.discordipc.entities.User;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.settings.Settings;


public class DiscordRP {
	
	private long created = -1;
	
	private boolean useStartTimeStamp = true;
	
	public static String discordName;

	public static IPCClient client;
	
	
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

	try {
		client = new IPCClient(Long.valueOf(Settings.getDiscordAPIKey())); // your client id
		client.setListener(new IPCListener() {
			@Override
			public void onReady(IPCClient client) {
				RichPresence.Builder builder = new RichPresence.Builder();
				builder.setState("State")
						.setDetails("Details")
						.setButton1Text("Discord")
						.setButton1Url("https://discord.com")
						.setButton2Text("Google")
						.setButton2Url("https://google.com")
						.setStartTimestamp(OffsetDateTime.now())
						.setLargeImage("canary-large", "Discord Canary")
						.setSmallImage("ptb-small", "Discord PTB");
				client.sendRichPresence(builder.build());
			}
		});
		client.connect();
	} catch (NoDiscordClientException e) {
		throw new RuntimeException(e);
	}

	}
	
	
	
	public void shutdown() {

	}
	

	public void update(String image, String imagetext, String smallimage, String smalltext, String firstLine, String secondLine){
		RichPresence.Builder builder = new RichPresence.Builder();
		builder.setState(secondLine)
				.setDetails(firstLine)
				.setButton1Text("Discord")
				.setButton1Url("https://discord.com")
				.setButton2Text("Google")
				.setButton2Url("https://google.com")
				.setStartTimestamp(OffsetDateTime.now())
				.setLargeImage(image, imagetext)
				.setSmallImage(smallimage, smalltext);
		client.sendRichPresence(builder.build());
	}
	
	
	
}

