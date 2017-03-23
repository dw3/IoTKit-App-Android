package com.cylan.jiafeigou.widget.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.cylan.panorama.CameraParam;
import com.cylan.panorama.Panoramic720View;

/**
 * Created by cylan-hunt on 16-11-30.
 */

public class PanoramicView720_Ext extends Panoramic720View implements VideoViewFactory.IVideoView {

    public PanoramicView720_Ext(Context context) {
        super(context);
    }


    @Override
    public void config360(CameraParam param) {
    }

    @Override
    public void setMode(int mode) {
    }

    @Override
    public void setInterActListener(VideoViewFactory.InterActListener interActListener) {
    }


    @Override
    public void config720() {

    }

    @Override
    public boolean isPanoramicView() {
        return true;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void loadBitmap(Bitmap bitmap) {
        super.loadImage(bitmap);
    }

    @Override
    public void takeSnapshot() {

    }

    @Override
    public void performTouch() {
        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );
        // Dispatch touch event to view
        dispatchTouchEvent(motionEvent);
    }

    @Override
    public void detectOrientationChanged() {
        super.detectOrientationChange();
    }
}
