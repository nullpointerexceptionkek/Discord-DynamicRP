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

package lee.aspect.dev.language

import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import lee.aspect.dev.application.interfaceGui.WarningManager
import lee.aspect.dev.discordrpc.settings.SettingManager
import java.util.*

abstract class LanguageManager {
    companion object{
        //this needs to be initialized after the file manager
        @JvmStatic
        lateinit var lang: ResourceBundle
        @JvmStatic
        fun setLang(lang : Languages){
            this.lang = ResourceBundle.getBundle(lang.resourceLocation, Locale.getDefault())
        }
        @JvmStatic
        fun init(){
            this.lang = ResourceBundle.getBundle(SettingManager.SETTINGS.lang.resourceLocation, Locale.getDefault())
        }

        @JvmStatic
        fun showDialog(){

            // Create a new Dialog object
            val dialog = Dialog<ButtonType>()
            dialog.title = "Change Language"

            // Create a ChoiceBox to display the available languages
            val choiceBox = ChoiceBox<Languages>()
            choiceBox.items.addAll(Languages.values())


            // Set the dialog content to the ChoiceBox
            val textLabel = Label("Please select a language, your current language is ${SettingManager.SETTINGS.lang}")
            dialog.dialogPane.content = VBox(textLabel, choiceBox)

            // Add a button to confirm the selected language
            dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL,ButtonType.OK)

            dialog.dialogPane.stylesheets.add(SettingManager.SETTINGS.theme.themepass)

            // Show the dialog and wait for the user to confirm their selection
            val result = dialog.showAndWait()

            // If the user confirmed their selection, change the current language
            if (result.isPresent && result.get() == ButtonType.OK) {
                setLang(choiceBox.value)
                SettingManager.SETTINGS.lang = choiceBox.value
                WarningManager.restartToApplyChanges()
            }

        }
    }

}