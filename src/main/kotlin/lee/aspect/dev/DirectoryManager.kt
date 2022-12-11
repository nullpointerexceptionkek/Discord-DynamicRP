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
            println("CDRPCDir: ${System.getenv("CDRPCDir")}")
            //this should not be null
            return System.getenv("CDRPCDir")?: defaultDir
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
        fun isSetUp(): Boolean {
            //check if everything is set up
            if(System.getenv("CDRPCDir") != null && File(System.getenv("CDRPCDir")).exists())
                return true
            return false
        }

        @JvmStatic
        fun askForDirectory() {
            //make a javaFX dialog
            try {
                    val directoryChooser = DirectoryChooser()
                    directoryChooser.title = "Choose a directory"

                    val directoryPathField = TextField()
                    directoryPathField.promptText = "Enter a directory path"
                    directoryPathField.text = defaultDir

                    val chooseDirectoryButton = Button("Choose directory")
                    chooseDirectoryButton.setOnAction {
                        val selectedDirectory = directoryChooser.showDialog(null)
                        if (selectedDirectory != null) {
                            directoryPathField.text = selectedDirectory.absolutePath
                        }
                    }

                    val messageLabel = Label("Please choose a directory or enter a directory path:")


                    val dialogLayout = VBox(messageLabel, directoryPathField, chooseDirectoryButton)
                    dialogLayout.spacing = 10.0

                    val dialog: Dialog<String> = Dialog()
                    dialog.title = "Select or input a directory"
                    dialog.dialogPane.content = dialogLayout

                    val submitButtonType = ButtonType("Submit", ButtonData.OK_DONE)
                    dialog.dialogPane.buttonTypes.add(submitButtonType)

                    dialog.showAndWait()

                    if (directoryPathField.text.isNotEmpty() && directoryPathField.text.isNotBlank()) {
                        val directoryPath = directoryPathField.text
                        if(!File(directoryPath).exists()){
                            //open a dialog to tell the user that the directory doesn't exist and ask if they want to create it
                            val createDirectoryDialog = Alert(Alert.AlertType.CONFIRMATION)
                            createDirectoryDialog.title = "Create directory?"
                            createDirectoryDialog.headerText = "The directory you entered doesn't exist."
                            createDirectoryDialog.contentText = "Would you like to create the directory?"
                            val createDirectoryResult = createDirectoryDialog.showAndWait()
                            if(createDirectoryResult.isPresent && createDirectoryResult.get() == ButtonType.OK){
                                //create the directory
                                File(directoryPath).mkdirs()
                                writeDirectoryEnvironmentVar(directoryPath)
                            }
                        }
                        else{
                            writeDirectoryEnvironmentVar(directoryPath)
                        }
                        // ...
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun getRootDir(): File {
            return System.getenv("CDiscordRPConfigDir")?.let { File(it) } ?: ROOT_DIR
        }

    }

}