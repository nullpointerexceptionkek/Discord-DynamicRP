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

import lee.aspect.dev.DirectoryManager;
import lee.aspect.dev.jsonreader.FileManager;

import java.io.File;


public abstract class SettingManager {

    public static Settings SETTINGS;

    public static void init() {
        SETTINGS = loadKeyFromJson();
    }

    private static Settings loadKeyFromJson() {
        Settings loaded = FileManager.readFromJson(new File(DirectoryManager.getRootDir(), "Settings.json"), Settings.class);
        if (loaded == null) {
            loaded = new Settings();
            loaded.setTheme(Settings.Theme.dark);
            saveSettingToFile();
        }
        return loaded;

    }

    public static void saveSettingToFile() {
        FileManager.writeJsonTofile(new File(DirectoryManager.getRootDir(), "Settings.json"), SETTINGS);
    }

}
