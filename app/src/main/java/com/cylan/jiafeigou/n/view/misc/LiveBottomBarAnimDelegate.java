//package com.cylan.jiafeigou.n.view.misc;
//
///**
// * Created by cylan-hunt on 16-7-13.
// */
//
//import android.app.Activity;
//import android.content.pm.ActivityInfo;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.TextView;
//
//import com.cylan.jiafeigou.R;
//import com.cylan.jiafeigou.n.mvp.contract.cam.CamLiveContract;
//import com.cylan.jiafeigou.utils.TimeUtils;
//import com.cylan.jiafeigou.utils.ViewUtils;
//import com.cylan.jiafeigou.widget.flip.FlipImageView;
//import com.cylan.jiafeigou.widget.flip.FlipLayout;
//import com.cylan.jiafeigou.widget.flip.ISafeStateSetter;
//
//import java.lang.ref.WeakReference;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//
////
//// 安全防护   直播|5/16 12:30   全屏
////
//
///**
// * 这一个块的 show 和hide动画
// */
//public class LiveBottomBarAnimDelegate implements FlipImageView.OnFlipListener {
//    private WeakReference<ViewGroup> weakReference;
//    private WeakReference<Activity> activityWeakReference;
//    private CamLiveContract.Presenter presenter;
//    private TextView vLiveTime;
//
//    public LiveBottomBarAnimDelegate(Activity activity, ViewGroup view, CamLiveContract.Presenter presenter) {
//        weakReference = new WeakReference<>(view);
//        activityWeakReference = new WeakReference<>(activity);
//        this.presenter = presenter;
//        vLiveTime = (TextView) weakReference.get().findViewById(R.id.tv_live_time);
//        initFlipLayout();
//        initZoomLayout();
//    }
//
//    private void initZoomLayout() {
//        if (weakReference == null || weakReference.get() == null)
//            return;
//        View vZoomFullScreen = weakReference.get().findViewById(R.id.imgV_cam_zoom_to_full_screen);
//        //设置全屏点击
//        vZoomFullScreen.setOnClickListener((View v) -> {
//            if (activityWeakReference == null
//                    || activityWeakReference.get() == null)
//                return;
//            ViewUtils.setRequestedOrientation(activityWeakReference.get(),
//                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        });
//        vLiveTime.setOnClickListener((View v) -> {
//            if (liveTimeRectListener != null) liveTimeRectListener.click(v);
//        });
//    }
//
//    /**
//     * 安全防护
//     */
//    private void initFlipLayout() {
//        if (weakReference == null || weakReference.get() == null)
//            return;
//        iSafeStateSetter.setState();
//    }
//
//    /**
//     * 显示热区
//     *
//     * @param show
//     */
//    public void showLiveTimeRect(boolean show) {
//        if (!check())
//            return;
//        if (weakReference != null && weakReference.get() != null) {
//            vLiveTime.setVisibility(show ? View.VISIBLE : View.GONE);
//        }
//    }
//
//    /**
//     * 设置安全防护状态
//     *
//     * @param state
//     */
//    public void setProtectionState(boolean state) {
//        if (!check())
//            return;
//        if (vProtection.getFlipImageView() != null) {
//            boolean isFlipped = vProtection.getFlipImageView().isFlipped();//切换到背面
//            if ((isFlipped && state) || (!state && !isFlipped))
//                vProtection.getFlipImageView().performClick();
//            vProtection.getTextView().setText(state ? vProtection.getContext().getString(R.string.SECURE) : "");
//        }
//    }
//
//    /**
//     * 更新播放时间
//     *
//     * @param state 直播|历史录像|空闲  1:直播 2:录像
//     * @param startTime
//     */
//    public void setLiveTime(int state, long startTime) {
//        if (!check() || state == 0)
//            return;
//        String content = String.format(vLiveTime.getContext().getString(
//                state == 1 ? R.string.Tap1_Camera_VideoLive : R.string.Tap1_Camera_Playback)
//                + "|%s", state == 1 ? dateFormat.format(new Date(startTime)) :
//                TimeUtils.simpleDateFormat0.format(new Date(startTime)));
//        if (!vLiveTime.isShown()) vLiveTime.setVisibility(View.VISIBLE);
//        vLiveTime.setText(content);
//    }
//
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm",
//            Locale.getDefault());
//
//    private boolean check() {
//        return weakReference != null && weakReference.get() != null && activityWeakReference != null && activityWeakReference.get() != null;
//    }
//
//    @Override
//    public void onClick(FlipImageView view) {
//        presenter.saveAlarmFlag(!view.isFlipped());
//    }
//
//    @Override
//    public void onFlipStart(FlipImageView view) {
//
//    }
//
//    @Override
//    public void onFlipEnd(FlipImageView view) {
//
//    }
//
//    private LiveTimeRectListener liveTimeRectListener;
//
//    public void setLiveTimeRectListener(LiveTimeRectListener liveTimeRectListener) {
//        this.liveTimeRectListener = liveTimeRectListener;
//    }
//
//    public interface LiveTimeRectListener {
//        void click(View v);
//    }
//}