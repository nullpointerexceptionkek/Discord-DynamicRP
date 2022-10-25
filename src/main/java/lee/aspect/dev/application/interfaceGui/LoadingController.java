package lee.aspect.dev.application.interfaceGui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
                    } catch (NoDiscordClientException e) {
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
                                var loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/CallBack.fxml"));
                                Parent root = loader.load();
                                stackPane.getChildren().add(0,root);
                                callBackController = loader.getController();
                                var animation = new RotateIn(root);
                                animation.setOnFinished(actionEvent -> {
                                    var fadeOut = new FadeOut(anchorRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(anchorRoot)));
                                    fadeOut.play();
                                });
                                animation.play();
                                break;
                            default:
                                Parent root1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                                stackPane.getChildren().add(0,root1);
                                var animation1 = new RotateIn(root1);
                                animation1.setOnFinished(actionEvent -> {
                                    var fadeOut = new FadeOut(anchorRoot);
                                    fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(anchorRoot)));
                                    fadeOut.play();
                                });
                                animation1.play();
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
