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
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import lee.aspect.dev.DirectoryManager
import java.io.File
import java.io.FilenameFilter




class ConfigManager {

    companion object{
        @JvmStatic
        fun getCurrentConfigFiles(): Array<out File>? {
            val filter = FilenameFilter { _, name -> name.contains("UpdateScript.json") }

             return DirectoryManager.getRootDir()?.listFiles(filter)

        }
        @JvmStatic
        fun showDialog(){
            // Get the list of files from getCurrentConfigFiles
            val files = getCurrentConfigFiles()

            // Create a JavaFX stage for the dialog
            val dialogStage = Stage()
            dialogStage.initModality(Modality.APPLICATION_MODAL)

            // Create a JavaFX VBox to hold the checkboxes and buttons
            val vBox = VBox()
            vBox.padding = Insets(10.0, 10.0, 10.0, 10.0)
            vBox.spacing = 10.0

            // Create a checkbox for each file
            val checkBoxes = mutableListOf<CheckBox>()
            for (file in files!!) {
                // Get the file name without the "_UpdateScript.json" part
                val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                val checkBox = CheckBox(fileName)
                checkBoxes.add(checkBox)
                vBox.children.add(checkBox)
            }

            // Create an "OK" button and add an event handler to close the dialog when clicked
            val okButton = Button("OK")
            okButton.setOnAction {
                dialogStage.close()
            }

            // Create a "Cancel" button and add an event handler to close the dialog when clicked
            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                dialogStage.close()
            }

            // Add the buttons to the VBox
            vBox.children.addAll(okButton, cancelButton)

            // Set the scene and show the dialog
            dialogStage.scene = Scene(vBox)
            dialogStage.showAndWait()
        }
    }

}