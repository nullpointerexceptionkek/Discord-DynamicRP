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
    }
    @Override
    AnimationFX resetNode() {
        getNode().setTranslateY(0);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
               // new KeyFrame(Duration.millis(0),
                       // new KeyValue(getNode().translateYProperty(), getNode().getLayoutY(),AnimateFXInterpolator.EASE)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().translateYProperty(), y,Interpolator.EASE_IN)
                )
        ));
    }
}
