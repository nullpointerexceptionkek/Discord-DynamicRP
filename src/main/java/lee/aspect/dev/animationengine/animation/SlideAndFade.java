package lee.aspect.dev.animationengine.animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

public class SlideAndFade extends AnimationFX{

    private int y;

    public SlideAndFade(final Node node, double y){
        super(node);
        this.y = y;
    }
    @Override
    AnimationFX resetNode() {
        getNode().setTranslateY(0);
        getNode().setOpacity(1);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(100),
                        new KeyValue(getNode().opacityProperty(), 1, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(getNode().translateYProperty(), y/2, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().opacityProperty(), 1, AnimateFXInterpolator.EASE),
                        new KeyValue(getNode().translateYProperty(), y+y/4, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(getNode().opacityProperty(), 0.7, AnimateFXInterpolator.EASE),
                        new KeyValue(getNode().translateYProperty(), y, AnimateFXInterpolator.EASE)
                )
        ));
    }
}
