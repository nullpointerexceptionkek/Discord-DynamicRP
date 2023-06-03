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
package lee.aspect.dev.dynamicrp.system

import javafx.application.Platform
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import lee.aspect.dev.dynamicrp.Launch
import lee.aspect.dev.dynamicrp.application.core.RunLoopManager
import lee.aspect.dev.dynamicrp.application.core.Script
import lee.aspect.dev.dynamicrp.application.core.Settings
import lee.aspect.dev.dynamicrp.autoswitch.SwitchManager
import lee.aspect.dev.dynamicrp.exceptions.FileNotAJarException
import lee.aspect.dev.dynamicrp.exceptions.UnsupportedOSException
import java.io.*
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

object SystemHandler {
    @JvmStatic
    val isOnWindows: Boolean
        get() {
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            return osName.contains("win")
        }
    @JvmStatic
    val isOnLinux: Boolean
        get() {
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            return osName.contains("nix") || osName.contains("nux") || osName.contains("aix")
        }
    @JvmStatic
    val isOnMac: Boolean
        get() {
            val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
            return osName.contains("mac")
        }

    @JvmStatic
    fun setEnvironmentVariable(dir: String,  onSuccess: (() -> Unit)?, onFailure: ((Throwable) -> Unit)?) {
        val task = object : Task<Void>() {
            @Throws(Exception::class)
            override fun call(): Void? {
                if (isOnWindows) {
                    ProcessBuilder("setx", Launch.NAME, dir).start().waitFor()
                } else if (isOnMac) {
                    val launchAgentDirectory = File(System.getProperty("user.home"), "Library/LaunchAgents")
                    if (!launchAgentDirectory.exists()) {
                        launchAgentDirectory.mkdirs()
                    }
                    val launchdPlistPath = File(launchAgentDirectory, "${Launch.NAME}.plist")
                    PrintWriter(FileWriter(launchdPlistPath)).use { writer ->
                        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        writer.println("<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\"")
                        writer.println("        \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">")
                        writer.println("<plist version=\"1.0\">")
                        writer.println("<dict>")
                        writer.println("    <key>Label</key>")
                        writer.println("    <string>${Launch.NAME}</string>")
                        writer.println("    <key>ProgramArguments</key>")
                        writer.println("    <array>")
                        writer.println("        <string>sh</string>")
                        writer.println("        <string>-c</string>")
                        writer.println("        <string>launchctl setenv ${Launch.NAME} $dir</string>")
                        writer.println("    </array>")
                        writer.println("    <key>RunAtLoad</key>")
                        writer.println("    <true/>")
                        writer.println("</dict>")
                        writer.println("</plist>")
                    }
                    ProcessBuilder("launchctl", "load", launchdPlistPath.absolutePath).start().waitFor()
                } else if (isOnLinux) {
                    BufferedWriter(
                        Files.newBufferedWriter(
                            Paths.get(
                                System.getProperty("user.home"),
                                ".bashrc"
                            )
                        )
                    ).use {
                        it.write("export ${Launch.NAME}=$dir\n")
                    }
                    ProcessBuilder("/bin/bash", "-c", "source ~/.bashrc").inheritIO().start().waitFor()
                    ProcessBuilder("/bin/bash", "-c", "source ~/.zshrc").inheritIO().start().waitFor()
                } else {
                    throw UnsupportedOSException("invalid os")
                }
                return null
            }
        }
        if(onSuccess != null)
            task.onSucceeded = EventHandler { onSuccess() }
        if(onFailure != null)
            task.onFailed = EventHandler { onFailure(task.exception) }
        Thread(task).start()
    }

    /**
     * Restarts the current application.
     *
     *
     * This method uses the current jar file to start a new process that runs the application. The
     * current process is exited after calling the [RunLoopManager.onClose] method to perform
     * necessary clean-up.
     *
     *
     * However, this method does not work if the application is started from an IDE or if the jar file
     * You should always make a exception handler to catch the exception and show a prompt to the user
     *
     * @throws URISyntaxException   if the current jar file cannot be found
     * @throws IOException          if an error occurs while starting the new process
     * @throws FileNotAJarException if the current file is not a jar file
     */
    @JvmStatic
    @Throws(URISyntaxException::class, IOException::class, FileNotAJarException::class)
    fun fullRestart() {
        try {
            RunLoopManager.shutDownRP()
        } catch (_: NullPointerException) {
            //shutDownRP() will throw either NullPointException or IllegalStateException if it is already closed
            //the purpose if this is obviously a way to notify if the RP is already closed.
            //There is no harm on ignoring this exception
        } catch (_: IllegalStateException) {
        }
        try{
            Settings.saveSettingToFile()
            Script.saveScriptToFile()
            SwitchManager.saveToFile()
        } catch (_:UninitializedPropertyAccessException){
            //can be ignored
        }
        val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
        val currentJar = File(SystemHandler::class.java.protectionDomain.codeSource.location.toURI())
        if (!currentJar.name.endsWith(".jar")) {
            throw FileNotAJarException()
        }
        val command = ArrayList<String>()
        command.add(javaBin)
        command.add("-jar")
        command.add(currentJar.path)
        val builder = ProcessBuilder(command)
        builder.start()
    }

    fun fullRestartWithWarnings(msg: String?, forceRestart:Boolean = false) {
        Platform.runLater {
            val alert = Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to restart the application?" + if (msg == null) "" else "\n\n$msg",
                ButtonType.OK, ButtonType.CANCEL
            )
            alert.title = "Restart Confirmation"
            val result = alert.showAndWait()

            if (result.isPresent && result.get() == ButtonType.OK) {
                try {
                    fullRestart()
                } catch (e: Exception) {
                    val errorAlert = Alert(Alert.AlertType.ERROR)
                    errorAlert.title = "Error Restarting"
                    errorAlert.contentText = e.message
                    errorAlert.showAndWait()
                    if(forceRestart) exitProcess(0)
                }

            } else if (forceRestart) {
                val closeAlert = Alert(Alert.AlertType.CONFIRMATION)
                closeAlert.title = "Close Confirmation"
                closeAlert.contentText = "Program will now close"
                closeAlert.showAndWait()
                exitProcess(0)
            }
        }
    }

    object StartLaunch {
        private val STARTUPDIR_WINDOWS =
            File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup")
        private val STARTUPDIR_LINUX = File(System.getProperty("user.home") + "/.config/autostart/")
        private val STARTUPDIR_MAC = File(System.getProperty("user.home") + "/Library/LaunchAgents/")
        private const val APP_NAME = Launch.NAME
        private const val APP_SCRIPT_WINDOWS = "$APP_NAME.bat"
        private const val APP_SCRIPT_LINUX = "$APP_NAME.desktop"
        private const val APP_SCRIPT_MAC = "$APP_NAME.sh"
        @JvmStatic
        @Throws(
            IOException::class,
            UnsupportedOSException::class,
            FileNotAJarException::class,
            URISyntaxException::class
        )
        fun createStartupScript() {
            val currentJar = File(StartLaunch::class.java.protectionDomain.codeSource.location.toURI())
            if (!currentJar.name.endsWith(".jar")) {
                throw FileNotAJarException()
            }
            if (isOnWindows) {
                val batFile = File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS)
                PrintWriter(FileWriter(batFile)).use { writer -> writer.println("start \"\" javaw -jar $currentJar --StartLaunch") }
            } else if (isOnLinux) {
                val scriptFile = File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX)
                PrintWriter(FileWriter(scriptFile)).use { writer ->
                    writer.println("[Desktop Entry]")
                    writer.println("Type=Application")
                    writer.println("Name=$APP_NAME")
                    writer.println("Exec=/usr/bin/java -jar $currentJar --StartLaunch")
                    writer.println("Terminal=false")
                    writer.println("Version=1.0")
                    writer.println("X-GNOME-Autostart-enabled=true")
                }
                scriptFile.setExecutable(true)
            } else if (isOnMac) {
                if(!STARTUPDIR_MAC.exists()) STARTUPDIR_MAC.mkdirs()
                val plistFile = File(STARTUPDIR_MAC, APP_SCRIPT_MAC)
                PrintWriter(FileWriter(plistFile)).use { writer ->
                    writer.println("<plist version=\"1.0\">")
                    writer.println("<dict>")
                    writer.println("    <key>Label</key>")
                    writer.println("    <string>$APP_NAME</string>")
                    writer.println("    <key>ProgramArguments</key>")
                    writer.println("    <array>")
                    writer.println("        <string>java</string>")
                    writer.println("        <string>-jar</string>")
                    writer.println("        <string>$currentJar</string>")
                    writer.println("        <string>--StartLaunch</string>")
                    writer.println("    </array>")
                    writer.println("    <key>RunAtLoad</key>")
                    writer.println("    <true/>")
                    writer.println("</dict>")
                    writer.println("</plist>")
                }
                plistFile.setExecutable(true)
            } else {
                throw UnsupportedOSException("Start Launch currently only supports Windows, Linux, and macOS")
            }
        }

        val isStartupScriptCreated: Boolean
            get() {
                if (isOnWindows) {
                    return File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS).exists()
                }
                if (isOnLinux) {
                    return File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX).exists()
                }
                return if (isOnMac) {
                    File(STARTUPDIR_MAC, APP_SCRIPT_MAC).exists()
                } else false
            }

        @JvmStatic
        fun deleteStartupScript() {
            if (isOnWindows) {
                val batFile = File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS)
                if (batFile.exists()) {
                    batFile.delete()
                }
            } else if (isOnLinux) {
                val scriptFile = File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX)
                if (scriptFile.exists()) {
                    scriptFile.delete()
                }
            } else if (isOnMac) {
                val plistFile = File(STARTUPDIR_MAC, APP_SCRIPT_MAC)
                if (plistFile.exists()) {
                    plistFile.delete()
                }
            }
        }

        @JvmStatic
        val isJar: Boolean
            get() {
                val currentJar: File = try {
                    File(StartLaunch::class.java.protectionDomain.codeSource.location.toURI())
                } catch (e: URISyntaxException) {
                    return false
                }
                return currentJar.name.endsWith(".jar")
            }
    }
}
