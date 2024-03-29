/*
 * Original code is License under Apache License 2.0 - https://github.com/Typhon0/AnimateFX/blob/master/LICENSE
 * Animation value is modified, License under MIT License
 */

package lee.aspect.dev.dynamicrp.animatefx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;


/**
 * @author Loïc Sculier aka typhon0
 */
public class Shake extends AnimationFX {

    /**
     * Create new Shake
     *
     * @param node The node to affect
     */
    public Shake(Node node) {
        super(node);
    }

    public Shake() {
    }

    @Override
    AnimationFX resetNode() {

        getNode().setTranslateX(0);
        return this;
    }

    @Override
    void initTimeline() {
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(getNode().translateXProperty(), 0, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(getNode().translateXProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(getNode().translateXProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(getNode().translateXProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(getNode().translateXProperty(), 10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(getNode().translateXProperty(), -10, AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(getNode().translateXProperty(), 0, AnimateFXInterpolator.EASE)
                )
        ));
    }
}



