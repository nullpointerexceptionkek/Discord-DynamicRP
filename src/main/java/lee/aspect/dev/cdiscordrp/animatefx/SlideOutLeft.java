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
public class SlideOutLeft extends AnimationFX {

    /**
     * Create new SlideOutLeft
     *
     * @param node The node to affect
     */
    public SlideOutLeft(Node node) {
        super(node);
    }

    public SlideOutLeft() {
    }

    @Override
    AnimationFX resetNode() {
        getNode().setOpacity(1);
        getNode().setTranslateX(0);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(getNode().translateXProperty(), 0, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(getNode().translateXProperty(), -getNode().getBoundsInParent().getWidth(), AnimateFXInterpolator.EASE)
                )
        ));
    }
}

