package lee.aspect.dev.discordrpc.settings;

public enum Theme {

	light("/lee/aspect/dev/ApplicationLight.css"),
	dark("/lee/aspect/dev/Application.css"),
	lightgreen("/lee/aspect/dev/ApplicationLightGreen.css");
	
	private final String themepass;

	Theme(String string) {
		this.themepass = string;
	}
	public String Themepass() {
		return themepass;
	}
}
