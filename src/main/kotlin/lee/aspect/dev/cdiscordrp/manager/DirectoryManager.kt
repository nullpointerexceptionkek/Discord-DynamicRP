/*
 *
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

import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import lee.aspect.dev.cdiscordrp.Launch
import lee.aspect.dev.cdiscordrp.application.core.Settings
import lee.aspect.dev.cdiscordrp.application.core.Settings.Theme
import lee.aspect.dev.cdiscordrp.exceptions.UnsupportedOSException
import lee.aspect.dev.cdiscordrp.util.system.RestartApplication
import lee.aspect.dev.cdiscordrp.util.system.StartLaunch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess


class DirectoryManager {
    companion object {

        @JvmField
        val defaultDir = System.getProperty("user.home") + File.separator + "CustomDiscordRPC"

        @JvmStatic
        lateinit var ROOT_DIR: File
        private set


        @JvmStatic
        fun writeDirectoryEnvironmentVar(dir: String) {

            val progressIndicator = ProgressIndicator()

            progressIndicator.progress = -1.0

            val dialog: Dialog<Void> = Dialog()
            dialog.graphic = progressIndicator

            dialog.title = "Setting up manager directory"
            dialog.contentText = "This process is setting the environment variable CDRPCDir to $dir"

            dialog.show()


            if (StartLaunch.isOnWindows()) {
                ProcessBuilder("setx", "CDRPCDir", dir).start().waitFor()
            } else if (StartLaunch.isOnMac()) {
                val launchAgentDirectory = File(System.getProperty("user.home"), "Library/LaunchAgents")
                if (!launchAgentDirectory.exists()) {
                    launchAgentDirectory.mkdirs()
                }
                val launchdPlistPath = File(launchAgentDirectory, "CDRPCDir.plist")
                PrintWriter(FileWriter(launchdPlistPath)).use { writer ->
                    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                    writer.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\"")
                    writer.println("        \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">")
                    writer.println("<plist version=\"1.0\">")
                    writer.println("<dict>")
                    writer.println("    <key>Label</key>")
                    writer.println("    <string>CDRPCDir</string>")
                    writer.println("    <key>ProgramArguments</key>")
                    writer.println("    <array>")
                    writer.println("        <string>/usr/bin/launchctl</string>")
                    writer.println("        <string>setenv</string>")
                    writer.println("        <string>CDRPCDir</string>")
                    writer.println("        <string>$dir</string>")
                    writer.println("    </array>")
                    writer.println("    <key>RunAtLoad</key>")
                    writer.println("    <true/>")
                    writer.println("    <key>KeepAlive</key>")
                    writer.println("    <true/>")
                    writer.println("</dict>")
                    writer.println("</plist>")
                }


            } else if (StartLaunch.isOnLinux()) {
                BufferedWriter(
                    Files.newBufferedWriter(
                        Paths.get(
                            System.getProperty("user.home"),
                            ".bashrc"
                        )
                    )
                ).use {
                    it.write("export CDRPCDir=$dir\n")
                }
                ProcessBuilder("/bin/bash", "-c", "source ~/.bashrc").inheritIO().start().waitFor()
                ProcessBuilder("/bin/bash", "-c", "source ~/.zshrc").inheritIO().start().waitFor()
            } else {
                throw UnsupportedOSException("invalid os")
            }
            dialog.close()

        }

        @JvmStatic
        fun deleteDirectoryEnvironmentVar() {
            if (StartLaunch.isOnWindows()) {
                ProcessBuilder("REG", "DELETE", "HKCU\\Environment", "/F", "/V", "CDRPCDir").inheritIO().start().waitFor()
            } else if (StartLaunch.isOnMac()) {
                ProcessBuilder("launchctl", "unsetenv", "CDRPCDir").start().waitFor()
                val launchAgentPlistPath = System.getProperty("user.home") + "/Library/LaunchAgents/CDRPCDir.plist"
                File(launchAgentPlistPath).delete()
            } else if (StartLaunch.isOnLinux()) {
                ProcessBuilder("unset", "CDRPCDir").start().waitFor()
                val bashrcPath = Paths.get(System.getProperty("user.home"), ".bashrc")
                val zshrcPath = Paths.get(System.getProperty("user.home"), ".zshrc")
                Files.deleteIfExists(bashrcPath)
                Files.deleteIfExists(zshrcPath)
            } else {
                throw UnsupportedOSException("invalid os")
            }
        }

        @JvmStatic
        fun unsetEnvBash(){
            ProcessBuilder("unset", "CDRPCDir").start().waitFor()
            val bashrcPath = Paths.get(System.getProperty("user.home"), ".bashrc")
            val zshrcPath = Paths.get(System.getProperty("user.home"), ".zshrc")
            Files.deleteIfExists(bashrcPath)
            Files.deleteIfExists(zshrcPath)
        }


        @JvmStatic
        fun initDirectory():Boolean{
            val directory = System.getenv("CDRPCDir") ?: defaultDir
            return if (File(directory).exists()) {
                ROOT_DIR = File(directory)
                Launch.LOGGER.info("Directory exists: $directory")
                true
            } else {
                Launch.LOGGER.warn("Directory does not exist: $directory")
                false
            }
        }
        @JvmStatic
        fun askForDirectory() {
            Launch.LOGGER.info("Opening directory setup wizard")
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

                val resetToDefaultButton = Button("Reset to default directory")
                resetToDefaultButton.setOnAction {
                    directoryPathField.text = defaultDir
                }

                val messageLabel = Label("Please choose a directory or enter a directory path:")

                val warningMessage = Label("Warning: Changing the directory is not recommended.")
                warningMessage.styleClass.add("warning-label")
                warningMessage.isVisible = false

                directoryPathField.textProperty().addListener { _, _, newValue ->
                    warningMessage.isVisible = newValue != defaultDir
                }

                val dialogLayout = VBox(messageLabel, directoryPathField, chooseDirectoryButton, resetToDefaultButton, warningMessage)
                dialogLayout.spacing = 10.0

                val dialog: Dialog<ButtonType> = Dialog()
                dialog.title = "Select or input a directory"
                dialog.dialogPane.content = dialogLayout
                dialog.dialogPane.stylesheets.add(Settings.getINSTANCE().theme.path)

                val submitButtonType = ButtonType("Submit", ButtonData.OK_DONE)
                dialog.dialogPane.buttonTypes.add(submitButtonType)

                val cancelButtonType = ButtonType("Cancel", ButtonData.CANCEL_CLOSE)
                dialog.dialogPane.buttonTypes.add(cancelButtonType)

                val result: Optional<ButtonType> = dialog.showAndWait()

                if (result.isPresent) {
                    when (result.get()) {
                        submitButtonType -> handleSubmission(directoryPathField)
                        cancelButtonType -> exitProcess(0) // Exit program when 'x' button clicked
                    }
                } else {
                    exitProcess(0) // Exit program when 'x' button clicked
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun handleSubmission(directoryPathField: TextField) {
            val directoryPath = directoryPathField.text
            if (directoryPath.isNotBlank()) {
                val directory = File(directoryPath)
                if (!directory.exists()) {
                    createDirectory(directory)
                }
                if(directory != File(defaultDir)) {
                    writeDirectoryEnvironmentVar(directoryPath)
                    return
                }
                RestartApplication.FullRestart()
            }
        }

        private fun createDirectory(directory: File) {
            val createDirectoryDialog = Alert(Alert.AlertType.CONFIRMATION)
            createDirectoryDialog.title = "Create directory?"
            createDirectoryDialog.headerText = "The directory you entered doesn't exist."
            createDirectoryDialog.contentText = "Would you like to create the directory?"
            val createDirectoryResult = createDirectoryDialog.showAndWait()
            if (createDirectoryResult.isPresent && createDirectoryResult.get() == ButtonType.OK) {
                directory.mkdirs()
            } else {
                askForDirectory()
            }
        }


    }

}