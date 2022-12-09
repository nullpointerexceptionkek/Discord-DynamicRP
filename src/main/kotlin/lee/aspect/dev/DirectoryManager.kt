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

package lee.aspect.dev

import javafx.application.Platform
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import lee.aspect.dev.sysUtil.StartLaunch
import lee.aspect.dev.sysUtil.exceptions.UnsupportedOSException
import java.io.File
import java.util.*


class DirectoryManager {
    companion object {

        @JvmField
        val defaultDir = System.getProperty("user.home") + "\\CustomDiscordRPC"

        private var ROOT_DIR = File(getDirectoryEnvironmentVar())

        @JvmStatic
        fun getDirectoryEnvironmentVar(): String {
            println("getDirectoryEnvironmentVar: ${System.getenv("CDRPCDir")}")
            if (System.getProperty("CDRPCDir") == null) {
                writeDirectoryEnvironmentVar(defaultDir)
            }
            return System.getProperty("CDRPCDir") ?: defaultDir
        }

        @JvmStatic
        fun writeDirectoryEnvironmentVar(dir: String) {
            //write the directory to the environment variable

            //check if the user is on Windows
            if (StartLaunch.isOnWindows()) {
                ProcessBuilder("setx", "CDRPCDir", dir).start().waitFor()
            } else if (StartLaunch.isOnMac()) {
                ProcessBuilder("launchctl", "setenv", "CDRPCDir", dir).start().waitFor()
            } else if (StartLaunch.isOnLinux()) {
                ProcessBuilder("export", "CDRPCDir", dir).start().waitFor()
            } else {
                throw UnsupportedOSException("invalid os")
            }


        }

        @JvmStatic
        fun deleteDirectoryEnvironmentVar() {
            if (StartLaunch.isOnWindows()) {
                ProcessBuilder("setx", "/m", "CDRPCDir").start().waitFor()
            } else if (StartLaunch.isOnMac()) {
                ProcessBuilder("launchctl", "unsetenv", "CDRPCDir").start().waitFor()
            } else if (StartLaunch.isOnLinux()) {
                ProcessBuilder("unset", "CDRPCDir").start().waitFor()
            } else {
                throw UnsupportedOSException("invalid os")
            }
        }

        @JvmStatic
        fun askForDirectory() {
            //make a javaFX dialog
            try {
                Platform.runLater {
                    val directoryChooser = DirectoryChooser()
                    directoryChooser.title = "Choose a directory"

                    // create a TextField control for the user to input a directory path

                    // create a TextField control for the user to input a directory path
                    val directoryPathField = TextField()
                    directoryPathField.promptText = "Enter a directory path"

                    // create a Button control for the user to open the DirectoryChooser popup

                    // create a Button control for the user to open the DirectoryChooser popup
                    val chooseDirectoryButton = Button("Choose directory")
                    chooseDirectoryButton.setOnAction { event ->
                        val selectedDirectory = directoryChooser.showDialog(null)
                        if (selectedDirectory != null) {
                            directoryPathField.text = selectedDirectory.absolutePath
                        }
                    }

                    // create a Label to show a message to the user

                    // create a Label to show a message to the user
                    val messageLabel = Label("Please choose a directory or enter a directory path:")

                    // create a VBox to hold the controls and layout them vertically

                    // create a VBox to hold the controls and layout them vertically
                    val dialogLayout = VBox(messageLabel, directoryPathField, chooseDirectoryButton)
                    dialogLayout.spacing = 10.0

                    // create a Dialog to show the controls to the user

                    // create a Dialog to show the controls to the user
                    val dialog: Dialog<String> = Dialog()
                    dialog.title = "Select or input a directory"
                    dialog.dialogPane.content = dialogLayout

                    // add a button to the dialog to allow the user to submit their selection

                    // add a button to the dialog to allow the user to submit their selection
                    val submitButtonType = ButtonType("Submit", ButtonData.OK_DONE)
                    dialog.dialogPane.buttonTypes.add(submitButtonType)

                    // show the dialog and wait for the user to either input a directory or select a directory

                    // show the dialog and wait for the user to either input a directory or select a directory
                    val result: Optional<String> = dialog.showAndWait()

                    // check if the user clicked the submit button and selected a directory or input a directory path

                    // check if the user clicked the submit button and selected a directory or input a directory path
                    if (result.isPresent && result.get().isNotEmpty()) {
                        // the user selected a directory or input a directory path, so you can use the selected directory as needed
                        val directoryPath = result.get()
                        // ...
                    } else {
                        // the user did not select a directory or input a directory path, so you can handle this as needed
                        // ...
                    }
                }
            } catch (e: Exception) {
                throw RuntimeException("JavaFx might not be initialized - $e")
            }
        }

        @JvmStatic
        fun getRootDir(): File {
            return System.getenv("CDiscordRPConfigDir")?.let { File(it) } ?: ROOT_DIR
        }

    }

}