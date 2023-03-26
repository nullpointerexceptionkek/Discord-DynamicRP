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

package lee.aspect.dev.cdiscordrp.application.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lee.aspect.dev.cdiscordrp.manager.SceneManager;
import lee.aspect.dev.cdiscordrp.animatefx.FadeOut;
import lee.aspect.dev.cdiscordrp.animatefx.RotateIn;
import lee.aspect.dev.cdiscordrp.application.core.RunLoopManager;
import lee.aspect.dev.cdiscordrp.exceptions.NoDiscordClientException;

import java.util.Objects;

public class LoadingController {
    public static CallBackController callBackController;
    @FXML
    private ProgressIndicator progress;

    @FXML
    private VBox vRoot;

    @FXML
    private StackPane stackPane;

    private Load file = Load.Error;

    public void toNewScene(Load file) {
        this.file = file;
        new SplashScreen().start();

    }

    public enum Load {
        ConfigScreen,
        CallBackScreen,
        Error
    }

    class SplashScreen extends Thread {
        @Override
        public void run() {
            switch (file) {
                case ConfigScreen:
                    RunLoopManager.closeCallBack();
                    break;
                case CallBackScreen:
                    try {
                        RunLoopManager.startUpdate();
                    } catch (NoDiscordClientException | RuntimeException e) {
                        e.printStackTrace();
                        file = Load.Error;
                    }
                    break;
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        switch (file) {
                            case CallBackScreen:
                                Parent root = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/CallBack.fxml").getRoot();
                                stackPane.getChildren().add(0, root);
                                RotateIn animation = new RotateIn(root);
                                animation.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(vRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(vRoot)));
                                    fadeOut.play();
                                });
                                animation.play();
                                break;
                            case ConfigScreen:
                                Parent cfgRoot = SceneManager.getConfigParent();
                                stackPane.getChildren().add(0, cfgRoot);
                                RotateIn animation1 = new RotateIn(cfgRoot);
                                animation1.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(vRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(vRoot)));
                                    fadeOut.play();
                                });
                                animation1.play();
                                break;
                            default:
                                SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/cdiscordrp/scenes/ReadyConfig.fxml");
                                stackPane.getChildren().add(0, sceneData.getRoot());
                                RotateIn animation2 = new RotateIn(sceneData.getRoot());
                                animation2.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(vRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> {
                                        stackPane.getChildren().remove(vRoot);
                                        ((ConfigController)sceneData.getController()).invalidDiscordAppID("Unable to connect to Discord, please check this field.");
                                    }));
                                    fadeOut.play();
                                });
                                animation2.play();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
