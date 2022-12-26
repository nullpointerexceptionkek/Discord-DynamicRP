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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lee.aspect.dev.animationengine.animation.FadeOut;
import lee.aspect.dev.animationengine.animation.RotateIn;
import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;

import java.util.Objects;

public class LoadingController {
    public static CallBackController callBackController;
    @FXML
    private ProgressIndicator progress;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorRoot;

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
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/CallBack.fxml"));
                                Parent root = loader.load();
                                stackPane.getChildren().add(0, root);
                                callBackController = loader.getController();
                                RotateIn animation = new RotateIn(root);
                                animation.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(anchorRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(anchorRoot)));
                                    fadeOut.play();
                                });
                                animation.play();
                                break;
                            case ConfigScreen:
                                Parent root1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                                stackPane.getChildren().add(0, root1);
                                RotateIn animation1 = new RotateIn(root1);
                                animation1.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(anchorRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(anchorRoot)));
                                    fadeOut.play();
                                });
                                animation1.play();
                                break;
                            default:
                                FXMLLoader loader2 = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                                Parent root2 = loader2.load();
                                ConfigController controller = loader2.getController();
                                stackPane.getChildren().add(0, root2);
                                RotateIn animation2 = new RotateIn(root2);
                                animation2.setOnFinished(actionEvent -> {
                                    FadeOut fadeOut = new FadeOut(anchorRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> {
                                        stackPane.getChildren().remove(anchorRoot);
                                        controller.invalidDiscordAppID("Unable to connect to Discord, please check this field.");
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
