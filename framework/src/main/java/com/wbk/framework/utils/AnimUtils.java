package com.wbk.framework.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 动画工具类
 */
public class AnimUtils {

    /**
     * 旋转动画
     */
    public static ObjectAnimator rotation(View view) {
        ObjectAnimator mAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        mAnimator.setDuration(2 * 1000);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
        return mAnimator;
    }
}
