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
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager
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
        fun showDialog(showRadioButton: Boolean, onCloseCallback: (() -> Unit)? = null) {
            Script.saveScriptToFile()
            val dialogStage = Stage()
            dialogStage.title = "Config Manager"
            dialogStage.initModality(Modality.APPLICATION_MODAL)
            val vBox = VBox()
            val vBoxToolBox = VBox()
            vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBox.spacing = 30.0
            vBox.alignment = Pos.CENTER_LEFT
            vBox.prefWidth = 150.0
            vBoxToolBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBoxToolBox.spacing = 30.0
            vBoxToolBox.alignment = Pos.CENTER_RIGHT

            val scrollPane = ScrollPane(HBox(vBox, vBoxToolBox))
            scrollPane.prefHeight = 150.0
            scrollPane.prefWidth = 220.0
            scrollPane.isFitToWidth = true
            scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

            updateDialogContent(vBox, vBoxToolBox, showRadioButton)

            val newConfigButton = Button("New Config")
            newConfigButton.setOnAction {
                val newConfigDialog = TextInputDialog()
                newConfigDialog.title = "File set up wizard"
                newConfigDialog.headerText = "Enter the name for the manager:"
                newConfigDialog.contentText = "name:"
                val result = newConfigDialog.showAndWait()
                if (result.isPresent && result.get().isNotEmpty())
                    try {
                        val newFile = File(DirectoryManager.getRootDir(), result.get() + "_UpdateScript.json")
                        newFile.createNewFile()
                        Settings.getINSTANCE().loadedConfig = newFile
                    } catch (e: Exception) {
                        //just let the default error handler to display the error and quit
                        throw RuntimeException("Failed to rename file!", e)
                    }
                updateDialogContent(vBox, vBoxToolBox, showRadioButton)
            }
            val okButton = Button("OK")
            okButton.setOnAction {
                dialogStage.close()
            }
            val okHbox = HBox(okButton, newConfigButton)
            okHbox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            okHbox.spacing = 30.0
            okHbox.alignment = Pos.CENTER

            dialogStage.scene = Scene(VBox(scrollPane, okHbox))
            dialogStage.scene.stylesheets.add(Settings.getINSTANCE().theme.path)
            dialogStage.isResizable = true
            if (onCloseCallback != null)
                dialogStage.setOnHiding { onCloseCallback() }
            dialogStage.show()
        }

        private fun updateDialogContent(vBox: VBox, vBoxToolBox: VBox, showRadioButton: Boolean) {
            vBox.children.clear()
            vBoxToolBox.children.clear()
            val files = getCurrentConfigFiles()
            val toggleGroup = if (showRadioButton) ToggleGroup() else null

            for (file in files!!) {

                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))

                if (showRadioButton) {
                    val radioButton = RadioButton(fileName)
                    radioButton.toggleGroup = toggleGroup
                    if (file == Settings.getINSTANCE().loadedConfig) {
                        radioButton.isSelected = true
                    }
                    radioButton.setOnAction {
                        val selectedFile = File(DirectoryManager.getRootDir(), radioButton.text + "_UpdateScript.json")
                        Settings.getINSTANCE().loadedConfig = selectedFile
                        Script.loadScriptFromJson()
                    }
                    vBox.children.add(radioButton)
                } else {
                    val fileDisplay = Label(fileName)
                    vBox.children.add(fileDisplay)
                }

                val deleteButton = Button("Delete")
                deleteButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/delete.png"))
                        .toExternalForm()
                )
                deleteButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY


                deleteButton.setOnAction {
                    file.delete()
                    updateDialogContent(vBox, vBoxToolBox, showRadioButton)
                }

                val duplicateButton = Button("Duplicate")
                duplicateButton.graphic = ImageView(
                    Objects.requireNonNull(ConfigManager::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/editIcons/duplicate.png"))
                        .toExternalForm()
                )
                duplicateButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
                duplicateButton.setOnAction {
                    var newFileName = fileName + "_(1)_UpdateScript.json"
                    val newFileIndex = fileName.length + 1
                    if (!File(file.parent, newFileName).exists()) {
                        file.copyTo(File(file.parent, newFileName), overwrite = false)
                    } else {
                        for (i in 1..15) {
                            newFileName = newFileName.substring(0, newFileIndex) + "($i)_UpdateScript.json"
                            if (!File(file.parent, newFileName).exists()) {
                                file.copyTo(File(file.parent, newFileName), overwrite = false)
                                break
                            }
                        }
                    }
                    updateDialogContent(vBox, vBoxToolBox, showRadioButton)
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
                            for (switch in SwitchManager.loaded.switch) {
                                if (switch.config == file) {
                                    //println("Switch ${switch.config} now points to $newFile")
                                    switch.config = newFile
                                }
                            }
                            file.renameTo(newFile)
                            SwitchManager.saveToFile()
                            if(Settings.getINSTANCE().loadedConfig == file) {
                                Settings.getINSTANCE().loadedConfig = newFile
                            }
                        } catch (e: Exception) {
                            //just let the default error handler to display the error and quit
                            throw RuntimeException("Failed to rename file!", e)
                        }
                        updateDialogContent(vBox, vBoxToolBox, showRadioButton)
                    }
                }

                val hBox = HBox()
                hBox.spacing = 10.0
                hBox.children.addAll(deleteButton, duplicateButton, renameButton)
                vBoxToolBox.children.add(hBox)
            }
        }

    }
}