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

package lee.aspect.dev.cdiscordrp.manager

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Settings
import java.io.File
import java.io.FilenameFilter
import java.util.*


class ConfigManager {

    companion object {
        @JvmStatic
        fun getCurrentConfigFiles(): Array<out File>? {
            val filter = FilenameFilter { _, name -> name.contains("_UpdateScript.json") }

            return DirectoryManager.getRootDir()?.listFiles(filter)

        }

        @JvmStatic
        fun showDialog() {
            Script.saveScriptToFile()
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
                if (file == Settings.getINSTANCE().loadedConfig) {
                    radioButton.isSelected = true
                }
                val hBox = HBox()
                hBox.spacing = 10.0

                val deleteButton = Button("Delete")
                deleteButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/delete.png"))
                        .toExternalForm()
                )
                deleteButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                deleteButton.setOnAction {
                    if (radioButton.isSelected) {
                        Alert(
                            Alert.AlertType.WARNING,
                            "You can't delete the currently loaded manager!",
                            ButtonType.OK
                        ).show()

                    } else {
                        file.delete()
                        val selectedRadioButton = toggleGroup.selectedToggle as RadioButton
                        val selectedFile =
                            File(DirectoryManager.getRootDir(), selectedRadioButton.text + "_UpdateScript.json")
                        Settings.getINSTANCE().loadedConfig = selectedFile
                        Script.loadScriptFromJson()
                        dialogStage.close()
                        showDialog()
                    }
                }

                val duplicateButton = Button("Duplicate")
                duplicateButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/duplicate.png"))
                        .toExternalForm()
                )
                duplicateButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                duplicateButton.setOnAction {
                    val newFile = File(file.parent, "copy-" + file.name)
                    file.copyTo(newFile, overwrite = true)
                    dialogStage.close()
                    showDialog()
                }

                val renameButton = Button("Rename")
                renameButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/rename.png"))
                        .toExternalForm()
                )
                renameButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                renameButton.setOnAction {
                    val textInputDialog =
                        TextInputDialog(file.name.substring(0, file.name.indexOf("_UpdateScript.json")))
                    textInputDialog.title = "Rename File"
                    textInputDialog.headerText = "Enter the new name for the file:"
                    textInputDialog.contentText = "New name:"
                    val result = textInputDialog.showAndWait()
                    if (result.isPresent) {
                        val newName = result.get()
                        val newFile = File(file.parent, newName + "_UpdateScript.json")
                        try {
                            file.renameTo(newFile)
                            Settings.getINSTANCE().loadedConfig = newFile
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
                newConfigDialog.headerText = "Enter the name for the manager:"
                newConfigDialog.contentText = "name:"
                val result = newConfigDialog.showAndWait()
                if (result.isPresent) {
                    try {
                        val newFile = File(DirectoryManager.getRootDir(), result.get() + "_UpdateScript.json")
                        newFile.createNewFile()
                        Settings.getINSTANCE().loadedConfig = newFile
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
                val selectedFile = File(DirectoryManager.getRootDir(), selectedRadioButton.text + "_UpdateScript.json")
                Settings.getINSTANCE().loadedConfig = selectedFile
                Script.loadScriptFromJson()
                dialogStage.close()
            }

            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                dialogStage.close()
            }

            val hBox = HBox()
            hBox.spacing = 10.0
            hBox.children.addAll(okButton, cancelButton)
            vBox.children.addAll(newConfigButton, hBox)

            dialogStage.scene = Scene(vBox)
            dialogStage.scene.stylesheets.add(Settings.getINSTANCE().theme.path)
            dialogStage.isResizable = false
            dialogStage.show()
        }

        @JvmStatic
        fun showDialogWithNoRadioButton() {
            Script.saveScriptToFile()
            val files = getCurrentConfigFiles()
            val dialogStage = Stage()
            dialogStage.initModality(Modality.APPLICATION_MODAL)
            dialogStage.title = "Config Manager"
            val vBox = VBox()
            val vBoxToolBox = VBox()
            vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBox.spacing = 30.0
            vBox.alignment = Pos.CENTER_LEFT
            vBox.prefWidth = 80.0
            vBoxToolBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBoxToolBox.spacing = 30.0
            vBoxToolBox.alignment = Pos.CENTER_RIGHT
            for (file in files!!) {
                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                val fileDisplay = Label(fileName)

                val hBox = HBox()
                hBox.spacing = 10.0

                val deleteButton = Button("Delete")
                deleteButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/delete.png"))
                        .toExternalForm()
                )
                deleteButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                deleteButton.setOnAction {
                    file.delete()
                    dialogStage.close()
                    showDialogWithNoRadioButton()

                }

                val duplicateButton = Button("Duplicate")
                duplicateButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/duplicate.png"))
                        .toExternalForm()
                )
                duplicateButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                duplicateButton.setOnAction {
                    var newFileName = if (!fileName.contains("_(")) fileName + "_(1)_UpdateScript.json" else fileName + "_UpdateScript.json"
                    if(!File(file.parent,newFileName).exists()){
                        file.copyTo(File(file.parent,newFileName), overwrite = false)
                    } else{
                        for(i in 1..15){
                            newFileName = newFileName.substring(0,newFileName.indexOf("_(")) + "_($i)_UpdateScript.json"
                            println(newFileName)
                            if(!File(file.parent,newFileName).exists()){
                                file.copyTo(File(file.parent,newFileName), overwrite = false)
                                break
                            }
                        }
                    }
                    dialogStage.close()
                    showDialogWithNoRadioButton()
                }

                val renameButton = Button("Rename")
                renameButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/rename.png"))
                        .toExternalForm()
                )
                renameButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                renameButton.setOnAction {
                    val textInputDialog =
                        TextInputDialog(file.name.substring(0, file.name.indexOf("_UpdateScript.json")))
                    textInputDialog.title = "Rename File"
                    textInputDialog.headerText = "Enter the new name for the file:"
                    textInputDialog.contentText = "New name:"
                    val result = textInputDialog.showAndWait()
                    if (result.isPresent) {
                        val newName = result.get()
                        val newFile = File(file.parent, newName + "_UpdateScript.json")
                        try {
                            file.renameTo(newFile)
                            Settings.getINSTANCE().loadedConfig = newFile
                        } catch (e: Exception) {
                            //just let the default error handler to display the error and quit
                            throw RuntimeException("Failed to rename file!", e)
                        }
                        dialogStage.close()
                        showDialogWithNoRadioButton()
                    }
                }

                hBox.children.addAll(deleteButton, duplicateButton, renameButton)
                vBox.children.add(fileDisplay)
                vBoxToolBox.children.add(hBox)
            }
            val newConfigButton = Button("New Config")
            newConfigButton.setOnAction {
                val newConfigDialog = TextInputDialog()
                newConfigDialog.title = "File set up wizard"
                newConfigDialog.headerText = "Enter the name for the manager:"
                newConfigDialog.contentText = "name:"
                val result = newConfigDialog.showAndWait()
                if (result.isPresent) {
                    try {
                        val newFile = File(DirectoryManager.getRootDir(), result.get() + "_UpdateScript.json")
                        newFile.createNewFile()
                        Settings.getINSTANCE().loadedConfig = newFile
                    } catch (e: Exception) {
                        //just let the default error handler to display the error and quit
                        throw RuntimeException("Failed to rename file!", e)
                    }
                    dialogStage.close()
                    showDialogWithNoRadioButton()
                }
            }


            val okButton = Button("OK")
            okButton.setOnAction {
                dialogStage.close()
            }
            val okHbox = HBox(okButton, newConfigButton)
            okHbox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            okHbox.spacing = 30.0
            okHbox.alignment = Pos.CENTER


            dialogStage.scene = Scene(VBox(HBox(vBox, vBoxToolBox), okHbox))
            dialogStage.scene.stylesheets.add(Settings.getINSTANCE().theme.path)
            dialogStage.isResizable = false
            dialogStage.show()
        }
    }
}