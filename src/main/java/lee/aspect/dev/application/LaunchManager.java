package lee.aspect.dev.application;

import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;

public class LaunchManager {
	
	public static boolean isRunning = false;
	
	private static DiscordRP discordRP = new DiscordRP();
	
	private static UpdateManager upm;
	
	private static Thread runloop;
	
	public static void init() {
		FileManager.init();
		SettingManager.init();
		upm = new UpdateManager();
		
		
	}
	
	public static void initCallBack() {
		discordRP.LaunchReadyCallBack();
		isRunning = true;
		
	}
	
	public static void startUpdate() {
		if(runloop == null) {
			runloop = new Thread("RunLoop") {
				@Override
				public void run() {
					try {
						if (upm.getUpdates().getSize() == 1) {
							excuteUpdate(upm.getUpdates().getUpdates(0));
							//DiscordRPC.discordRunCallbacks();
							return;
						}


						while (isRunning) {
							for (int i = 0; i < upm.getUpdates().getSize(); i++) {
								if (!isRunning)
									return;
								excuteUpdate(upm.getUpdates().getUpdates(i));
								//DiscordRPC.discordRunCallbacks();
							}

						}
					}catch(NoDiscordClientException e) {
						e.printStackTrace();
					}
				}
			};
			runloop.start();
			return;
		}
	}
	
	public static void closeCallBack() {
		System.out.println();
		discordRP.shutdown();
		isRunning = false;
	}
	
	
	
	
	
	private static void excuteUpdate(Updates update) throws NoDiscordClientException {
		System.out.println("Sented Update Request, trans: " + update);
		if(update.getWait() == -1) {
			discordRP.update(update.getImage(),update.getImagetext(),update.getSmallimage() 
					,update.getSmalltext() ,update.getFl(), update.getSl());
			return;
		}
		
		try {
			//Thread.sleep((update.getWait() <= 3000 )? 3000 : update.getWait());
			Thread.sleep(update.getWait());
			discordRP.update(update.getImage(),update.getImagetext(),update.getSmallimage() 
					,update.getSmalltext() ,update.getFl(), update.getSl());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public static void onClose() {
		SettingManager.saveSettingToFile();
		upm.saveScriptToFile();
		discordRP.shutdown();
		System.exit(0);
	}
	
	public static void saveScripToFile() {
		upm.saveScriptToFile();
	}
	
	
	

	
}
