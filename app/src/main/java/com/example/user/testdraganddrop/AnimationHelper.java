package com.example.user.testdraganddrop;

import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by User on 7/4/2015.
 */
public class AnimationHelper {

    public static Animation createFastRotateAnimation() {
        Animation rotate = new RotateAnimation(-2.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(200);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());

        return rotate;
    }

    private TranslateAnimation createTranslateAnimation(Point oldOffset,
                                                        Point newOffset) {
        TranslateAnimation translate = new TranslateAnimation(
                Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
                newOffset.x, Animation.ABSOLUTE, oldOffset.y,
                Animation.ABSOLUTE, newOffset.y);
        translate.setDuration(300);
        translate.setFillEnabled(true);
        translate.setFillAfter(true);
        translate.setInterpolator(new AccelerateDecelerateInterpolator());
        return translate;
    }

    public static Animation createSlideAndRotate(boolean slide2Right) {
        AnimationSet animationSet = new AnimationSet(true);
        final float fromX = slide2Right ? -1.0f : 1.0f;
        final float toX = slide2Right ? 0.0f : 0.0f;

        TranslateAnimation in = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX,
                Animation.RELATIVE_TO_SELF, toX, 0, 0.0f, 0, 0.0f);

        in.setDuration(300);
        animationSet.addAnimation(in);
        animationSet.addAnimation(createFastRotateAnimation());
        return animationSet;
    }
}
