package lee.aspect.dev.discordrpc.settings.options;

public enum Theme {

    light("/lee/aspect/dev/theme/Light.css", "Discord light"),
    dark("/lee/aspect/dev/theme/Dark.css", "Discord dark"),

    lime("/lee/aspect/dev/theme/lime.css", "lime"),

    mikuSnow("/lee/aspect/dev/theme/mikuSnow/MikuSnow.css", "Miku snow"),

    huTao("/lee/aspect/dev/theme/hutao/Hutao.css", "Hutao");

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
