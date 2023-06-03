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

package lee.aspect.dev.dynamicrp.animatefx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;


/**
 * @author Loïc Sculier aka typhon0
 */
public class SlideInDown extends AnimationFX {

    /**
     * Create new SlideInDown
     *
     * @param node The node to affect
     */
    public SlideInDown(Node node) {
        super(node);
    }

    public SlideInDown() {
    }

    @Override
    protected AnimationFX resetNode() {
        getNode().setTranslateY(0);
        return this;
    }

    @Override
    protected void initTimeline() {
        //getNode().getScene().getWindow().getHeight() - use this because the default method is buggy
        setTimeline(new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(getNode().translateYProperty(), -getNode().getScene().getWindow().getHeight(), AnimateFXInterpolator.EASE)
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(getNode().translateYProperty(), 0, AnimateFXInterpolator.EASE)
                )
        ));
    }
}