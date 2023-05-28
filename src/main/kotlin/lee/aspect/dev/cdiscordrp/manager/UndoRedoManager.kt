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

package lee.aspect.dev.cdiscordrp.manager

import lee.aspect.dev.cdiscordrp.application.core.Script
import lee.aspect.dev.cdiscordrp.application.core.Updates
import java.util.*

class UndoRedoManager(initialList: MutableList<Updates>) {
    private val maxStackSize: Int = 15
    private val undoStack = Stack<MutableList<Updates>>()
    private val redoStack = Stack<MutableList<Updates>>()
    private var currentList: MutableList<Updates> = initialList.toMutableList()

    init {
        undoStack.push(currentList)
    }

    fun undo() {
        if (undoStack.size <= 1) {
            return
        }
        redoStack.push(currentList)
        if (redoStack.size > maxStackSize) {
            redoStack.removeElementAt(0)
        }
        currentList = undoStack.pop()
        applyChangesToScript()
    }

    fun redo() {
        if (redoStack.isEmpty()) {
            return
        }
        undoStack.push(currentList)
        if (undoStack.size > maxStackSize) {
            undoStack.removeElementAt(0)
        }
        currentList = redoStack.pop()
        applyChangesToScript()
    }

    fun modifyList(list: MutableList<Updates>) {
        undoStack.push(currentList)
        if (undoStack.size > maxStackSize) {
            undoStack.removeElementAt(0)
        }
        currentList = list.toMutableList()
        redoStack.clear()
    }

    private fun applyChangesToScript() {
        val scriptInstance = Script.getINSTANCE()
        scriptInstance.totalupdates.clear()
        scriptInstance.totalupdates.addAll(currentList)
    }

    fun refresh() {
        undoStack.clear()
        redoStack.clear()
        undoStack.push(currentList)
    }
}

