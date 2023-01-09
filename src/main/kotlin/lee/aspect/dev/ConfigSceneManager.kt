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

package lee.aspect.dev

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import lee.aspect.dev.application.CustomDiscordRPC
import lee.aspect.dev.autoswitch.SwitchManager.Companion.initMenu
import lee.aspect.dev.discordrpc.settings.SettingManager
import java.io.IOException
import java.util.*

class ConfigSceneManager {
    companion object {
        /**
         * returns different config screen according to the settings.
         */
        @JvmStatic
        @Throws(IOException::class)
        fun getConfigParent(): Parent? {
            if (SettingManager.SETTINGS.isAutoSwitch) {
                return initMenu()
            }
            //default
            val root = FXMLLoader.load<Parent>(
                Objects.requireNonNull(
                    CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")
                )
            )
            root.stylesheets.add(
                Objects.requireNonNull(CustomDiscordRPC::class.java.getResource(SettingManager.SETTINGS.theme.path))
                    .toExternalForm()
            )
            return root
        }
    }
}