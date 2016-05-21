package com.cylan.jiafeigou.n.view.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.n.presenter.TestPresenter;
import com.cylan.jiafeigou.n.view.BaseView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class HomeMineFragment extends Fragment implements BaseView {

    @BindView(R.id.fLayout_main_content_holder)
    PtrClassicFrameLayout fLayoutMainContentHolder;

    public static HomeMineFragment newInstance(Bundle bundle) {
        HomeMineFragment fragment = new HomeMineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if (getArguments() != null) {
            bundle = getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_mine, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TestPresenter testPresenter = new TestPresenter(this);
        testPresenter.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void initView() {

    }
}
