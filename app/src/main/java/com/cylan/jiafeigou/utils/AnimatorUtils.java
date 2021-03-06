package com.cylan.jiafeigou.utils;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.cylan.jiafeigou.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by lxh on 16-6-16.
 */
public class AnimatorUtils {

    /**
     * view 的垂直移动动画
     *
     * @param view     view
     * @param isShow   是否显示
     * @param delay    延迟
     * @param start    开始位置
     * @param end      结束位置
     * @param duration 持续时间
     */
    public static void viewTranslationY(View view, boolean isShow, long delay, float start, float end, int duration) {
        ObjectAnimator an;
        if (isShow) {
            an = ObjectAnimator.ofFloat(view, "translationY", start, end);
            an.setInterpolator(new OvershootInterpolator());
        } else {
            an = ObjectAnimator.ofFloat(view, "translationY", start, end);
        }
        an.addListener(new showViewListener(view, isShow));
        if (delay > 0) {
            an.setStartDelay(delay);
        }
        an.setDuration(duration).start();
    }

    public static void shake(View view) {
//        view.animate().setInterpolator(CycleInterpolator)

    }


    public interface OnFinish {
        void onFinish();
    }

    public static void slideIn(View target, boolean down, boolean autoHide) {

        int height = target.getHeight();
        if (height == 0) height = 300;
        final float start = target.isShown() ? 0.0f : (down ? height : -height);
        final float end = target.isShown() ? (down ? height : -height) : 0.0f;
        final boolean shouldGone = target.isShown();
        if (autoHide && target.isShown()) {
            ObjectAnimator autoShowAnim = ObjectAnimator.ofFloat(target, "translationY", down ? height : -height,
                    0.0f);
            autoShowAnim.setStartDelay(3000);
            autoShowAnim.setInterpolator(new AccelerateInterpolator());
        }
        ObjectAnimator o = ObjectAnimator.ofFloat(target, "translationY", start, end);
        o.setDuration(200);
        o.setInterpolator(new AccelerateInterpolator());
        o.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!target.isShown())
                    target.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (shouldGone)
                    target.setVisibility(View.INVISIBLE);
            }
        });
        o.start();
    }

    public static void slide(View target, boolean down, OnFinish listener) {

        int height = target.getHeight();
        if (height == 0) height = 300;
        final float start = target.isShown() ? 0.0f : (down ? height : -height);
        final float end = target.isShown() ? (down ? height : -height) : 0.0f;
        final boolean shouldGone = target.isShown();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "translationY", start, end));
        set.setDuration(200);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!target.isShown())
                    target.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (shouldGone)
                    target.setVisibility(View.INVISIBLE);
                if (listener != null) listener.onFinish();
            }
        });
        set.start();
    }

    public static void slide(final View target, final OnEndListener listener) {
        int height = target.getHeight();
        if (height == 0) height = 300;
        final float start = target.isShown() ? 0.0f : height;
        final float end = target.isShown() ? height : 0.0f;
        final boolean shouldGone = target.isShown();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "translationY", start, end));
        set.setDuration(300);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!target.isShown())
                    target.setVisibility(View.VISIBLE);
                if (listener != null) {
                    listener.onAnimationStart(target.isShown());
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (shouldGone)
                    target.setVisibility(View.GONE);
                if (listener != null) listener.onAnimationEnd(shouldGone);
            }
        });
        set.start();
    }

    public interface OnEndListener {
        void onAnimationEnd(boolean gone);

        void onAnimationStart(boolean gone);
    }

    public static void slide(final View target) {
        int height = target.getHeight();
        if (height == 0) height = 300;
        final float start = target.isShown() ? 0.0f : height;
        final float end = target.isShown() ? height : 0.0f;
        final boolean shouldGone = target.isShown();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "translationY", start, end));
        set.setDuration(200);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!target.isShown())
                    target.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (shouldGone)
                    target.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    public static class SimpleAnimationListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    /**
     * view 的水平移动动画
     *
     * @param view     view
     * @param isShow   是否显示
     * @param delay    延迟
     * @param start    开始位置
     * @param end      结束位置
     * @param duration 持续时间
     */
    public static void viewTranslationX(View view, boolean isShow, long delay, float start, float end, int duration) {
        ObjectAnimator an;
        if (isShow) {
            an = ObjectAnimator.ofFloat(view, "translationX", start, end);
            an.setInterpolator(new OvershootInterpolator());
        } else {
            an = ObjectAnimator.ofFloat(view, "translationX", start, end);
        }
        an.addListener(new showViewListener(view, isShow));
        an.setDuration(duration);
        if (delay > 0) {
            an.setStartDelay(delay);
        }
        an.start();

    }


    /**
     * 以中心为原点缩放view
     *
     * @param view
     * @param isShow
     */
    public static void viewScaleCenter(View view, boolean isShow, int duration, int delay) {
        AnimatorSet set = new AnimatorSet();
        if (isShow) {
            set.playTogether(ObjectAnimator.ofFloat(view, "scale", 0f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                    ObjectAnimator.ofFloat(view, "alpha", 0f, 1f));
        } else {
            set.playTogether(ObjectAnimator.ofFloat(view, "scale", 1f, 0f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
                    ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
        }
        set.setDuration(duration).addListener(new showViewListener(view, isShow));
        if (delay > 0) {
            set.setStartDelay(delay);
        }
        set.start();
    }


    public static void viewAlpha(View view, boolean isShow, int duration, int delay) {
        ObjectAnimator an;
        if (isShow) {
            an = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        } else {
            an = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        }

        an.setDuration(duration).addListener(new showViewListener(view, isShow));
        if (delay > 0) {
            an.setStartDelay(delay);
        }
        an.start();
    }


    /**
     * 动画监听器
     */
    static class showViewListener implements Animator.AnimatorListener {

        private boolean isShow;
        private View view;

        public showViewListener(View view, boolean isShow) {
            this.isShow = isShow;
            this.view = view;
        }

        // 在开始时，先显示view
        @Override
        public void onAnimationStart(Animator animator) {
            if (isShow) {
                view.setVisibility(View.VISIBLE);
            }
        }

        //如果要隐藏的view 在结束时隐藏
        @Override
        public void onAnimationEnd(Animator animator) {
            if (!isShow) {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    public static void onSimpleBounceUpIn(View target, final long duration, final long delay) {
        target.setTranslationY(800);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "translationY", 800, -30.0F, 0.0F));
//                ObjectAnimator.ofFloat(target, "alpha", 0.0F, 1.0F, 1.0F, 1.0F));
        set.setDuration(duration);
        set.setStartDelay(delay);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    public static void onSimpleTangle(final long duration, final long delay, View target) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "scaleY", 1.0F, 1.1F, 1.0F),
                ObjectAnimator.ofFloat(target, "scaleX", 1.0F, 1.1F, 1.0F));
        set.setDuration(duration);
        set.setInterpolator(new OvershootInterpolator());
        set.setStartDelay(delay);
        set.start();
    }


    public static AnimationDrawable onWiFiLightFlash(ImageView target) {
        target.setBackgroundResource(R.drawable.camera_wifi_light_flash);
        Drawable drawable = target.getBackground();
        if (drawable instanceof AnimationDrawable) {
            return ((AnimationDrawable) drawable);
        }
        return null;
    }

    public static ObjectAnimator slideRight(View target) {

        int right = 200;
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationX", right, 0);
        animator.setDuration(600);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public static void slideInRight(View target) {
        if (!target.isShown()) target.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) target.getParent();
        int distance = parent.getWidth() - target.getLeft();
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationX", (float) distance, 0.0F);
        animator.setDuration(250);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static void slideOutRight(final View target) {
        if (target.getLeft() != target.getX()) return;
        if (!target.isShown()) target.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) target.getParent();
        int distance = parent.getWidth() - target.getLeft();
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationX", 0.0F, (float) distance);
        animator.setDuration(250);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public static Animator onHandMoveAndFlash(final View hand, final View redot,
                                              final ImageView flash) {
        ObjectAnimator slideIn = slideRight(hand);
        slideIn.setStartDelay(1000);
        slideIn.setDuration(500);
        slideIn.setRepeatCount(ValueAnimator.INFINITE);
        slideIn.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                hand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                redot.setVisibility(View.VISIBLE);
                redot.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //可能会内存泄露
                        AnimationDrawable dAnim = onWiFiLightFlash(flash);
                        if (dAnim != null) dAnim.start();
                    }
                }, 1000);

            }
        });
        return slideIn;
    }

    public static Animator onHandMoveAndFlashPanorama(final View hand, final View redot,
                                                      final ImageView flash, View bg) {
        Animator slideIn = slideRight(hand);
        slideIn.setStartDelay(1000);
        slideIn.setDuration(500);
        slideIn.addListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                hand.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                bg.setVisibility(View.VISIBLE);
                redot.setVisibility(View.VISIBLE);
                android.animation.ObjectAnimator alpha = android.animation.ObjectAnimator.ofFloat(flash, "alpha", 0, 1);
                alpha.setDuration(500);
                alpha.setRepeatMode(android.animation.ValueAnimator.REVERSE);
                alpha.setRepeatCount(3);
                alpha.start();
            }
        });
        return slideIn;
    }

    public static AnimatorSet scale(View target, Animator.AnimatorListener animatorListener) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(target, "scale", 0.9f, 1.1f, 1.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 0.9f, 1.1f, 1.0f),
                ObjectAnimator.ofFloat(target, "alpha", 0f, 1f));
        set.setDuration(300);
        if (animatorListener != null)
            set.addListener(animatorListener);
        return set;
    }

    public static AnimatorSet onHand2Left(final View hand, Animator.AnimatorListener animatorListener) {
        AnimatorSet setLeft = new AnimatorSet();
        final int endX0 = -(((ViewGroup) hand.getParent()).getWidth() / 2 - hand.getWidth() / 2) + 10;
        final int endY = -(((ViewGroup) hand.getParent()).getHeight() / 4 - hand.getHeight() / 2) - 10;
        Animator translateX0 = ObjectAnimator.ofFloat(hand, "translationX", 0, endX0);
        Animator translateY0 = ObjectAnimator.ofFloat(hand, "translationY", 0, endY);
        Animator alpha = ObjectAnimator.ofFloat(hand, "alpha", 0.0f, 1.0f);
        setLeft.playTogether(translateX0, translateY0, alpha);
        setLeft.setInterpolator(new DecelerateInterpolator());
        setLeft.setDuration(1000);
        if (animatorListener != null)
            setLeft.addListener(animatorListener);
        return setLeft;
    }

    public static AnimatorSet onHand2Right(final View hand, Animator.AnimatorListener animatorListener) {
        final AnimatorSet setRight = new AnimatorSet();
        final int startX = -(((ViewGroup) hand.getParent()).getWidth() / 2 - hand.getWidth() / 2) + 10;
        final int endX1 = ((ViewGroup) hand.getParent()).getWidth() / 2 - hand.getWidth() + 10;
        final int endY = -(((ViewGroup) hand.getParent()).getHeight() / 4 - hand.getHeight() / 2) - 10;
//        Animator alpha = ObjectAnimator.ofFloat(hand, "alpha", 0.0f, 1.0f);
        Animator translateX1 = ObjectAnimator.ofFloat(hand, "translationX", startX, endX1);
        Animator translateY1 = ObjectAnimator.ofFloat(hand, "translationY", endY, endY);
        setRight.playTogether(translateX1, translateY1);
        setRight.setInterpolator(new DecelerateInterpolator());
        setRight.setDuration(1000);
        if (animatorListener != null)
            setRight.addListener(animatorListener);
        return setRight;
    }

    /**
     * 切出屏幕
     *
     * @param view
     * @param fromTop
     */
    public static void slideOut(View view, boolean fromTop) {
        Object o = view.getTag();
        if (o != null && o instanceof ObjectAnimator) {
            ObjectAnimator animator = (ObjectAnimator) o;
            if (animator.isRunning() || animator.isStarted())
                animator.cancel();
        }
        int start = 0, end;
        if (!fromTop) {
            ViewGroup parent = (ViewGroup) view.getParent();
            end = parent.getHeight() - view.getTop();
        } else {
            end = -view.getBottom();
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", start, end);
        objectAnimator.setDuration(250);
        view.setTag(objectAnimator);
        if (!view.isShown()) view.setVisibility(View.VISIBLE);
        objectAnimator.start();
    }

    /**
     * 切出屏幕
     *
     * @param view
     * @param fromTop
     */
    public static void slideIn(final View view, boolean fromTop) {
        Object o = view.getTag();
        if (o != null && o instanceof ObjectAnimator) {
            ObjectAnimator animator = (ObjectAnimator) o;
            if (animator.isRunning() || animator.isStarted())
                animator.cancel();
        }
        int start, end = 0;
        if (!fromTop) {
            ViewGroup parent = (ViewGroup) view.getParent();
            start = parent.getHeight() - view.getTop();
//            view.setTranslationY(-view.getTop());
        } else {
            start = -(view.getTop() + view.getHeight());
        }
        view.clearAnimation();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY", start, end);
        objectAnimator.setDuration(250);
        view.setTag(objectAnimator);
        if (!view.isShown()) view.setVisibility(View.VISIBLE);
        objectAnimator.start();
    }

    /**
     * 自动呼入呼出
     * 1.view的初始状态是Invisible
     * 2.view移动距离是view的高度
     *
     * @param view
     * @param fromTop:紧贴顶部,false:紧贴底部
     */
    public static void slideAuto(final View view, boolean fromTop) {
        if (fromTop) {
            if (view.getBottom() == view.getTop() - view.getY())
                slideIn(view, true);
            if (view.getY() == view.getTop())
                slideOut(view, true);
        }
        if (!fromTop) {
            ViewGroup parent = (ViewGroup) view.getParent();
            int end = parent.getHeight() - view.getTop();
            if (end + view.getTop() == view.getY())
                slideIn(view, false);
            if (view.getTop() == view.getY())
                slideOut(view, false);
        }
    }

    /**
     * @param view
     * @return
     */
    public static boolean isReset(View view) {
        return view.getTop() == view.getY();
    }

    public static int getSlideOutYDistance(View view) {
        return -view.getBottom();
    }

    public static int getSlideOutXDistance(View view) {
        return (int) view.getTranslationX();
    }

    public static ObjectAnimator toCenterX(View view, int offset) {
        int endX = ((ViewGroup) view.getParent()).getWidth() / 2 - view.getLeft() - view.getWidth() / 2 + offset;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, endX);
        animator.setInterpolator(new DecelerateInterpolator());
        if (!view.isShown()) view.setVisibility(View.VISIBLE);
        return animator;
    }

    public static ObjectAnimator toCenterY(View view, int offset) {
        int endY = ((ViewGroup) view.getParent()).getHeight() / 2 - view.getTop() - view.getHeight() / 2 + offset;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, endY);
        animator.setInterpolator(new DecelerateInterpolator());
        if (!view.isShown()) view.setVisibility(View.VISIBLE);
        return animator;
    }
}
