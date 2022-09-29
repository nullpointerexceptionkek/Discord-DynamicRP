package lee.aspect.dev.discordrpc.settings;

public class Settings {

	private static String DiscordAPIKey;
	
	private static Theme theme = Theme.dark;
	
	public static void setDiscordAPIKey(String discordAPIKey) {
		DiscordAPIKey = discordAPIKey;
	}
	
	public static void setTheme(Theme theme) {
		Settings.theme = theme;
	}
	
	public static String getDiscordAPIKey() {
		return DiscordAPIKey;
	}
	
	public static Theme getTheme() {
		return theme;
	}
	
	@Override
	public String toString() {
		return theme + ", " + DiscordAPIKey;
	}
	
}

