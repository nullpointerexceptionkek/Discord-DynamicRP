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
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import lee.aspect.dev.DirectoryManager
import lee.aspect.dev.discordrpc.UpdateManager
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
            UpdateManager.saveScriptToFile()
            val files = getCurrentConfigFiles()
            val dialogStage = Stage()
            dialogStage.title = "Config Manager"
            dialogStage.initModality(Modality.APPLICATION_MODAL)
            val vBox = VBox()
            vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBox.spacing = 30.0
            val toggleGroup = ToggleGroup()
            for (file in files!!) {
                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                val radioButton = RadioButton(fileName)
                radioButton.toggleGroup = toggleGroup
                if(file == SettingManager.SETTINGS.loadedConfig){
                    radioButton.isSelected = true
                }
                val hBox = HBox()
                hBox.spacing = 10.0

                val deleteButton = Button("Delete")
                deleteButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/delete.png"))
                        .toExternalForm())
                deleteButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                deleteButton.setOnAction {
                    if(radioButton.isSelected){
                        Alert(Alert.AlertType.WARNING, "You can't delete the currently loaded config!", ButtonType.OK).show()

                    } else {
                        file.delete()
                        val selectedRadioButton = toggleGroup.selectedToggle as RadioButton
                        val selectedFile = File(DirectoryManager.getRootDir(), selectedRadioButton.text+"_UpdateScript.json")
                        SettingManager.SETTINGS.loadedConfig = selectedFile
                        UpdateManager.SCRIPT = UpdateManager.loadScriptFromJson()
                        dialogStage.close()
                        showDialog()
                    }
                }

                val duplicateButton = Button("Duplicate")
                duplicateButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/duplicate.png"))
                        .toExternalForm())
                duplicateButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                duplicateButton.setOnAction {
                    val newFile = File(file.parent, "copy-" + file.name)
                    file.copyTo(newFile, overwrite = true)
                    dialogStage.close()
                    showDialog()
                }

                val renameButton = Button("Rename")
                renameButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/icon/editIcons/rename.png"))
                        .toExternalForm())
                renameButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                renameButton.setOnAction {
                    val textInputDialog = TextInputDialog(file.name.substring(0, file.name.indexOf("_UpdateScript.json")))
                    textInputDialog.title = "Rename File"
                    textInputDialog.headerText = "Enter the new name for the file:"
                    textInputDialog.contentText = "New name:"
                    val result = textInputDialog.showAndWait()
                    if (result.isPresent) {
                        val newName = result.get()
                        val newFile = File(file.parent, newName+"_UpdateScript.json")
                        try {
                            file.renameTo(newFile)
                            SettingManager.SETTINGS.loadedConfig = newFile
                        } catch (e: Exception) {
                            //just let the default error handler to display the error and quit
                            throw RuntimeException("Failed to rename file!", e)
                        }
                        dialogStage.close()
                        showDialog()
                    }
                }

                hBox.children.addAll(radioButton, deleteButton, duplicateButton, renameButton)
                vBox.children.add(hBox)
            }
            val newConfigButton = Button("New Config")
            newConfigButton.setOnAction {
                val newConfigDialog = TextInputDialog()
                newConfigDialog.title = "File set up wizard"
                newConfigDialog.headerText = "Enter the name for the config:"
                newConfigDialog.contentText = "name:"
                val result = newConfigDialog.showAndWait()
                if (result.isPresent) {
                    try {
                        val newFile = File(DirectoryManager.getRootDir(), result.get() + "_UpdateScript.json")
                        newFile.createNewFile()
                        SettingManager.SETTINGS.loadedConfig = newFile
                    } catch (e: Exception) {
                        //just let the default error handler to display the error and quit
                        throw RuntimeException("Failed to rename file!", e)
                    }
                    dialogStage.close()
                    showDialog()
                }
            }


            val okButton = Button("OK")
            okButton.setOnAction {
                val selectedRadioButton = toggleGroup.selectedToggle as RadioButton
                val selectedFile = File(DirectoryManager.getRootDir(), selectedRadioButton.text+"_UpdateScript.json")
                SettingManager.SETTINGS.loadedConfig = selectedFile
                UpdateManager.SCRIPT = UpdateManager.loadScriptFromJson()
                dialogStage.close()
            }

            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                dialogStage.close()
            }

            val hBox = HBox()
            hBox.spacing = 10.0
            hBox.children.addAll(okButton, cancelButton)
            vBox.children.addAll(newConfigButton,hBox)

            dialogStage.scene = Scene(vBox)
            dialogStage.scene.stylesheets.add(SettingManager.SETTINGS.theme.path)
            dialogStage.isResizable = false
            dialogStage.show()
        }
    }

}