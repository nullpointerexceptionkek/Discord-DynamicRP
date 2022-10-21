package lee.aspect.dev.discordrpc.settings.options;

public enum Theme {

	light("/lee/aspect/dev/Theme/ApplicationLight.css"),
	dark("/lee/aspect/dev/Theme/Application.css"),
	lightgreen("/lee/aspect/dev/Theme/ApplicationLightGreen.css");
	
	private final String themepass;

	Theme(String string) {
		this.themepass = string;
	}
	public String Themepass() {
		return themepass;
	}
}
