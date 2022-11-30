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

package lee.aspect.dev.application.interfaceGui;


import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class WarningManager {

    public enum Mode{
        Left,Right,Up,Down
    }

    /**
     * Creates a warning icon
     * @return ImageView
     */
    public static ImageView setWarning(double x, double y, int size, String message){
        ImageView warning = new ImageView(Objects.requireNonNull(WarningManager.class.getResource("/lee/aspect/dev/icon/Warning.png")).toExternalForm());
        warning.setFitHeight(size);
        warning.setFitWidth(size);
        warning.setLayoutX(x);
        warning.setLayoutY(y);
        Tooltip.install(warning, new Tooltip(message));
        return warning;
    }
    /**
     * Creates a warning icon
     * @return ImageView
     */
    public static ImageView setWarning(Parent parent, int size, String message, Mode mode, double ofSetX, double ofSetY){
        switch(mode) {
            case Up:
                return setWarning(parent.getLayoutX() + parent.getLayoutBounds().getCenterX() + ofSetX,
                        parent.getLayoutY()-10 - size/2 + ofSetY, size,message);
            case Down:
                return setWarning(parent.getLayoutX() + parent.getLayoutBounds().getCenterX()+ ofSetX,
                        parent.getLayoutY()+10 + size/2+ ofSetY, size,message);
            case Left:
                return setWarning(parent.getLayoutX()-10 - size/2+ ofSetX,
                        parent.getLayoutY() + parent.getLayoutBounds().getCenterY() - size/2+ ofSetY, size,message);
            case Right:
                return setWarning(parent.getLayoutX()+10+ size/2+ ofSetX,
                        parent.getLayoutY() + parent.getLayoutBounds().getCenterY() - size/2+ ofSetY, size,message);
            default:
                return null;
        }
    }
    /**
     * Creates a warning icon
     * @return ImageView
     */
    public static ImageView setWarning(Parent p){
        return setWarning(p,16,"",Mode.Left);
    }
    /**
     * Creates a warning icon
     * @return ImageView
     */
    public static ImageView setWarning(Parent parent, int size, String message, Mode mode){
        return setWarning(parent,size,message,mode,0,0);
    }
    /**
     * Creates a alert with a checkbox
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
        //alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(WarningManager.class.getResource(Settings.getTheme().getThemepass())).toExternalForm());
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

}
