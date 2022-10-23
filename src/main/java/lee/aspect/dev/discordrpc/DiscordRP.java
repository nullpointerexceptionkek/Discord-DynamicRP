package lee.aspect.dev.discordrpc;

import java.util.Calendar;

import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordipc.IPCClient;
import lee.aspect.dev.discordipc.IPCListener;
import lee.aspect.dev.discordipc.entities.Callback;
import lee.aspect.dev.discordipc.entities.RichPresence;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.settings.Settings;


public class DiscordRP {
	
	private long created = -1;
	
	private boolean useStartTimeStamp = true;

	private long current;
	
	public static String discordName;

	public static IPCClient client;

	public static Callback callback;

	public static boolean autoReconnect = true;
	
	
	public void LaunchReadyCallBack(Updates updates) throws NoDiscordClientException {
		Calendar calendar = Calendar.getInstance();
		switch(Script.getTimestampmode()) {
			case applaunch:
				useStartTimeStamp = true;
				this.created = System.currentTimeMillis();
				break;
			case none:
				created = -1;
				break;
			
			case current:
				useStartTimeStamp = true;
				calendar.set(Calendar.HOUR_OF_DAY, 0);
	        	calendar.set(Calendar.MINUTE, 0);
	        	calendar.set(Calendar.SECOND, 0);
	        	calendar.set(Calendar.MILLISECOND, 0);
				this.created = calendar.getTimeInMillis();
				break;
		
			case CDFromDayEnd:
				useStartTimeStamp = false;
				calendar.set(Calendar.HOUR_OF_DAY, 24);
	        	calendar.set(Calendar.MINUTE, 0);
	        	calendar.set(Calendar.SECOND, 0);
	        	calendar.set(Calendar.MILLISECOND, 0);
				this.created = calendar.getTimeInMillis();
				break;
		}
		created = (long) Math.floor(created/1000);
		current = (long) Math.floor(System.currentTimeMillis()/1000);

		/**
		 * offical discord ipc doc
		 * https://discord.com/developers/docs/topics/rpc#rpc
		 */

		callback = new Callback();
		client = new IPCClient(Long.valueOf(Settings.getDiscordAPIKey())); // your client id
		client.setListener(new IPCListener() {
			@Override
			public void onReady(IPCClient client) {
				RichPresence.Builder builder = new RichPresence.Builder();
				builder.setState(updates.getSl())
						.setDetails(updates.getFl())
						.setLargeImage(updates.getImage(), updates.getImagetext())
						.setSmallImage(updates.getSmallimage(), updates.getSmalltext())
						.setButton1Text(updates.getButton1Text())
						.setButton1Url(updates.getButton1Url())
						.setButton2Text(updates.getButton2Text())
						.setButton2Url(updates.getButton2Url());
				if(!(created == -1)) {
					if(created > current) {
						builder.setEndTimestamp(created);
					} else {
						builder.setStartTimestamp(created);
					}
				}
				client.sendRichPresence(builder.build(),callback);
			}
		});

		client.connect();
	}



	public void shutdown() {
		client.close();
	}


	public void update(Updates updates){
		try {
			RichPresence.Builder builder = new RichPresence.Builder();
			builder.setState(updates.getSl())
					.setDetails(updates.getFl())
					.setLargeImage(updates.getImage(), updates.getImagetext())
					.setSmallImage(updates.getSmallimage(), updates.getSmalltext())
					.setButton1Text(updates.getButton1Text())
					.setButton1Url(updates.getButton1Url())
					.setButton2Text(updates.getButton2Text())
					.setButton2Url(updates.getButton2Url());
			if(!(created == -1)) {
				if(created > current) {
					builder.setEndTimestamp(created);
				} else {
					builder.setStartTimestamp(created);
				}
			}
			client.sendRichPresence(builder.build(), callback);
		}	catch (IllegalStateException|NullPointerException e){
			if(autoReconnect && RunLoopManager.isRunning) {
				client = new IPCClient(Long.valueOf(Settings.getDiscordAPIKey())); // your client id
				client.setListener(new IPCListener() {
					@Override
					public void onReady(IPCClient client) {
						RichPresence.Builder builder = new RichPresence.Builder();
						builder.setState(updates.getSl())
								.setDetails(updates.getFl())
								.setLargeImage(updates.getImage(), updates.getImagetext())
								.setSmallImage(updates.getSmallimage(), updates.getSmalltext())
								.setButton1Text(updates.getButton1Text())
								.setButton1Url(updates.getButton1Url())
								.setButton2Text(updates.getButton2Text())
								.setButton2Url(updates.getButton2Url());
						if(!(created == -1)) {
							if(created > current) {
								builder.setEndTimestamp(created);
							} else {
								builder.setStartTimestamp(created);
							}
						}
						client.sendRichPresence(builder.build(),callback);
					}
				});

				try {
					client.connect();
				} catch (NoDiscordClientException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		}


	
	
}

