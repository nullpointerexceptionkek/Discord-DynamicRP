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

package lee.aspect.dev.config

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import lee.aspect.dev.DirectoryManager
import lee.aspect.dev.application.interfaceGui.WarningManager
import lee.aspect.dev.discordrpc.settings.SettingManager
import java.io.File
import java.io.FilenameFilter
import java.util.*


class ConfigManager {

    companion object{
        @JvmStatic
        fun getCurrentConfigFiles(): Array<out File>? {
            val filter = FilenameFilter { _, name -> name.contains("UpdateScript.json") }

             return DirectoryManager.getRootDir()?.listFiles(filter)

        }

        @JvmStatic
        fun showDialog(){
            val files = getCurrentConfigFiles()
            val dialogStage = Stage()
            dialogStage.initModality(Modality.APPLICATION_MODAL)
            val vBox = VBox()
            vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBox.spacing = 10.0
            val toggleGroup = ToggleGroup()
            for (file in files!!) {
                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                val radioButton = RadioButton(fileName)
                radioButton.toggleGroup = toggleGroup
                val hBox = HBox()
                hBox.spacing = 10.0

                val deleteButton = Button("Delete")
                deleteButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/delete.png"))
                        .toExternalForm())
                deleteButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                deleteButton.setOnAction {
                    file.delete()
                    dialogStage.close()
                    showDialog()
                }

                val duplicateButton = Button("Duplicate")
                duplicateButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/duplicate.png"))
                        .toExternalForm())
                duplicateButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY

                val renameButton = Button("Rename")
                renameButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/rename.png"))
                        .toExternalForm())
                renameButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY

                hBox.children.addAll(radioButton, deleteButton, duplicateButton, renameButton)
                vBox.children.add(hBox)
            }

            val okButton = Button("OK")
            okButton.setOnAction {
                dialogStage.close()
            }

            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                dialogStage.close()
            }

            val hBox = HBox()
            hBox.spacing = 10.0
            hBox.children.addAll(okButton, cancelButton)
            vBox.children.add(hBox)

            dialogStage.scene = Scene(vBox)
            dialogStage.scene.stylesheets.add(SettingManager.SETTINGS.theme.path)
            dialogStage.showAndWait()
        }
    }

}