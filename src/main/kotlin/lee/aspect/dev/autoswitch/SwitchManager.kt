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

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import lee.aspect.dev.config.ConfigManager
import lee.aspect.dev.discordrpc.settings.SettingManager


abstract class SwitchManager {

    companion object {
        @JvmStatic
        fun initMenu(): Parent {
            val fileNames = ConfigManager.getCurrentConfigFiles()

            val vboxtext = VBox()
            vboxtext.spacing = 10.0
            vboxtext.alignment = Pos.CENTER_LEFT
            vboxtext.border =
                Border(
                    BorderStroke(
                        Color.RED,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths(2.0)
                    )
                )

            val vboxtextbox = VBox()
            vboxtextbox.spacing = 10.0
            vboxtextbox.alignment = Pos.CENTER_RIGHT
            vboxtextbox.border =
                Border(
                    BorderStroke(
                        Color.YELLOW,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths(2.0)
                    )
                )

            if (fileNames != null) {
                for (file in fileNames) {
                    val fileName = file.name.substring(0, file.name.indexOf("_UpdateScript.json"))
                    val text = Text(fileName)
                    text.maxWidth(180.0)
                    text.font = Font.font(16.0)
                    vboxtext.children.add(text)

                    val textField = TextField()
                    textField.maxWidth = 140.0

                    val editCfgButton = Button("Edit")

                    val hbox = HBox(editCfgButton,textField)
                    hbox.spacing = 10.0
                    hbox.alignment = Pos.CENTER_RIGHT
                    vboxtextbox.children.addAll(hbox)
                }
            }



            val switchStackPane = StackPane()
            switchStackPane.padding = Insets(30.0)
            switchStackPane.setPrefSize(334.0,540.0)
            switchStackPane.children.addAll(vboxtext, vboxtextbox)
            switchStackPane.border =
                Border(
                    BorderStroke(
                        Color.GREEN,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths(2.0)
                    )
                )
            switchStackPane.stylesheets.add(SettingManager.SETTINGS.theme.path)

            if (fileNames!!.size > switchStackPane.height / 30) {
                // Show the scrollbar
                val scrollPane = ScrollPane()
                scrollPane.content = switchStackPane
                scrollPane.isFitToWidth = true
                return scrollPane
            }
            return switchStackPane

        }
    }


}