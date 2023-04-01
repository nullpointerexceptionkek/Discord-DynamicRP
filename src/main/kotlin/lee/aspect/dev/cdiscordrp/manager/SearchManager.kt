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

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Updates
import java.util.*

class SearchManager {

    companion object {
        private val searchMode = listOf(
            "All",
            "First Line",
            "Second Line",
            "Delay",
            "Large Img",
            "Large Img Text",
            "Small Img",
            "Small Img Text",
            "Button 1",
            "Button 1 URL",
            "Button 2",
            "Button 2 URL"
        )

        @JvmStatic
        fun showDialog() {
            val dialog = Dialog<String>()
            dialog.title = "Search"
            dialog.headerText = "Find an item in the list"

            val textInput = TextField()

            val searchChoiceBox = ChoiceBox(FXCollections.observableList(searchMode))
            searchChoiceBox.selectionModel.select(0)

            val tableView = TableView<Updates>()
            val firstLineColumn = TableColumn<Updates, String>("First Line")
            firstLineColumn.cellValueFactory = PropertyValueFactory("fl")

            val secondLineColumn = TableColumn<Updates, String>("Second Line")
            secondLineColumn.cellValueFactory = PropertyValueFactory("sl")

            val delayColumn = TableColumn<Updates, Long>("Delay")
            delayColumn.cellValueFactory = PropertyValueFactory("wait")

            val largeImgColumn = TableColumn<Updates, String>("Large Img")
            largeImgColumn.cellValueFactory = PropertyValueFactory("image")

            val largeImgTextColumn = TableColumn<Updates, String>("Large Img Text")
            largeImgTextColumn.cellValueFactory = PropertyValueFactory("imagetext")

            val smallImgColumn = TableColumn<Updates, String>("Small Img")
            smallImgColumn.cellValueFactory = PropertyValueFactory("smallimage")

            val smallImgTextColumn = TableColumn<Updates, String>("Small Img Text")
            smallImgTextColumn.cellValueFactory = PropertyValueFactory("smalltext")

            val button1Column = TableColumn<Updates, String>("Button 1")
            button1Column.cellValueFactory = PropertyValueFactory("button1Text")

            val button1URLColumn = TableColumn<Updates, String>("Button 1 URL")
            button1URLColumn.cellValueFactory = PropertyValueFactory("button1Url")

            val button2Column = TableColumn<Updates, String>("Button 2")
            button2Column.cellValueFactory = PropertyValueFactory("button2Text")

            val button2URLColumn = TableColumn<Updates, String>("Button 2 URL")
            button2URLColumn.cellValueFactory = PropertyValueFactory("button2Url")

            tableView.columns.addAll(firstLineColumn, secondLineColumn, delayColumn, largeImgColumn, largeImgTextColumn, smallImgColumn, smallImgTextColumn, button1Column, button1URLColumn, button2Column, button2URLColumn)

            val items: ObservableList<Updates> = FXCollections.observableList(Script.getINSTANCE().totalupdates)
            tableView.items = items

            val content = VBox()
            content.children.addAll(textInput, searchChoiceBox, tableView)

            val okButton = ButtonType.OK
            dialog.dialogPane.buttonTypes.addAll(okButton)

            dialog.dialogPane.content = content

            dialog.setResultConverter { buttonType ->
                if (buttonType == okButton) {
                    dialog.close()
                }
                null
            }
            textInput.textProperty().addListener { _, _, newValue ->
                val searchText = newValue.trim()
                if (searchText.isNotEmpty()) {
                    when(searchChoiceBox.selectionModel.selectedItem){
                        "All" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item -> item.matches(searchText) }
                        }
                        "First Line" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.fl.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Second Line" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.sl.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Delay" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.wait.toString().lowercase(Locale.ROOT)
                                    .contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Large Img" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.image.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Large Img Text" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.imagetext.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Small Img" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.smallimage.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Small Img Text" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.smalltext.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Button 1" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.button1Text.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Button 1 URL" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.button1Url.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Button 2" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.button2Text.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                        "Button 2 URL" -> {
                            tableView.selectionModel.clearSelection()
                            tableView.items = items.filtered { item ->
                                item.button2Url.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT))
                            }
                        }
                    }
                } else {
                    tableView.selectionModel.clearSelection()
                    tableView.items = items
                }
            }

            dialog.show()
        }

    }
}
