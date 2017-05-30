package com.cs.inje.of;

import android.view.animation.Animation;

public interface SlidingPageAnimationListener extends Animation.AnimationListener {
    @Override
    void onAnimationEnd(Animation animation);

    @Override
    void onAnimationStart(Animation animation);

    @Override
    void onAnimationRepeat(Animation animation);
}
