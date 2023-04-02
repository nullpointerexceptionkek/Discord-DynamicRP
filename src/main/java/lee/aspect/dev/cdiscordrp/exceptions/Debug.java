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

package lee.aspect.dev.cdiscordrp.exceptions;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Used for development purposes only, does not affect the program in any way.
 */
@SuppressWarnings("unused") // This class is only used for debugging purposes
public class Debug {

    private static final boolean DEBUG = true;

    private Debug() {
        throw new IllegalStateException("Utility class");
    }

    public static void checkStylesheets(Parent parent) {
        if (!DEBUG) return;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement callingMethod = stackTrace[2];
        String className = callingMethod.getClassName();
        String methodName = callingMethod.getMethodName();
        System.out.println(className + "." + methodName);
        try {
            System.out.println("Debug.checkStylesheet(" + parent.getId() + "):");
            System.out.println("----------------------------------------");
            System.out.println("Parent has " + parent.getStylesheets().size() + " stylesheets applied.");
            for (Node node : parent.getChildrenUnmodifiable()) {
                // Get the stylesheets applied to the node
                Scene scene = node.getScene();
                if (scene != null) {
                    int stylesheetCount = node.getScene().getStylesheets().size();
                    System.out.println(node.getClass().getSimpleName() + " has " + stylesheetCount + " stylesheets applied.");
                }
            }
            System.out.println("----------------------------------------");
        } catch (Exception e) {
            System.err.println("Exception occurred while running Debug.checkStylesheets: " + e.getMessage());
        }
    }
}
