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

package lee.aspect.dev.cdiscordrp.autoswitch

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment
import lee.aspect.dev.cdiscordrp.Launch
import lee.aspect.dev.cdiscordrp.animatefx.SlideInDown
import lee.aspect.dev.cdiscordrp.animatefx.SlideInLeft
import lee.aspect.dev.cdiscordrp.animatefx.SlideInUp
import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC
import lee.aspect.dev.cdiscordrp.application.core.RunLoopManager
import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Settings
import lee.aspect.dev.cdiscordrp.json.loader.FileManager
import lee.aspect.dev.cdiscordrp.manager.ConfigManager
import lee.aspect.dev.cdiscordrp.manager.DirectoryManager.Companion.getRootDir
import lee.aspect.dev.cdiscordrp.manager.SceneManager
import lee.aspect.dev.cdiscordrp.processmonitor.OpenCloseListener
import lee.aspect.dev.cdiscordrp.processmonitor.ProcessMonitor
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class SwitchManager private constructor() {

    companion object {
        class LoadSwitchFromFile {
            lateinit var switch: Array<Switch>
        }

        private lateinit var loaded: LoadSwitchFromFile

        @JvmStatic
        fun loadFromFile() {
            if (!File(getRootDir(), "Switch.json").exists())
                File(getRootDir(), "Switch.json").createNewFile()
            var loaded = FileManager.readFromJson(
                File(getRootDir(), "Switch.json"),
                LoadSwitchFromFile::class.java
            )
            if (loaded == null) {
                loaded = LoadSwitchFromFile()
                loaded.switch = Array(ConfigManager.getCurrentConfigFiles()?.size ?: 1) { Switch() }
                for (i in loaded.switch.indices) {
                    loaded.switch[i].config = ConfigManager.getCurrentConfigFiles()!![i]
                }

                Companion.loaded = loaded
                saveToFile()
                return
            }
            if (loaded.switch.size != ConfigManager.getCurrentConfigFiles()?.size) {
                val oldSwitch = loaded.switch
                loaded.switch = Array(ConfigManager.getCurrentConfigFiles()?.size ?: 1) { Switch() }
                for (i in oldSwitch.indices) {
                    if (i >= loaded.switch.size) {
                        break
                    }
                    loaded.switch[i] = oldSwitch[i]
                }
                for (i in oldSwitch.size until loaded.switch.size) {
                    loaded.switch[i] = Switch()
                    loaded.switch[i].config = ConfigManager.getCurrentConfigFiles()!![i]
                }
                Companion.loaded = loaded
                saveToFile()
                return;
            }
            Companion.loaded = loaded
        }


        @JvmStatic
        fun saveToFile() {
            FileManager.writeJsonTofile(
                File(getRootDir(), "Switch.json"),
                loaded
            )
        }

        @JvmStatic
        fun initMenu(): Parent {


            val anchorRoot = AnchorPane()
            anchorRoot.id = "anchorRoot"
            val switchStackPane = StackPane()
            //switchStackPane.id = "defaultPane"
            switchStackPane.padding = Insets(30.0)
            switchStackPane.setPrefSize(334.0, 540.0)

            val files = ConfigManager.getCurrentConfigFiles()
            files!!

            val gridPane = GridPane()
            gridPane.alignment = Pos.CENTER
            gridPane.vgap = 10.0
            gridPane.hgap = 10.0

            for (i in files.indices) {
                val fileName = files[i].name.substring(0, files[i].name.indexOf("_UpdateScript.json"))
                val text = Label(fileName)
                gridPane.add(text, 0, i)

                val textField = TextField()

                if (loaded.switch.size > i && loaded.switch[i].checkName.isNotBlank())
                    textField.text = loaded.switch[i].checkName

                textField.textProperty().addListener { _, _, newValue ->
                    loaded.switch[i].checkName = newValue
                }

                textField.maxWidth = 140.0

                val editCfgButton = Button("Edit")

                editCfgButton.setOnAction {
                    Settings.getINSTANCE().loadedConfig = files[i]
                    Script.loadScriptFromJson()

                    val root = SceneManager.getDefaultConfigParent()
                    anchorRoot.children.add(root)
                    val animation = SlideInDown(root)
                    animation.setOnFinished {
                        if (anchorRoot.children.contains(switchStackPane)) {
                            anchorRoot.children.remove(switchStackPane)
                        }
                    }
                    switchStackPane.children.forEach{
                        it.isDisable = true
                    }
                    animation.play()

                }

                val hbox = HBox(editCfgButton, textField)
                hbox.spacing = 10.0
                hbox.alignment = Pos.CENTER_RIGHT

                gridPane.add(hbox, 1, i)
            }


            val statusLabel = Label("Not Connected")
            statusLabel.textAlignment = TextAlignment.CENTER

            val startButton = Button("Start Operation")
            var isStarted = false

            val configManagerButton = Button()
            configManagerButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            val cfgIcon = ImageView(
                Objects.requireNonNull(CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/Config.png"))
                    .toExternalForm()
            )
            cfgIcon.fitHeight = 16.0
            cfgIcon.fitWidth = 16.0
            configManagerButton.graphic = cfgIcon
            configManagerButton.setOnAction {
                ConfigManager.showDialog(false) {
                    refreshUI(anchorRoot)
                }
            }

            val settingsButton = Button()
            settingsButton.setOnAction {
                val root = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/Settings.fxml").root
                anchorRoot.children.add(root)
                val animation = SlideInUp(root)
                animation.setOnFinished {
                    anchorRoot.children.remove(anchorRoot)
                }
                switchStackPane.children.forEach{
                    it.isDisable = true
                }
                animation.play()
            }
            settingsButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            val settingsIcon = ImageView(
                Objects.requireNonNull(CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/cdiscordrp/icon/settingsImage.png"))
                    .toExternalForm()
            )

            settingsIcon.fitHeight = 16.0
            settingsIcon.fitWidth = 16.0
            settingsButton.graphic = settingsIcon

            val controlHBbox = HBox(settingsButton, startButton, configManagerButton)
            controlHBbox.spacing = 10.0
            controlHBbox.alignment = Pos.BOTTOM_CENTER
            controlHBbox.isPickOnBounds = false

            //declear an arraylist that holds object(reference) in kotlin

            val reference = ArrayList<ProcessMonitor>()

            startButton.setOnAction {
                startButton.isDisable = true
                if (!isStarted) {
                    startButton.text = "Stop Operation"
                    isStarted = true
                    statusLabel.text = "Initializing..."
                    gridPane.children.forEach {
                        it.isDisable = true
                    }
                    settingsButton.isDisable = true
                    configManagerButton.isDisable = true
                    for (i in files.indices) {
                        if (loaded.switch[i].checkName.isNotEmpty()) {
                            val monitor = ProcessMonitor()
                            monitor.startMonitoring(
                                loaded.switch[i].checkName,
                                object : OpenCloseListener {
                                    override fun onProcessOpen() {
                                        try {
                                            RunLoopManager.closeCallBack()
                                        } catch (_: Exception) {
                                        }
                                        Settings.getINSTANCE().loadedConfig = files[i]
                                        Script.loadScriptFromJson()
                                        Platform.runLater {
                                            statusLabel.text = "${loaded.switch[i].checkName} Process Opened"
                                        }
                                        try {
                                            RunLoopManager.startUpdate()
                                            Platform.runLater {
                                                statusLabel.text = "Connected"
                                            }
                                        } catch (e: Exception) {
                                            Platform.runLater {
                                                statusLabel.text = "Error: ${e.message}"
                                            }
                                        }
                                    }

                                    override fun onProcessClose() {
                                        try {
                                            RunLoopManager.closeCallBack()
                                        } catch (_: Exception) {
                                        }
                                        Platform.runLater {
                                            statusLabel.text = "${loaded.switch[i].checkName} Process Closed"
                                        }
                                        Platform.runLater {
                                            statusLabel.text = "Not Connected - Waiting for Process"
                                        }
                                    }
                                },
                                3,
                                TimeUnit.SECONDS
                            )
                            reference.add(monitor)
                        }
                    }


                } else {
                    statusLabel.text = "Disconnecting..."
                    try {
                        RunLoopManager.closeCallBack()
                    } catch (_: Exception) {
                    }

                    for (i in reference.indices) {
                        reference[i].stopMonitoring()
                    }

                    gridPane.children.forEach {
                        it.isDisable = false
                    }
                    settingsButton.isDisable = false
                    configManagerButton.isDisable = false
                    statusLabel.text = "Not Connected"
                    startButton.text = "Launch Callback"
                    isStarted = false
                }

                startButton.isDisable = false

            }

            val controlVbox = VBox(controlHBbox, statusLabel)

            controlVbox.spacing = 5.0

            controlVbox.alignment = Pos.BOTTOM_CENTER

            controlVbox.isPickOnBounds = false


            val CDiscordRP = Label("CDiscordRP" + Launch.VERSION)
            CDiscordRP.id = "titleLabel"
            CDiscordRP.textAlignment = TextAlignment.CENTER
            val CDiscordRPHBox = HBox(CDiscordRP)
            CDiscordRPHBox.alignment = Pos.TOP_CENTER

            val scrollPane = ScrollPane()
            scrollPane.isFitToWidth = true
            scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            scrollPane.content = gridPane



            val content = VBox()
            content.spacing = 10.0
            content.alignment = Pos.CENTER
            content.children.addAll(scrollPane)


            switchStackPane.children.addAll(CDiscordRPHBox, content, controlVbox)
            anchorRoot.children.add(switchStackPane)
            AnchorPane.setTopAnchor(switchStackPane, 0.0)
            AnchorPane.setLeftAnchor(switchStackPane, 0.0)
            AnchorPane.setRightAnchor(switchStackPane, 0.0)
            AnchorPane.setBottomAnchor(switchStackPane, 0.0)


            anchorRoot.stylesheets.add(Settings.getINSTANCE().theme.path)

            return anchorRoot

        }

        private fun refreshUI(anchorRoot: AnchorPane) {
            loadFromFile()
            anchorRoot.children.clear()
            val newRoot = initMenu()
            anchorRoot.children.add(newRoot)
        }
        @JvmStatic
        fun initAutoSwitchSilent() {
            val files = ConfigManager.getCurrentConfigFiles()
            files!!

            for (i in files.indices) {
                if (loaded.switch[i].checkName.isNotEmpty()) {
                    val monitor = ProcessMonitor()
                    monitor.startMonitoring(
                        loaded.switch[i].checkName,
                        object : OpenCloseListener {
                            override fun onProcessOpen() {
                                try {
                                    RunLoopManager.closeCallBack()
                                } catch (_: Exception) {
                                }
                                Settings.getINSTANCE().loadedConfig = files[i]
                                Script.loadScriptFromJson()

                                try {
                                    RunLoopManager.startUpdate()
                                } catch (_: Exception) {
                                }
                            }

                            override fun onProcessClose() {
                                try {
                                    RunLoopManager.closeCallBack()
                                } catch (_: Exception) {
                                }
                            }
                        },
                        3,
                        TimeUnit.SECONDS
                    )
                }
            }
        }

    }


}