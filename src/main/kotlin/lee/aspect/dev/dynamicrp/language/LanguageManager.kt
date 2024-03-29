/*
 * 2022-
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

package lee.aspect.dev.dynamicrp.language

import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import lee.aspect.dev.dynamicrp.application.core.DynamicRP
import lee.aspect.dev.dynamicrp.application.core.Settings
import lee.aspect.dev.dynamicrp.manager.SceneManager.Companion.loadSceneWithStyleSheet
import java.util.*

class LanguageManager private constructor() {
    companion object {
        //this needs to be initialized after the file manager
        @JvmStatic
        lateinit var lang: ResourceBundle

        @JvmStatic
        fun setLang(lang: Languages) {
            Companion.lang = ResourceBundle.getBundle(lang.resourceLocation, Locale.getDefault(), UTF8Control())
        }

        @JvmStatic
        fun init() {
            setLang(Settings.getINSTANCE().lang)
        }

        @JvmStatic
        fun showDialog() {

            val dialog = Dialog<ButtonType>()
            dialog.title = "Change Language"

            val choiceBox = ChoiceBox<Languages>()
            choiceBox.items.addAll(Languages.values())
            val warningMessage = Label("Warning: translation may not be accurate")
            warningMessage.styleClass.add("warning-label")
            warningMessage.isVisible = false
            choiceBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                if(newValue != null)
                    warningMessage.isVisible = newValue != Languages.EN_US
            }

            val textLabel = Label("Please select a language, your current language is ${Settings.getINSTANCE().lang}")
            dialog.dialogPane.content = VBox(textLabel, warningMessage, choiceBox)

            dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)

            dialog.dialogPane.stylesheets.add(Settings.getINSTANCE().theme.path)

            val result = dialog.showAndWait()

            if (result.isPresent && result.get() == ButtonType.OK) {
                setLang(if(choiceBox.value == null) Settings.getINSTANCE().lang else choiceBox.value)
                Settings.getINSTANCE().lang = choiceBox.value
                DynamicRP.primaryStage.scene = Scene(loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/Settings.fxml").root)
            }

        }
    }

}
