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
import lee.aspect.dev.cdiscordrp.animatefx.SlideInUp
import lee.aspect.dev.cdiscordrp.application.core.CustomDiscordRPC
import lee.aspect.dev.cdiscordrp.application.core.RunLoopManager
import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Settings
import lee.aspect.dev.cdiscordrp.json.loader.FileManager
import lee.aspect.dev.cdiscordrp.manager.ConfigManager
import lee.aspect.dev.cdiscordrp.manager.DirectoryManager.Companion.getRootDir
import lee.aspect.dev.cdiscordrp.manager.SceneManager
import lee.aspect.dev.cdiscordrp.manager.SceneManager.Companion.loadSceneWithStyleSheet
import lee.aspect.dev.cdiscordrp.processmonitor.OpenCloseListener
import lee.aspect.dev.cdiscordrp.processmonitor.ProcessMonitor
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class SwitchManager private constructor() {

    companion object {
        class LoadSwitchFromFile {
            val cfgVer = "1.0"
            lateinit var switch: ArrayList<Switch>
        }

        lateinit var loaded: LoadSwitchFromFile

        @JvmStatic
        fun loadFromFile() {
            if (!File(getRootDir(), "Switch.json").exists()){
                File(getRootDir(), "Switch.json").createNewFile()
            }
            var loaded = FileManager.readFromJson(
                File(getRootDir(), "Switch.json"),
                LoadSwitchFromFile::class.java
            )
            if (loaded == null) {
                loaded = LoadSwitchFromFile()
                loaded.switch = ArrayList()
            }
            Companion.loaded = loaded
            saveToFile()
        }


        @JvmStatic
        fun saveToFile() {
            // remove deleted config
            val iterator = loaded.switch.iterator()
            while (iterator.hasNext()) {
                val switch = iterator.next()
                if (!switch.config.exists()) {
                    iterator.remove()
                }
            }
            FileManager.writeJsonTofile(
                File(getRootDir(), "Switch.json"),
                loaded
            )
        }

        @JvmStatic
        fun initMenu(): Parent {


            val anchorRoot = AnchorPane()
            anchorRoot.id = "anchorRoot"
            anchorRoot.padding = Insets(20.0)
            anchorRoot.setPrefSize(334.0, 540.0)

            val switchStackPane = StackPane()

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

                var loadedProcess = ""
                for(j in loaded.switch){
                    if(j.config == files[i]){
                        loadedProcess = j.checkName
                        break
                    }
                }

                textField.text = loadedProcess

                textField.textProperty().addListener { _, _, newValue ->
                    if(newValue == "") {
                        for (j in loaded.switch) {
                            if (j.config == files[i]) {
                                loaded.switch.remove(j)
                                break
                            }
                        }
                    } else{
                        var isFound = false
                        for (j in loaded.switch) {
                            if (j.config == files[i]) {
                                j.checkName = newValue
                                isFound = true
                                break
                            }
                        }
                        if(!isFound){
                            loaded.switch.add(Switch(newValue,files[i]))
                        }
                    }
                }

                textField.maxWidth = 140.0

                val editCfgButton = Button("Edit")

                editCfgButton.setOnAction {
                    Settings.getINSTANCE().loadedConfig = files[i]
                    Script.loadScriptFromJson()

                    val root = SceneManager.getDefaultConfigParent()
                    switchStackPane.children.add(root)
                    val animation = SlideInDown(root)
                    animation.setOnFinished {
                        if (switchStackPane.children.contains(anchorRoot)) {
                            switchStackPane.children.remove(anchorRoot)
                        }
                    }
                    anchorRoot.children.forEach{
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
                saveToFile()
                ConfigManager.showDialog(false) {
                    refreshUI(switchStackPane)
                }
            }

            val settingsButton = Button()
            settingsButton.setOnAction {
                val root = loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/Settings.fxml").root
                switchStackPane.children.add(root)
                val animation = SlideInUp(root)
                animation.setOnFinished {
                    switchStackPane.children.remove(anchorRoot)
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




            AnchorPane.setTopAnchor(CDiscordRPHBox, 0.0)
            AnchorPane.setLeftAnchor(CDiscordRPHBox, 0.0)
            AnchorPane.setRightAnchor(CDiscordRPHBox, 0.0)

            AnchorPane.setTopAnchor(scrollPane, 60.0)
            AnchorPane.setBottomAnchor(scrollPane, 80.0)
            AnchorPane.setLeftAnchor(scrollPane, 0.0)
            AnchorPane.setRightAnchor(scrollPane, 0.0)

            AnchorPane.setBottomAnchor(controlVbox, 0.0)
            AnchorPane.setLeftAnchor(controlVbox, 0.0)
            AnchorPane.setRightAnchor(controlVbox, 0.0)

            anchorRoot.children.addAll(CDiscordRPHBox, scrollPane, controlVbox)
            switchStackPane.children.add(anchorRoot)


            switchStackPane.stylesheets.add(Settings.getINSTANCE().theme.path)

            return switchStackPane

        }

        private fun refreshUI(stackpane: StackPane) {
            loadFromFile()
            stackpane.children.clear()
            val newRoot = initMenu()
            stackpane.children.add(newRoot)
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