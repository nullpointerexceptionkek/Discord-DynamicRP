/*
 *
 * MIT License
 *
 * Copyright (c) 2023 lee
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

package lee.aspect.dev.cdiscordrp.application.core;


import lee.aspect.dev.cdiscordrp.json.loader.FileManager;
import lee.aspect.dev.cdiscordrp.language.Languages;
import lee.aspect.dev.cdiscordrp.manager.DirectoryManager;

import java.io.File;

public class Settings {

    private static final Settings INSTANCE = new Settings();
    private Languages lang = Languages.EN_US;
    private Theme theme = Theme.dark;
    private MinimizeMode minimizeMode = MinimizeMode.Ask;
    private boolean NoAnimation = false;
    private boolean StartTrayOnlyInterfaceClose = false;
    private boolean StartLaunch = false;
    private boolean ShutDownInterfaceWhenTray = false;
    private boolean AutoSwitch = false;
    private File loadedConfig = null;

    private double windowWidth = -1;
    private double windowHeight = -1;

    private Settings() {
    }

    public static Settings getINSTANCE() {
        return INSTANCE;
    }

    public static void loadKeyFromJson() {
        try {
            FileManager.readFromJson(new File(DirectoryManager.getROOT_DIR(), "Settings.json"), Settings.class);
        } catch (RuntimeException e) {
            setup();
        }
    }

    public static void saveSettingToFile() {
        if(CDiscordRP.primaryStage != null) {
            Settings.getINSTANCE().setWindowHeight(CDiscordRP.primaryStage.getHeight());
            Settings.getINSTANCE().setWindowWidth(CDiscordRP.primaryStage.getWidth());
        }

        FileManager.writeJsonTofile(new File(DirectoryManager.getROOT_DIR(), "Settings.json"), INSTANCE);
    }

    public static void setup() {
        File defaultFile = new File(DirectoryManager.getROOT_DIR(), "default_UpdateScript.json");
        getINSTANCE().setLoadedConfig(defaultFile);
        saveSettingToFile();
        loadKeyFromJson();
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

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

    public Languages getLang() {
        return lang;
    }

    public void setLang(Languages lang) {
        this.lang = lang;
    }

    public boolean isAutoSwitch() {
        return AutoSwitch;
    }

    public void setAutoSwitch(boolean autoSwitch) {
        AutoSwitch = autoSwitch;
    }

    @Override
    public String toString() {
        return theme.toString();
    }

    public File getLoadedConfig() {
        return loadedConfig;
    }

    public void setLoadedConfig(File loadedConfig) {
        this.loadedConfig = loadedConfig;
    }

    public enum MinimizeMode {
        Ask, Always, Never, WaitAndSee
    }

    public enum Theme {

        light("/lee/aspect/dev/cdiscordrp/theme/DefaultLight.css", "Default Light"),
        dark("/lee/aspect/dev/cdiscordrp/theme/DefaultDark.css", "Default Dark"),

        lime("/lee/aspect/dev/cdiscordrp/theme/KeyLime.css", "Key Lime"),

        purple("/lee/aspect/dev/cdiscordrp/theme/MidnightPurple.css", "Midnight Purple"),

        retroLight("/lee/aspect/dev/cdiscordrp/theme/RetroLight.css", "Retro Light"),

        minimalistWhite("/lee/aspect/dev/cdiscordrp/theme/MinimalistWhite.css", "Minimalist White"),

        materialWhite("/lee/aspect/dev/cdiscordrp/theme/MaterialWhite.css", "Material White"),

        materialDark("/lee/aspect/dev/cdiscordrp/theme/MaterialDark.css", "Material Dark");



        private final String themePath;

        private final String displayName;

        Theme(String string, String displayName) {
            this.themePath = string;
            this.displayName = displayName;
        }

        public String getPath() {
            return themePath;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

