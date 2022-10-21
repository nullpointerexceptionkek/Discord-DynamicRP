package lee.aspect.dev.discordrpc.settings;

import lee.aspect.dev.discordrpc.settings.options.MinimizeMode;
import lee.aspect.dev.discordrpc.settings.options.Theme;

public class Settings {

    private static String DiscordAPIKey;

    private static Theme theme = Theme.dark;

    private static MinimizeMode minimizeMode = MinimizeMode.Ask;
    private static boolean NoAnimation = false;
    private static boolean LowResourceMode = false;
    private static boolean StartLaunch = false;
    private static boolean ShutDownInterfaceWhenTray = false;

    public static boolean isLowResourceMode() {
        return LowResourceMode;
    }

    public static void setLowResourceMode(boolean lowResourceMode) {
        LowResourceMode = lowResourceMode;
    }

    public static boolean isStartLaunch() {
        return StartLaunch;
    }

    public static void setStartLaunch(boolean startLaunch) {
        StartLaunch = startLaunch;
    }

    public static boolean isShutDownInterfaceWhenTray() {
        return ShutDownInterfaceWhenTray;
    }

    public static void setShutDownInterfaceWhenTray(boolean shutDownInterfaceWhenTray) {
        ShutDownInterfaceWhenTray = shutDownInterfaceWhenTray;
    }

    public static Theme getTheme() {
        return theme;
    }

    public static void setTheme(Theme theme) {
        Settings.theme = theme;
    }

    public static MinimizeMode getMinimizeMode() {
        return minimizeMode;
    }

    public static void setMinimizeMode(MinimizeMode minimizeMode) {
        Settings.minimizeMode = minimizeMode;
    }

    public static boolean isNoAnimation() {
        return NoAnimation;
    }

    public static void setNoAnimation(boolean noAnimation) {
        NoAnimation = noAnimation;
    }

    public static String getDiscordAPIKey() {
        return DiscordAPIKey;
    }

    public static void setDiscordAPIKey(String discordAPIKey) {
        DiscordAPIKey = discordAPIKey;
    }

    @Override
    public String toString() {
        return theme + ", " + DiscordAPIKey;
    }

}

