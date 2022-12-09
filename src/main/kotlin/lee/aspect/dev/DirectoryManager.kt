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
import javafx.stage.DirectoryChooser
import java.io.File
import javax.swing.JFileChooser

class DirectoryManager {
    companion object {

        private val defaultDir = System.getProperty("user.home") + "\\CustomDiscordRPC"

        private var ROOT_DIR = File(getDirectoryEnvironmentVar())

        @JvmStatic
        fun getDirectoryEnvironmentVar(): String {
            println("getDirectoryEnvironmentVar: ${System.getenv("CDiscordRPConfigDir")}")
            if (System.getProperty("CDiscordRPConfigDir") == null) {
                writeDirectoryEnvironmentVar(defaultDir)
            }
            return System.getProperty("CDiscordRPConfigDir")
        }
        @JvmStatic
        fun writeDirectoryEnvironmentVar(dir: String) {
            //write the directory to the environment variable
            try {
                System.setProperty("CDiscordRPConfigDir", dir)
            } catch (e: SecurityException) {
                println("SecurityException: ${e.message}")
            }

        }
        @JvmStatic
        fun deleteDirectoryEnvironmentVar() {
            try {
                System.clearProperty("CDiscordRPConfigDir")
            } catch (e: SecurityException) {
                println("SecurityException: ${e.message}")
            }
        }
        @JvmStatic
        fun askForDirectory(){
            //make a javaFX dialog
            try {
                Platform.runLater {
                    val chooser = DirectoryChooser()
                    chooser.title = "Choose a directory to store your config files"
                    chooser.initialDirectory = File(defaultDir)

                    val selectedDirectory = chooser.showDialog(null)
                    if(selectedDirectory != null){
                        writeDirectoryEnvironmentVar(selectedDirectory.absolutePath)
                    }
                }
            } catch (e: Exception) {
                //if javafx failed, use swing
                val chooser = JFileChooser()

                // Set the file chooser to only allow directories
                chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY

                // Set the initial directory
                chooser.currentDirectory = File(defaultDir)

                // Set the dialog title
                chooser.dialogTitle = "Choose a directory to store your config files"

                // Show the directory chooser and get the selected directory
                val result = chooser.showOpenDialog(null)

                // Check if the user selected a directory
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Get the selected directory
                    val selectedDirectory = chooser.selectedFile

                    // Write the selected directory to an environment variable
                    writeDirectoryEnvironmentVar(selectedDirectory.absolutePath)
                }

            }
        }
        @JvmStatic
        fun getRootDir(): File {
            return System.getenv("CDiscordRPConfigDir")?.let { File(it) } ?: ROOT_DIR
        }

    }

}