package lee.aspect.dev.application.interfaceGui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lee.aspect.dev.animationengine.animation.FadeOut;
import lee.aspect.dev.animationengine.animation.RotateIn;
import lee.aspect.dev.application.RunLoopManager;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoadingController implements Initializable {
    public static CallBackController callBackController;
    @FXML
    private ProgressIndicator progress;

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorRoot;

    private Load file;

    private Long sleep;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        new SplashScreen().start();
    }

    public void toNewScene(long sleep, Load file) {
        this.sleep = sleep;
        this.file = file;

    }

    public void toNewScene(Load file) {
        this.file = file;

    }

    public enum Load {
        ConfigScreen,
        CallBackScreen,
        Error
    }

    class SplashScreen extends Thread {

        @Override
        public void run() {
            if (file == null) {
                file = Load.Error;
            }
            try {
                switch (file) {
                    case CallBackScreen:
                        try {
                            RunLoopManager.initCallBack();
                            RunLoopManager.startUpdate();
                        } catch (RuntimeException e) {
                            file = Load.Error;
                            break;
                        }

                        break;
                    case ConfigScreen:
                        try {
                            RunLoopManager.closeCallBack();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Thread.sleep(sleep != null ? sleep : 1000);
                }
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            switch (file) {
                                case CallBackScreen:
                                    var loader = new FXMLLoader(getClass().getResource("/lee/aspect/dev/scenes/CallBack.fxml"));
                                    Parent root = loader.load();
                                    stackPane.getChildren().add(root);
                                    callBackController = loader.getController();
                                    var animation = new RotateIn(root);
                                    animation.setCycleCount(1).setDelay(Duration.valueOf("100ms"));
                                    animation.setOnFinished(actionEvent -> {
                                        var fadeOut = new FadeOut(anchorRoot);
                                        fadeOut.setOnFinished((actionEvent1 -> stackPane.getChildren().remove(anchorRoot)));
                                        fadeOut.play();
                                    });
                                    animation.play();
                                    break;
                                case ConfigScreen:
                                    Parent root1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                                    Scene scene1 = anchorRoot.getScene();

                                    root1.translateYProperty().set(scene1.getHeight());
                                    stackPane.getChildren().add(root1);

                                    Timeline timeline1 = new Timeline();
                                    KeyValue keyValue1 = new KeyValue(root1.translateYProperty(), 0, Interpolator.EASE_OUT);
                                    KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(1), keyValue1);
                                    timeline1.getKeyFrames().add(keyFrame1);
                                    timeline1.setOnFinished(event1 -> stackPane.getChildren().remove(anchorRoot));
                                    timeline1.play();
                                    break;
                                default:
                                    Parent root2 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/lee/aspect/dev/scenes/ReadyConfig.fxml")));
                                    Scene scene2 = anchorRoot.getScene();

                                    root2.translateYProperty().set(scene2.getHeight());
                                    stackPane.getChildren().add(root2);

                                    Timeline timeline2 = new Timeline();
                                    KeyValue keyValue2 = new KeyValue(root2.translateYProperty(), 0, Interpolator.EASE_OUT);
                                    KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1), keyValue2);
                                    timeline2.getKeyFrames().add(keyFrame2);
                                    timeline2.setOnFinished(event1 -> stackPane.getChildren().remove(anchorRoot));
                                    timeline2.play();
                                    System.err.println(file);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
