// License under Apache License 2.0 - https://github.com/Typhon0/AnimateFX/blob/master/LICENSE
package lee.aspect.dev.dynamicrp.animatefx;

import javafx.animation.Interpolator;

public final class AnimateFXInterpolator {

    public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);

    private AnimateFXInterpolator() {
        throw new IllegalStateException("AnimateFX Interpolator");
    }

}
