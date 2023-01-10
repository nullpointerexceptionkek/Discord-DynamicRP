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
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import lee.aspect.dev.config.ConfigManager


abstract class SwitchManager {

    companion object {
        @JvmStatic
        fun initMenu(): Parent {
            val fileNames = ConfigManager.getCurrentConfigFiles()
            val vbox = VBox()

            if (fileNames != null) {
                for (file in fileNames) {
                    val hbox = HBox()
                    val text = Text(file.name)
                    val textField = TextField()
                    hbox.children.addAll(text, textField)
                    vbox.children.add(hbox)
                }
            }

            val scrollPane = ScrollPane()
            scrollPane.content = vbox

            val stackPane = StackPane()
            stackPane.padding = Insets(10.0)
            stackPane.setPrefSize(334.0,540.0)

            if (fileNames!!.size > stackPane.height / 30) {
                // Show the scrollbar
                scrollPane.isFitToWidth = true
                stackPane.children.add(scrollPane)
            } else {
                // Don't show the scrollbar
                stackPane.children.add(vbox)
            }


            println(stackPane.prefHeight)

            return stackPane
        }
    }


}