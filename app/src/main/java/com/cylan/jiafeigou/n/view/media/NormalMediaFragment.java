package com.cylan.jiafeigou.n.view.media;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.dp.DpMsgDefine;
import com.cylan.jiafeigou.misc.JConstant;
import com.cylan.jiafeigou.n.base.IBaseFragment;
import com.cylan.jiafeigou.support.photoview.PhotoView;
import com.cylan.jiafeigou.utils.CamWarnGlideURL;
import com.cylan.jiafeigou.utils.ContextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cylan.jiafeigou.misc.JConstant.KEY_SHARED_ELEMENT_LIST;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NormalMediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NormalMediaFragment extends IBaseFragment {

    public static final String KEY_INDEX = "key_index";
    public static final String KEY_UUID = "key_uuid";
    @BindView(R.id.imgV_show_pic)
    PhotoView imgVShowPic;


    public NormalMediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create activity_cloud_live_mesg_call_out_item new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle Parameter 1.
     * @return A new instance of fragment NormalMediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NormalMediaFragment newInstance(Bundle bundle) {
        NormalMediaFragment fragment = new NormalMediaFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //http://www.androiddesignpatterns.com/2015/03/activity-postponed-shared-element-transitions-part3b.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_normal_big_pic, container, false);
        ButterKnife.bind(this, view);
        // Postpone the shared element enter transition in onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().postponeEnterTransition();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int index = getArguments().getInt(KEY_INDEX);
        String uuid = getArguments().getString(KEY_UUID);
        DpMsgDefine.DPAlarm dpAlarm = getArguments().getParcelable(KEY_SHARED_ELEMENT_LIST);
        Glide.with(ContextUtils.getContext())
                .load(new CamWarnGlideURL(dpAlarm, index, uuid))
                .asBitmap()
                .placeholder(R.drawable.wonderful_pic_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgVShowPic);
        imgVShowPic.setOnViewTapListener((View v, float x, float y) -> {
            if (callBack != null) callBack.callBack(null);
        });
    }
}
