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

package lee.aspect.dev.dynamicrp.application.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lee.aspect.dev.dynamicrp.Launch;
import lee.aspect.dev.dynamicrp.animatefx.*;
import lee.aspect.dev.dynamicrp.application.core.*;
import lee.aspect.dev.dynamicrp.language.LanguageManager;
import lee.aspect.dev.dynamicrp.manager.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CallBackController implements Initializable {
    private final Label display1 = new Label();
    private final Label display2 = new Label();
    private final Label display3 = new Label();
    @FXML
    private Label playingLabel;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private Button switchToConfig;
    @FXML
    private Label titleLabel;
    @FXML
    private VBox content;

    private BounceOutRight pMoveUp;

    private BounceInLeft afterIn;

    public void switchToConfig() throws IOException {
        switchToConfig.setDisable(true);
        SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/LoadingScreen.fxml");

        FadeOut fadeOut = new FadeOut(anchorRoot);
        fadeOut.setOnFinished((actionEvent -> {
            stackPane.getChildren().remove(anchorRoot);
            FadeIn fadeIn = new FadeIn(sceneData.getRoot());
            fadeIn.setOnFinished((actionEvent1) -> {
                LoadingController lc = (LoadingController) sceneData.getController();
                lc.toNewScene(LoadingController.Load.ConfigScreen);
            });
            sceneData.getRoot().setOpacity(0);
            stackPane.getChildren().add(sceneData.getRoot());
            fadeIn.play();
        }));
        fadeOut.setSpeed(5);
        fadeOut.play();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //load languages
        playingLabel.setText(LanguageManager.lang.getString("playing"));
        switchToConfig.setText(LanguageManager.lang.getString("CloseCallBackButton"));

        switchToConfig.setDisable(false);
        Platform.runLater(() -> {
            setDefault(display1, -45);
            setDefault(display2, 0);
            setDefault(display3, 45);

            if (Script.getINSTANCE().getTotalupdates().size() == 1) {
                display2.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getFl()
                        + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getSl());
            } else {
                if (Script.getINSTANCE().getUpdateType().equals(Script.UpdateType.Random) || Settings.getINSTANCE().isNoAnimation()) {
                    display2.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getFl()
                            + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getSl());
                } else {
                    if (RunLoopManager.getCurrentDisplay() > 0)
                        display1.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay() - 1).getFl()
                                + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay() - 1).getSl());
                    display1.opacityProperty().set(0.3);
                    display2.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getFl()
                            + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getSl());
                    display3.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay() >= Script.getINSTANCE().getTotalupdates().size() - 1 ? 0 : RunLoopManager.getCurrentDisplay() + 1).getFl()
                            + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay() >= Script.getINSTANCE().getTotalupdates().size() - 1 ? 0 : RunLoopManager.getCurrentDisplay() + 1).getSl());
                    display3.opacityProperty().set(0.8);
                }
            }


            if (Script.getINSTANCE().getUpdateType().equals(Script.UpdateType.Random) || Settings.getINSTANCE().isNoAnimation()) {
                anchorRoot.getChildren().add(display2);
                return;
            }
            anchorRoot.getChildren().addAll(display1, display2, display3);
            pMoveUp = new BounceOutRight(display1);
            afterIn = new BounceInLeft(display3, 0.8);

        });
    }

    public void updateCurrentDisplay(Updates next) {
        if (Script.getINSTANCE().getUpdateType().equals(Script.UpdateType.Random) || Settings.getINSTANCE().isNoAnimation()) {
            display2.setText(Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getFl()
                    + '\n' + Script.getINSTANCE().getTotalupdates().get(RunLoopManager.getCurrentDisplay()).getSl());
            return;
        }
        String nextLine = next.getFl() + '\n' + next.getSl();
        if ((display1.getLayoutY() + display1.getTranslateY()) <= 230) {
            updateDisplayLabel(display1, display2, display3, nextLine);
        } else if ((display2.getLayoutY() + display2.getTranslateY()) <= 230) {
            updateDisplayLabel(display2, display3, display1, nextLine);
        } else if ((display3.getLayoutY() + display3.getTranslateY()) <= 230) {
            updateDisplayLabel(display3, display1, display2, nextLine);
        } else {
            Launch.LOGGER.error("Animation have encounter an error, this should not be occur");
        }


    }

    private void setDefault(Label display, int transY) {
        display.setPrefWidth(150);
        display.setPrefHeight(45);

        display.translateXProperty().bind(anchorRoot.widthProperty().subtract(display.widthProperty()).divide(2));
        display.translateYProperty().bind(anchorRoot.heightProperty().subtract(display.heightProperty()).divide(2).add(transY));

        display.setTextAlignment(TextAlignment.CENTER);
    }

    private void updateDisplayLabel(Label Display1, Label Display2, Label Display3, String next) {
        SlideAndFade CurrentUp, AfterUp;
        pMoveUp.setNode(Display1);
        pMoveUp.setOnFinished((actionEvent -> {
            Display1.setTranslateY(45);
            afterIn.setNode(Display1);
            Display1.setText(next);
            afterIn.play();
        }));
        pMoveUp.play();
        CurrentUp = new SlideAndFade(Display2, -45, 0.3);
        CurrentUp.play();
        AfterUp = new SlideAndFade(Display3, 0, 1);
        AfterUp.play();
    }

}
