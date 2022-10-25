package lee.aspect.dev.animationengine.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class SlideAndFade extends AnimationFX {


    public SlideAndFade(final Node node, double y, double opacity) {
        super(node);
        getTimeline().getKeyFrames().addAll(
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().opacityProperty(), opacity, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().translateYProperty(), y, Interpolator.LINEAR)
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
