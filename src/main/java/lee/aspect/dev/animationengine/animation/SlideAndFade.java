package lee.aspect.dev.animationengine.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class SlideAndFade extends AnimationFX{

    private double y;


    public SlideAndFade(final Node node, double y){
        super(node);
        this.y = y;
        getTimeline().getKeyFrames().addAll(
                //new KeyFrame(Duration.millis(0),
                    //new KeyValue(getNode().translateYProperty(), node.getLayoutY(),Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().translateYProperty(), y,Interpolator.LINEAR)
                ));
    }
    @Override
    AnimationFX resetNode() {
        getNode().setTranslateY(0);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline());
    }
}
