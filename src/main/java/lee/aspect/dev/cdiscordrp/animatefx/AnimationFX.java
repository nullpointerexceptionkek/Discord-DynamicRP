/*
 * Original code is License under Apache License 2.0 - https://github.com/Typhon0/AnimateFX/blob/master/LICENSE
 * Modification is made to skip the animation, License under MIT License
 */

package lee.aspect.dev.cdiscordrp.animatefx;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;
import lee.aspect.dev.cdiscordrp.application.core.Settings;

/**
 * @author Loïc Sculier aka typhon0
 */
public abstract class AnimationFX {

    /**
     * Used to specify an animatefx that repeats indefinitely, until the
     * {@code stop()} method is called.
     */
    public static final int INDEFINITE = -1;
    private Timeline timeline;
    private boolean reset;
    private Node node;
    private AnimationFX nextAnimation;
    private boolean hasNextAnimation;


    /**
     * Create a new animatefx
     *
     * @param node the node to affect
     */
    public AnimationFX(Node node) {
        super();
        setNode(node);

    }

    /**
     * Default constructor
     */
    public AnimationFX() {
        hasNextAnimation = false;
        this.reset = false;


    }

    /**
     * Handle when the animatefx is finished
     *
     * @return
     */
    private AnimationFX onFinished() {
        if (reset) {
            resetNode();
        }
        if (this.nextAnimation != null) {
            this.nextAnimation.play();
        }
        return this;
    }

    /**
     * Set the next animatefx to play
     *
     * @param animation
     * @return
     */
    public AnimationFX playOnFinished(AnimationFX animation) {
        setNextAnimation(animation);
        return this;

    }

    /**
     * Play the animatefx
     */
    public void play() {
        if (Settings.getINSTANCE().isNoAnimation()) {
            // Disable animation and set node to final value
            timeline.stop();
            timeline.jumpTo(timeline.getCycleDuration());
            resetNode();
            if (this.nextAnimation != null) {
                this.nextAnimation.play();
            }
            if (timeline.getOnFinished() != null) {
                timeline.getOnFinished().handle(null);
            }
        } else {
            // Play animation
            timeline.play();
        }
    }


    /**
     * Stop the animatefx
     *
     * @return
     */
    public AnimationFX stop() {
        timeline.stop();
        return this;
    }

    /**
     * Function the reset the node to original state
     *
     * @return
     */
    abstract AnimationFX resetNode();

    /**
     * Function to initialize the timeline
     */
    abstract void initTimeline();

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public boolean isResetOnFinished() {
        return reset;
    }

    /**
     * Function to reset the node or not when the animatefx is finished
     *
     * @param reset
     * @return
     */
    public AnimationFX setResetOnFinished(boolean reset) {
        this.reset = reset;
        return this;
    }

    protected void setReset(boolean reset) {
        this.reset = reset;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
        initTimeline();
        timeline.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Animation.Status.STOPPED)) {
                onFinished();
            }

        });
    }

    public AnimationFX getNextAnimation() {
        return nextAnimation;
    }

    protected void setNextAnimation(AnimationFX nextAnimation) {
        hasNextAnimation = true;
        this.nextAnimation = nextAnimation;
    }

    public boolean hasNextAnimation() {
        return hasNextAnimation;
    }

    protected void setHasNextAnimation(boolean hasNextAnimation) {
        this.hasNextAnimation = hasNextAnimation;
    }

    /**
     * Define the number of cycles in this animatefx
     *
     * @param value
     * @return
     */
    public AnimationFX setCycleCount(int value) {
        this.timeline.setCycleCount(value);
        return this;
    }

    /**
     * Set the speed factor of the animatefx
     *
     * @param value
     * @return
     */
    public AnimationFX setSpeed(double value) {
        this.timeline.setRate(value);
        return this;
    }

    /**
     * Delays the start of an animatefx
     *
     * @param value
     * @return
     */
    public AnimationFX setDelay(Duration value) {
        this.timeline.setDelay(value);
        return this;
    }

    /**
     * Set event when the animatefx ended.
     *
     * @param value
     */
    public final void setOnFinished(EventHandler<ActionEvent> value) {
        this.timeline.setOnFinished(value);
    }

}
