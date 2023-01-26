/*
 * Original code is License under Apache License 2.0 - https://github.com/Typhon0/AnimateFX/blob/master/LICENSE
 */

package lee.aspect.dev.cdiscordrp.animatefx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author Loïc Sculier aka typhon0
 */

public class FadeIn extends AnimationFX {

    /**
     * Create a new FadeIn animatefx
     *
     * @param node the node to affect
     */
    public FadeIn(Node node) {
        super(node);
    }

    public FadeIn() {
    }

    @Override
    AnimationFX resetNode() {
        getNode().setOpacity(1);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(getNode().opacityProperty(), 1, AnimateFXInterpolator.EASE)
                )

        ));
    }
}

