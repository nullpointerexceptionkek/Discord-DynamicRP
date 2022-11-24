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
    private String DiscordAPIKey;

    private Theme theme = Theme.dark;

    private MinimizeMode minimizeMode = MinimizeMode.Ask;
    private boolean NoAnimation = false;
    private boolean StartTrayOnlyInterfaceClose = false;
    private boolean StartLaunch = false;
    private boolean ShutDownInterfaceWhenTray = false;

    public boolean isStartTrayOnlyInterfaceClose() {
        return StartTrayOnlyInterfaceClose;
    }

    public void setStartTrayOnlyInterfaceClose(boolean startTrayOnlyInterfaceClose) {
        this.StartTrayOnlyInterfaceClose = startTrayOnlyInterfaceClose;
    }

    public boolean isStartLaunch() {
        return StartLaunch;
    }

    public void setStartLaunch(boolean startLaunch) {
        this.StartLaunch = startLaunch;
    }

    public boolean isShutDownInterfaceWhenTray() {
        return ShutDownInterfaceWhenTray;
    }

    public void setShutDownInterfaceWhenTray(boolean shutDownInterfaceWhenTray) {
        this.ShutDownInterfaceWhenTray = shutDownInterfaceWhenTray;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public MinimizeMode getMinimizeMode() {
        return minimizeMode;
    }

    public void setMinimizeMode(MinimizeMode minimizeMode) {
        this.minimizeMode = minimizeMode;
    }

    public boolean isNoAnimation() {
        return NoAnimation;
    }

    public void setNoAnimation(boolean noAnimation) {
        this.NoAnimation = noAnimation;
    }

    public String getDiscordAPIKey() {
        return DiscordAPIKey;
    }

    public void setDiscordAPIKey(String discordAPIKey) {
        this.DiscordAPIKey = discordAPIKey;
    }

    @Override
    public String toString() {
        return theme + ", " + DiscordAPIKey;
    }

}

