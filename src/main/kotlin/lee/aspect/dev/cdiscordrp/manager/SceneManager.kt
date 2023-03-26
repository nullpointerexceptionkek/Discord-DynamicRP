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

package lee.aspect.dev.cdiscordrp.manager

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC
import lee.aspect.dev.cdiscordrp.application.core.Settings
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager
import lee.aspect.dev.cdiscordrp.exceptions.Debug
import lee.aspect.dev.cdiscordrp.language.LanguageManager
import java.io.IOException
import java.util.*

class SceneManager {
    companion object {
        /**
         * returns different manager screen according to the settings.
         */
        @JvmStatic
        @Throws(IOException::class)
        fun getConfigParent(): Parent? {
            if (Settings.getINSTANCE().isAutoSwitch) {
                return SwitchManager.initMenu()
            }
            return getDefaultConfigParent()
        }

        @JvmStatic
        fun getDefaultConfigParent(): Parent {
            return loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/ReadyConfig.fxml").root
        }

        @JvmStatic
        fun loadSceneWithStyleSheet(location:String): SceneData {
            val loader = FXMLLoader()
            loader.location = CustomDiscordRPC::class.java.getResource(location)

            loader.resources = LanguageManager.lang

            val root = loader.load<Parent>()
            root.stylesheets.clear()
            root.stylesheets.add(Settings.getINSTANCE().theme.path)
            return SceneData(root, loader.getController())
        }
    }
    data class SceneData(val root: Parent, val controller: Any)
}