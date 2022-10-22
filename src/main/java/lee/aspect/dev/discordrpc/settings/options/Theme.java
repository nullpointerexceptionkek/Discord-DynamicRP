package lee.aspect.dev.discordrpc.settings.options;

public enum Theme {

	light("/lee/aspect/dev/Theme/Light.css", "Discord light"),
	dark("/lee/aspect/dev/Theme/Dark.css", "Discord dark"),

	lime("/lee/aspect/dev/Theme/lime.css", "lime"),

	mikuSnow("/lee/aspect/dev/Theme/MikuSnow/MikuSnow.css", "Miku snow");
	
	private final String themePath;

	private final String displayName;

	Theme(String string, String displayName) {
		this.themePath = string;
		this.displayName = displayName;
	}
	public String getThemepass() {
		return themePath;
	}

	public String getDisplayName() {
		return displayName;
	}
}
