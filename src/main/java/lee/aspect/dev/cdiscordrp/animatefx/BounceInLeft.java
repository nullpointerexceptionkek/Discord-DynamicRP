/*
 *
 * MIT License
 *
 * Copyright (c) 2023 lee
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

package lee.aspect.dev.cdiscordrp.animatefx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author Loïc Sculier aka typhon0
 */
public class BounceInLeft extends AnimationFX {

    private double Opac;

    /**
     * Create new BounceInLeft
     *
     * @param node The node to affect
     */
    public BounceInLeft(Node node, double endOpac) {
        super(node);
        this.Opac = endOpac;
    }

    public BounceInLeft() {
    }

    @Override
    AnimationFX resetNode() {
        getNode().setOpacity(1);
        getNode().setTranslateX(0);
        return this;
    }

    @Override
    void initTimeline() {
        double startX = -getNode().localToScene(0, 0).getX() - getNode().getBoundsInParent().getWidth();
        setTimeline(
                new Timeline(
                        new KeyFrame(Duration.millis(0),
                                new KeyValue(getNode().opacityProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)),
                                new KeyValue(getNode().translateXProperty(), startX, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                        ),
                        new KeyFrame(Duration.millis(600),
                                new KeyValue(getNode().opacityProperty(), Opac, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000)),
                                new KeyValue(getNode().translateXProperty(), 25, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                        ),
                        new KeyFrame(Duration.millis(750),
                                new KeyValue(getNode().translateXProperty(), -10, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                        ),
                        new KeyFrame(Duration.millis(900),
                                new KeyValue(getNode().translateXProperty(), 5, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                        ),
                        new KeyFrame(Duration.millis(1000),
                                new KeyValue(getNode().translateXProperty(), 0, Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000))
                        )

                ));
    }

}

