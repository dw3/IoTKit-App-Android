package com.cylan.jiafeigou.n.view.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.n.mvp.contract.mine.MineRelativesAndFriendsAddFriendContract;
import com.cylan.jiafeigou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：zsl
 * 创建时间：2016/9/6
 * 描述：
 */
public class MineRelativesAndFriendAddFriendsFragment extends Fragment implements MineRelativesAndFriendsAddFriendContract.View {


    @BindView(R.id.iv_home_mine_relativesandfriends_add_back)
    ImageView ivHomeMineRelativesandfriendsAddBack;
    @BindView(R.id.et_friend_phonenumber)
    EditText etFriendPhonenumber;
    @BindView(R.id.tv_scan_add)
    TextView tvScanAdd;
    @BindView(R.id.tv_add_from_contract)
    TextView tvAddFromContract;

    public static MineRelativesAndFriendAddFriendsFragment newInstance() {
        return new MineRelativesAndFriendAddFriendsFragment();
    }

    @Override
    public void setPresenter(MineRelativesAndFriendsAddFriendContract.Presenter presenter) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_mine_relativesandfriends_addfriends, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.iv_home_mine_relativesandfriends_add_back, R.id.tv_scan_add, R.id.tv_add_from_contract})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_home_mine_relativesandfriends_add_back:        //返回
                getFragmentManager().popBackStack();
                break;

            case R.id.tv_scan_add:                                      //扫一扫添加
                ToastUtil.showToast(getContext(),"扫一扫添加...");
                break;

            case R.id.tv_add_from_contract:                             //从通讯录添加
                ToastUtil.showToast(getContext(),"从通讯录添加...");
                break;
        }
    }
}
