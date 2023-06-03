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

package lee.aspect.dev.dynamicrp.manager

import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import lee.aspect.dev.dynamicrp.application.controller.EditListController
import lee.aspect.dev.dynamicrp.application.core.Script
import lee.aspect.dev.dynamicrp.application.core.Settings
import lee.aspect.dev.dynamicrp.application.core.Updates
import java.util.*

class SearchManager {
    companion object{
        private val searchModes = listOf(
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
        private lateinit var tableView: TableView<Updates>

        @JvmStatic
        fun showDialog() {
            val dialog = createDialog()
            val textInput = createTextInput()
            val searchChoiceBox = createSearchChoiceBox()
            tableView = createTableView()

            val inputBox = HBox(10.0, textInput, searchChoiceBox).apply {
                HBox.setHgrow(textInput, Priority.ALWAYS)
            }
            val content = VBox(10.0, inputBox, tableView)

            dialog.dialogPane.content = content
            setupDialogResultConverter(dialog)
            setupTextInputListener(textInput, searchChoiceBox, tableView)

            dialog.dialogPane.stylesheets.add(Settings.getINSTANCE().theme.path)
            dialog.show()
        }

        private fun createDialog(): Dialog<String> {
            val dialog = Dialog<String>()
            dialog.title = "Search"
            dialog.headerText = null
            dialog.dialogPane.buttonTypes.addAll(ButtonType.OK)
            val stage = (dialog.dialogPane.scene.window as Stage)
            dialog.isResizable = true
            stage.icons.add(Image(Objects.requireNonNull(Companion::class.java.getResourceAsStream("/lee/aspect/dev/dynamicrp/icon/search.png"))))
            return dialog
        }

        private fun createTextInput() = TextField()

        private fun createSearchChoiceBox(): ChoiceBox<String> {
            val searchChoiceBox = ChoiceBox(FXCollections.observableList(searchModes))
            searchChoiceBox.selectionModel.select(0)
            return searchChoiceBox
        }

        private fun createTableView(): TableView<Updates> {
            val tableView = TableView<Updates>()
            tableView.columns.addAll(createColumns())
            tableView.setRowFactory { createRowFactory() }
            tableView.items = Script.getINSTANCE().totalupdates
            return tableView
        }

        private fun createColumns(): List<TableColumn<Updates, *>> = listOf(
            createColumn("First Line", "fl"),
            createColumn("Second Line", "sl"),
            createColumn("Delay", "wait"),
            createColumn("Large Img", "image"),
            createColumn("Large Img Text", "imagetext"),
            createColumn("Small Img", "smallimage"),
            createColumn("Small Img Text", "smalltext"),
            createColumn("Button 1", "button1Text"),
            createColumn("Button 1 URL", "button1Url"),
            createColumn("Button 2", "button2Text"),
            createColumn("Button 2 URL", "button2Url")
        )

        private fun createColumn(title: String, property: String): TableColumn<Updates, *> {
            val column = TableColumn<Updates, Any>(title)
            column.cellValueFactory = PropertyValueFactory(property)
            return column
        }

        private fun createRowFactory(): TableRow<Updates> {
            val row = TableRow<Updates>()
            val dialog = createDialog()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2 && !row.isEmpty) {
                    val originalItems = Script.getINSTANCE().totalupdates
                    EditListController.showListConfig(originalItems.indexOf(row.item), dialog.x, dialog.y)
                }
            }
            return row
        }

        private fun setupDialogResultConverter(dialog: Dialog<String>) {
            dialog.setResultConverter { buttonType ->
                if (buttonType == ButtonType.OK) {
                    dialog.close()
                }
                null
            }
        }

        private fun setupTextInputListener(textInput: TextField, searchChoiceBox: ChoiceBox<String>, tableView: TableView<Updates>) {
            textInput.textProperty().addListener { _, _, newValue ->
                val searchText = newValue.trim()
                if (searchText.isNotEmpty()) {
                    tableView.selectionModel.clearSelection()
                    val filterFunction = getFilterFunction(searchChoiceBox, searchText)
                    tableView.items = Script.getINSTANCE().totalupdates.filtered(filterFunction)
                } else {
                    tableView.selectionModel.clearSelection()
                    tableView.items = Script.getINSTANCE().totalupdates
                }
            }
        }

        private fun getFilterFunction(searchChoiceBox: ChoiceBox<String>, searchText: String): (Updates) -> Boolean {
            return when (searchChoiceBox.selectionModel.selectedItem) {
                "All" -> { item -> item.matches(searchText) }
                "First Line" -> { item -> item.fl.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Second Line" -> { item -> item.sl.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Delay" -> { item -> item.wait.toString().lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Large Img" -> { item -> item.image.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Large Img Text" -> { item -> item.imagetext.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Small Img" -> { item -> item.smallimage.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Small Img Text" -> { item -> item.smalltext.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Button 1" -> { item -> item.button1Text.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Button 1 URL" -> { item -> item.button1Url.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Button 2" -> { item -> item.button2Text.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                "Button 2 URL" -> { item -> item.button2Url.lowercase(Locale.ROOT).contains(searchText.lowercase(Locale.ROOT)) }
                else -> { _ -> false }
            }
        }
    }

}
