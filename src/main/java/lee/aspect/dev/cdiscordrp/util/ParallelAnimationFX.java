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

package lee.aspect.dev.cdiscordrp.util;

import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import lee.aspect.dev.cdiscordrp.animatefx.AnimationFX;

/**
 * @author Loïc Sculier aka typhon0
 */
public class ParallelAnimationFX {

    private final ObservableList<AnimationFX> animations = FXCollections.observableArrayList();
    private Animation.Status status;
    private ObjectProperty<Node> node;
    private boolean reset;

    public ParallelAnimationFX(Node node) {
        nodeProperty().set(node);
        reset = false;
    }

    public ParallelAnimationFX(Node node, AnimationFX... animations) {
        nodeProperty().set(node);
        getAnimation().setAll(animations);
        reset = false;
    }

    public ParallelAnimationFX(AnimationFX... animations) {
        getAnimation().setAll(animations);
        reset = false;
    }

    public ParallelAnimationFX() {
    }

    /**
     * The node property
     *
     * @return node property
     */
    public final ObjectProperty<Node> nodeProperty() {
        if (node == null) {
            node = new SimpleObjectProperty<>(this, "node", null);
        }
        return node;
    }

    /**
     * A list of {@link AnimationFX Animations} that will be
     * played in parallel.
     * <p>
     * It is not possible to change the animatefx of a running
     * {@code ParallelAnimationFX}. If the animatefx are changed for a running
     * {@code ParallelAnimationFX}, the animatefx has to be stopped and started
     * again to pick up the new value.
     */
    public final ObservableList<AnimationFX> getAnimation() {
        return animations;
    }


    /**
     * Set the node
     *
     * @param value the node
     */
    public void setNode(Node value) {
        if ((node != null) || (value != null)) {
            nodeProperty().set(value);
        }
    }

    /**
     * Play the animations
     */
    public void play() {
        initAnimations();
        getAnimation().get(0).play();
        status = Animation.Status.RUNNING;
    }

    /**
     * Initialize which animatefx to play after another
     */
    private void initAnimations() {
        for (AnimationFX animation : animations) {
            if (nodeProperty().get() != null) {
                if (animation.getNode() == null) {
                    animation.setNode(nodeProperty().get());
                }
            }
            animation.play();
        }
    }

    /**
     * Stop the animations
     */
    public void stop() {
        status = Animation.Status.STOPPED;
        for (AnimationFX animationFX : animations) {
            animationFX.stop();
        }
    }

    /**
     * Get the status
     *
     * @return animations status
     */
    public Animation.Status getStatus() {
        return status;
    }

    /**
     * Set if the node have to reset at the end
     *
     * @param value true to reset
     */
    public void setResetOnFinished(boolean value) {
        this.reset = value;
    }
}


