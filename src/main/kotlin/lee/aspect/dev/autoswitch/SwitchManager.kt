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

package lee.aspect.dev.autoswitch

import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import lee.aspect.dev.JProcessDetector.OpenCloseListener
import lee.aspect.dev.JProcessDetector.ProcessMonitor
import lee.aspect.dev.application.CustomDiscordRPC
import lee.aspect.dev.application.RunLoopManager
import lee.aspect.dev.config.ConfigManager
import lee.aspect.dev.discordrpc.UpdateManager
import lee.aspect.dev.discordrpc.settings.SettingManager
import java.util.*
import java.util.concurrent.TimeUnit


abstract class SwitchManager {


    companion object {
        @JvmStatic
        fun initMenu(): Parent {


            val anchorRoot = AnchorPane()
            anchorRoot.id = "defaultPane"
            val switchStackPane = StackPane()
            //switchStackPane.id = "defaultPane"
            switchStackPane.padding = Insets(30.0)
            switchStackPane.setPrefSize(334.0,540.0)

            val scrollPane = ScrollPane()

            val fileNames = ConfigManager.getCurrentConfigFiles()
            fileNames!!

            val vboxtext = VBox()
            vboxtext.spacing = 10.0
            vboxtext.alignment = Pos.CENTER_LEFT


            val vboxtextbox = VBox()
            vboxtextbox.spacing = 10.0
            vboxtextbox.alignment = Pos.CENTER_RIGHT
            val input = Array(fileNames.size) { "" }

            for (i in fileNames.indices) {
                val fileName = fileNames[i].name.substring(0, fileNames[i].name.indexOf("_UpdateScript.json"))
                val text = Label(fileName)
                text.maxWidth(180.0)
                text.font = Font.font(16.0)
                vboxtext.children.add(text)

                val textField = TextField()
                input[i] = textField.text
                textField.textProperty().addListener { _, _, newValue ->
                    input[i] = newValue
                }


                textField.maxWidth = 140.0

                val editCfgButton = Button("Edit")

                editCfgButton.setOnAction {
                    SettingManager.SETTINGS.loadedConfig= fileNames[i]
                    UpdateManager.SCRIPT = UpdateManager.loadScriptFromJson()

                    val root = FXMLLoader.load<Parent>(
                        Objects.requireNonNull(
                            CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")
                        )
                    )
                    root.stylesheets.add(
                        Objects.requireNonNull(CustomDiscordRPC::class.java.getResource(SettingManager.SETTINGS.theme.path))
                            .toExternalForm()
                    )

                    println(anchorRoot.children)

                    if(anchorRoot.children.contains(switchStackPane)){
                        anchorRoot.children.remove(switchStackPane)
                    }
                    if(anchorRoot.children.contains(scrollPane)){
                        anchorRoot.children.remove(scrollPane)
                    }
                    anchorRoot.children.add(root)

                }

                val hbox = HBox(editCfgButton,textField)
                hbox.spacing = 10.0
                hbox.alignment = Pos.CENTER_RIGHT

                vboxtextbox.children.addAll(hbox)
            }

            val statusLabel = Label("Not Connected")
            statusLabel.textAlignment = TextAlignment.CENTER

            val startButton = Button("Start Operation")
            var isStarted = false

            val configManagerButton = Button()
            configManagerButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            val jsonIcon = ImageView(Objects.requireNonNull(CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/icon/json-file.png")).toExternalForm())
            jsonIcon.fitHeight = 17.0
            jsonIcon.fitWidth = 17.0
            configManagerButton.graphic = jsonIcon

            val settingsButton = Button()
            configManagerButton.contentDisplay = ContentDisplay.GRAPHIC_ONLY
            val settingsIcon = ImageView(Objects.requireNonNull(CustomDiscordRPC::class.java.getResource("/lee/aspect/dev/icon/settingsImage.png")).toExternalForm())

            settingsIcon.fitHeight = 17.0
            settingsIcon.fitWidth = 17.0
            settingsButton.graphic = settingsIcon

            val controlHBbox = HBox(settingsButton,startButton,configManagerButton)
            controlHBbox.spacing = 10.0
            controlHBbox.alignment = Pos.BOTTOM_CENTER
            controlHBbox.isPickOnBounds = false

            startButton.setOnAction {
                startButton.isDisable = true
                if(!isStarted) {
                    println(input.contentToString())
                    startButton.text = "Stop Operation"
                    isStarted = true
                    statusLabel.text = "Initializing..."
                    for(i in fileNames.indices){
                        if(input[i].isNotEmpty())
                            ProcessMonitor().startMonitoring(input[i], object : OpenCloseListener {
                                override fun onProcessOpen() {
                                    try {
                                        RunLoopManager.closeCallBack()
                                    } catch (_: Exception) {
                                    }
                                    SettingManager.SETTINGS.loadedConfig = fileNames[i]
                                    UpdateManager.SCRIPT = UpdateManager.loadScriptFromJson()
                                    RunLoopManager.startUpdate()
                                    Platform.runLater{
                                        statusLabel.text = "${input[i]} Process Opened"
                                    }
                                }

                                override fun onProcessClose() {
                                    try {
                                        RunLoopManager.closeCallBack()
                                    }catch (_: Exception) {
                                    }
                                    Platform.runLater{
                                        statusLabel.text = "${input[i]} Process Closed"
                                    }
                                }
                            }, 3, TimeUnit.SECONDS)
                    }


                } else{
                    statusLabel.text = "Disconnecting..."
                    try {
                        RunLoopManager.closeCallBack()
                    }catch (_: Exception) {
                    }
                    statusLabel.text = "Not Connected"
                    startButton.text = "Start Operation"
                    isStarted = false
                }

                startButton.isDisable = false

            }

            val controlVbox = VBox(controlHBbox,statusLabel)

            controlVbox.spacing = 5.0

            controlVbox.alignment = Pos.BOTTOM_CENTER

            controlVbox.isPickOnBounds = false

            switchStackPane.children.addAll(vboxtext, vboxtextbox,controlVbox)


            anchorRoot.stylesheets.add(SettingManager.SETTINGS.theme.path)

            if (fileNames.size > switchStackPane.prefHeight / 12) {
                scrollPane.content = switchStackPane
                scrollPane.isFitToWidth = true
                anchorRoot.children.add(scrollPane)
            } else{
                anchorRoot.children.add(switchStackPane)
            }
            return anchorRoot

        }
    }


}