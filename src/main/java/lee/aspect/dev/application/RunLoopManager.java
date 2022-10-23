package lee.aspect.dev.application;

import javafx.application.Platform;
import lee.aspect.dev.application.Gui.LoadingController;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.DiscordRP;
import lee.aspect.dev.discordrpc.UpdateManager;
import lee.aspect.dev.discordrpc.Updates;
import lee.aspect.dev.discordrpc.settings.SettingManager;
import lee.aspect.dev.jsonreader.FileManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RunLoopManager {

	public static boolean isRunning = false;

	private static final DiscordRP discordRP = new DiscordRP();

	private static UpdateManager upm;

	private static Thread runloop;

	private static int CURRENTDISPLAY = 0;

	/**
	 * Create an instance of UpdateManager and init setting and file manager
	 */
	public static void init() {
		FileManager.init();
		SettingManager.init();
		upm = new UpdateManager();


	}

	public static void runFromStartLunch(){
		FileManager.init();
		SettingManager.init();
		upm = new UpdateManager();
		var delay = 16000;
		do{
			try {
				discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(0));
				break;
			} catch (NoDiscordClientException | RuntimeException ex) {
				try {
					Thread.sleep(Math.min(delay, 60000));
					delay+=3000;
				} catch (InterruptedException e) {
					throw new RuntimeException(ex);
				}
			}
		}while (true); //everyone likes while true
		isRunning = true;
		new Thread(()->{
			if (upm.getUpdates().getSize() == 1) {
				return;
			}
			for (int i = 1; i < upm.getUpdates().getSize(); i++) {
				excuteUpdate(upm.getUpdates().getUpdates(i));
				if(!isRunning) return;
			}
			while (isRunning) {
				for (int i = 0; i < upm.getUpdates().getSize(); i++) {
					excuteUpdate(upm.getUpdates().getUpdates(i));
					if(!isRunning) return;
				}

			}
		}).start();
	}

	/**
	 * Launches DiscordRP
	 */
	public static void initCallBack() throws NoDiscordClientException {
		if(runloop == null)
			discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(0));
		else {
			discordRP.LaunchReadyCallBack(upm.getUpdates().getUpdates(getCURRENTDISPLAY()));
		}
		isRunning = true;

	}

	/**
	 * Sent each data to DiscordIPC according to the config
	 */
	public static void startUpdate() {

		if(runloop == null) {
			System.out.println("Thread is not created, creating a new thread");
			runloop = new Thread("RunLoop") {
				@Override
				public void run() {
					if (upm.getUpdates().getSize() == 1) {
						return;
					}
					for (int i = 1; i < upm.getUpdates().getSize(); i++) {
						excuteUpdate(upm.getUpdates().getUpdates(i));
						CURRENTDISPLAY=i;

						if (!isRunning) return;
						if(!CustomDiscordRPC.isOnSystemTray)
							Platform.runLater(()-> LoadingController.callBackController.updateCurrentDisplay());
					}
					while (isRunning) {
						for (int i = 0; i < upm.getUpdates().getSize(); i++) {
							excuteUpdate(upm.getUpdates().getUpdates(i));
							CURRENTDISPLAY=i;
							if (!isRunning) return;
							if(!CustomDiscordRPC.isOnSystemTray)
								Platform.runLater(()->LoadingController.callBackController.updateCurrentDisplay());
						}

					}
				}
			};
			runloop.start();
		}
	}

	/**
	 * Shut down DiscordRP, this method must be called to stop displaying the RP
	 */
	public static void closeCallBack() {
		System.out.println();
		discordRP.shutdown();
		isRunning = false;
	}




	private static void excuteUpdate(@NotNull Updates update) {
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