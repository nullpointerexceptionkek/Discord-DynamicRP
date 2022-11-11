/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.discordrpc.settings;


public class Settings {

    //info
    public final static String VERSION = "1.0.0";

    public final static String AUTHOR = "Aspect Development";

    public final static String NAME = "Custom Discord Rich Presence";

    public enum MinimizeMode {
        Ask, Always, Never, WaitAndSee
    }
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
    private static String DiscordAPIKey;

    private static Theme theme = Theme.dark;

    private static MinimizeMode minimizeMode = MinimizeMode.Ask;
    private static boolean NoAnimation = false;
    private static boolean StartTrayOnlyInterfaceClose = false;
    private static boolean StartLaunch = false;
    private static boolean ShutDownInterfaceWhenTray = false;

    public static boolean isStartTrayOnlyInterfaceClose() {
        return StartTrayOnlyInterfaceClose;
    }

    public static void setStartTrayOnlyInterfaceClose(boolean startTrayOnlyInterfaceClose) {
        StartTrayOnlyInterfaceClose = startTrayOnlyInterfaceClose;
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

