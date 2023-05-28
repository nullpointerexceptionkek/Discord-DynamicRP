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
import javafx.scene.layout.*
import javafx.stage.Modality
import javafx.stage.Stage
import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Settings
import lee.aspect.dev.cdiscordrp.autoswitch.SwitchManager
import java.io.File
import java.io.FilenameFilter


class ConfigManager {

    companion object {
        @JvmStatic
        fun getCurrentConfigFiles(): Array<out File> {
            val filter = FilenameFilter { _, name -> name.contains("_UpdateScript.json") }

            return DirectoryManager.ROOT_DIR.listFiles(filter)!!

        }
        @JvmStatic
        fun showDialog(showRadioButton: Boolean, onCloseCallback: (() -> Unit)? = null) {
            Script.saveScriptToFile()
            val dialogStage = Stage()
            dialogStage.title = "Config Manager"
            dialogStage.initModality(Modality.APPLICATION_MODAL)
            val grid = GridPane()
            grid.padding = Insets(10.0, 10.0, 10.0, 10.0)
            grid.hgap = 10.0
            grid.vgap = 3.0
            grid.alignment = Pos.CENTER_LEFT
            grid.prefWidth = 150.0
            grid.columnConstraints.add(0,ColumnConstraints(200.0))

            val scrollPane = ScrollPane(grid)
            scrollPane.prefHeight = 150.0
            scrollPane.prefWidth = 380.0
            scrollPane.isFitToWidth = true
            scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

            VBox.setVgrow(scrollPane, Priority.ALWAYS) // Set Vgrow for ScrollPane

            val vBoxToolBox = VBox()
            vBoxToolBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBoxToolBox.spacing = 30.0
            vBoxToolBox.alignment = Pos.CENTER_RIGHT

            val vBox = VBox(scrollPane, vBoxToolBox)

            updateDialogContent(grid, showRadioButton)

            val newConfigButton = Button("New Config")
            newConfigButton.setOnAction {
                val newConfigDialog = TextInputDialog()
                newConfigDialog.title = "File set up wizard"
                newConfigDialog.headerText = "Enter the name for the manager:"
                newConfigDialog.contentText = "name:"
                val result = newConfigDialog.showAndWait()
                if (result.isPresent && result.get().isNotEmpty())
                    try {
                        val newFile = File(DirectoryManager.ROOT_DIR, result.get() + "_UpdateScript.json")
                        newFile.createNewFile()
                        Settings.getINSTANCE().loadedConfig = newFile
                    } catch (e: Exception) {
                        //just let the default error handler to display the error and quit
                        throw RuntimeException("Failed to rename file!", e)
                    }
                updateDialogContent(grid, showRadioButton)
            }
            val okButton = Button("OK")
            okButton.setOnAction {
                dialogStage.close()
            }
            val okHbox = HBox(okButton, newConfigButton)
            okHbox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            okHbox.spacing = 30.0
            okHbox.alignment = Pos.CENTER

            dialogStage.scene = Scene(VBox(vBox, okHbox))
            dialogStage.scene.stylesheets.add(Settings.getINSTANCE().theme.path)
            dialogStage.isResizable = true
            if (onCloseCallback != null)
                dialogStage.setOnHiding { onCloseCallback() }
            dialogStage.show()
        }


        @JvmStatic
        private fun updateDialogContent(grid: GridPane, showRadioButton: Boolean) {
            grid.children.clear()
            val files = getCurrentConfigFiles()
            val toggleGroup = if (showRadioButton) ToggleGroup() else null

            files.forEachIndexed { i, file ->
                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                if (showRadioButton) {
                    val radioButton = RadioButton(fileName).apply {
                        mnemonicParsingProperty().set(false) // this is required to display the underscore
                        this.toggleGroup = toggleGroup
                        if (file == Settings.getINSTANCE().loadedConfig) {
                            this.isSelected = true
                        }
                        setOnAction {
                            val selectedFile = File(DirectoryManager.ROOT_DIR, text + "_UpdateScript.json")
                            Settings.getINSTANCE().loadedConfig = selectedFile
                            Script.loadScriptFromJson()
                        }
                    }
                    GridPane.setHgrow(radioButton, Priority.ALWAYS)
                    grid.add(radioButton, 0, i) // add to first column
                } else {
                    val fileDisplay = Label(fileName)
                    GridPane.setHgrow(fileDisplay, Priority.ALWAYS)
                    grid.add(fileDisplay, 0, i) // add to first column
                }
                val deleteButton = createButton("Delete", "/lee/aspect/dev/cdiscordrp/icon/editIcons/delete.png").apply {
                    setOnAction {
                        file.delete()
                        if(Settings.getINSTANCE().loadedConfig == file) {
                            if(files.size == 1){
                                Settings.getINSTANCE().loadedConfig = Settings.getDefaultFileDir()
                            } else{
                                Settings.getINSTANCE().loadedConfig = getCurrentConfigFiles()[0]
                            }
                            Script.loadScriptFromJson()
                        }
                        updateDialogContent(grid, showRadioButton)
                    }
                }
                grid.add(deleteButton, 1, i) // add to second column

                val duplicateButton = createButton("Duplicate", "/lee/aspect/dev/cdiscordrp/icon/editIcons/duplicate.png").apply {
                    setOnAction {
                        duplicateFileAction(file, fileName)
                        updateDialogContent(grid, showRadioButton)
                    }
                }
                grid.add(duplicateButton, 2, i) // add to third column

                val renameButton = createButton("Rename", "/lee/aspect/dev/cdiscordrp/icon/editIcons/rename.png").apply {
                    setOnAction {
                        renameFileAction(file)
                        updateDialogContent(grid, showRadioButton)
                    }
                }
                grid.add(renameButton, 3, i) // add to fourth column
            }
        }
        private fun createButton(buttonText: String, iconPath: String): Button {
            return Button(buttonText).apply {
                graphic = ImageView(ConfigManager::class.java.getResource(iconPath)?.toExternalForm())
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
            }
        }

        private fun duplicateFileAction(file: File, fileName: String) {
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
        }

        private fun renameFileAction(file: File) {
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
            }
        }
    }
}