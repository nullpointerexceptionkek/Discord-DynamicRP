package lee.aspect.dev.application;

import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;

import java.io.IOException;

public class LaunchManager {
	
	public static boolean isRunning = false;
	
	private static DiscordRP discordRP = new DiscordRP();
	
	private static UpdateManager upm;
	
	private static Thread runloop;

	private static int CURRENTDISPLAY = 0;
	
	public static void init() {
		FileManager.init();
		SettingManager.init();
		upm = new UpdateManager();
		
		
	}
	
	public static void initCallBack() {
		discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(0));
		isRunning = true;
		
	}
	
	public static void startUpdate() {
		if(runloop == null) {
			runloop = new Thread("RunLoop") {
				@Override
				public void run() {
					if (upm.getUpdates().getSize() == 1) {
						return;
					}
					for (int i = 1; i < upm.getUpdates().getSize(); i++) {
						if (!isRunning)
							return;
						excuteUpdate(upm.getUpdates().getUpdates(i));
						CURRENTDISPLAY=i;
					}
					while (isRunning) {
						for (int i = 0; i < upm.getUpdates().getSize(); i++) {
							if (!isRunning)
								return;
							excuteUpdate(upm.getUpdates().getUpdates(i));
							CURRENTDISPLAY=i;
						}

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
	
	
	
	
	
	private static void excuteUpdate(Updates update) {
		if(update.getWait() == -1) {
			discordRP.update(update);
			return;
		}
		
		try {
			//Thread.sleep((update.getWait() <= 3000 )? 3000 : update.getWait());
			Thread.sleep(update.getWait());
			discordRP.update(update);
			System.out.println("Sented Update Request, trans: " + update);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public static void onClose() {
		SettingManager.saveSettingToFile();
		upm.saveScriptToFile();
		try {
			discordRP.shutdown();
		} catch (NullPointerException | IllegalStateException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void saveScripToFile() {
		upm.saveScriptToFile();
	}

	public static int getCURRENTDISPLAY() {
		return CURRENTDISPLAY;
	}
}
