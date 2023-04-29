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

package lee.aspect.dev.cdiscordrp.util;


import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import lee.aspect.dev.cdiscordrp.exceptions.FileNotAJarException;
import lee.aspect.dev.cdiscordrp.system.SystemHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.function.Consumer;

public class WarningManager {

    private WarningManager() {
    }

    /**
     * Creates a warning icon
     *
     * @return ImageView
     */
    public static ImageView setWarning(Parent parent, int size, String message, Mode mode) {
        double x, y;
        Bounds bounds = parent.getLayoutBounds();
        switch (mode) {
            case Up:
                x = parent.getLayoutX() + bounds.getMinX() + (bounds.getWidth() / 2);
                y = parent.getLayoutY() - 10 - size / 2;
                break;
            case Down:
                x = parent.getLayoutX() + bounds.getMinX() + (bounds.getWidth() / 2);
                y = parent.getLayoutY() + bounds.getHeight() + 10 + size / 2;
                break;
            case Left:
                x = parent.getLayoutX() - 10 - size / 2;
                y = parent.getLayoutY() + bounds.getMinY() + bounds.getHeight() / 2 - size / 2;
                break;
            default:
                x = parent.getLayoutX() + bounds.getWidth() + 10 + size / 2;
                y = parent.getLayoutY() + bounds.getMinY() + bounds.getHeight() / 2 - size / 2;
                break;
        }
        ImageView warning = new ImageView(Objects.requireNonNull(WarningManager.class.getResource("/lee/aspect/dev/cdiscordrp/icon/Warning.png")).toExternalForm());
        warning.setPickOnBounds(true);
        warning.setFitHeight(size);
        warning.setFitWidth(size);
        warning.setLayoutX(x);
        warning.setLayoutY(y);
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(javafx.util.Duration.millis(0));
        Tooltip.install(warning, tooltip);
        return warning;
    }


    /**
     * Creates a alert with a checkbox
     *
     * @return Alert
     */
    public static Alert createAlertWithOptOut(Alert.AlertType type, String title, String headerText,
                                              String message, String optOutMessage, Consumer<Boolean> optOutAction,
                                              ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        // Need to force the alert to layout in order to grab the graphic,
        // as we are replacing the dialog pane with a custom pane
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        // Create a new dialog pane that has a checkbox instead of the hide/show details button
        // Use the supplied callback for the action of the checkbox
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox();
                optOut.setText(optOutMessage);
                optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
                return optOut;
            }
        });
        //alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(WarningManager.class.getResource(Settings.getTheme().getPath())).toExternalForm());
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setContentText(message);
        // Fool the dialog into thinking there is some expandable content
        // a Group won't take up any space if it has no children
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(false);
        // Reset the dialog graphic using the default style
        alert.getDialogPane().setGraphic(graphic);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert;
    }

    public static void restartToApplyChanges() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ButtonType yesButton = new ButtonType("Yes restart now", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);
        alert.setTitle("Apply changes");
        alert.setHeaderText("Some changes need the application \n to restart inorder to apply");
        alert.setContentText("would you like to restart now?");
        ButtonType result = alert.showAndWait().get();
        if (result.equals(yesButton)) {
            try {
                SystemHandler.fullRestart();
            } catch (URISyntaxException | IOException | FileNotAJarException e) {
                Alert alertException = new Alert(Alert.AlertType.ERROR);
                alertException.setTitle("Exception");
                alertException.setHeaderText("We have encounter an exception");
                alertException.setContentText("FileNotAJarException, this occurs is because you are using an IDE to run the application");
                alertException.show();
            }
        }
    }

    public enum Mode {
        Left, Right, Up, Down
    }


}
