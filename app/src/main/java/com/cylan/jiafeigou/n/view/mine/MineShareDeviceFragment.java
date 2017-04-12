package com.cylan.jiafeigou.n.view.mine;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.misc.JConstant;
import com.cylan.jiafeigou.n.mvp.contract.mine.MineShareDeviceContract;
import com.cylan.jiafeigou.n.mvp.impl.mine.MineShareDevicePresenterImp;
import com.cylan.jiafeigou.n.mvp.model.DeviceBean;
import com.cylan.jiafeigou.n.mvp.model.RelAndFriendBean;
import com.cylan.jiafeigou.n.view.activity.BindDeviceActivity;
import com.cylan.jiafeigou.n.view.adapter.MineShareDeviceAdapter;
import com.cylan.jiafeigou.support.log.AppLogger;
import com.cylan.jiafeigou.support.superadapter.OnItemClickListener;
import com.cylan.jiafeigou.support.superadapter.internal.SuperViewHolder;
import com.cylan.jiafeigou.utils.NetUtils;
import com.cylan.jiafeigou.utils.ToastUtil;
import com.cylan.jiafeigou.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * 作者：zsl
 * 创建时间：2016/9/5
 * 描述：
 */
@RuntimePermissions
public class MineShareDeviceFragment extends Fragment implements MineShareDeviceContract.View {

    @BindView(R.id.recycle_share_device_list)
    RecyclerView recycleShareDeviceList;
    @BindView(R.id.ll_no_device)
    LinearLayout llNoDevice;

    private MineShareDeviceContract.Presenter presenter;
    private MineDevicesShareManagerFragment mineDevicesShareManagerFragment;
    private MineShareToFriendFragment shareToRelativeAndFriendFragment;
    private MineShareToContactFragment mineShareToContactFragment;
    private AlertDialog alertDialog;
    private MineShareDeviceAdapter adapter;
    private DeviceBean whichClick;
    private int position;
    private boolean dataHasChange;

    public static MineShareDeviceFragment newInstance() {
        return new MineShareDeviceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_share_device, container, false);
        ButterKnife.bind(this, view);
        initPresenter();
        showLoadingDialog();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.start();
        }
    }

    private void initPresenter() {
        presenter = new MineShareDevicePresenterImp(this);
    }

    @Override
    public void setPresenter(MineShareDeviceContract.Presenter presenter) {

    }

    @Override
    public void showShareDialog(final int layoutPosition, final DeviceBean item) {
        whichClick = item;
        position = layoutPosition;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = View.inflate(getContext(), R.layout.fragment_home_mine_share_devices_dialog, null);
        view.findViewById(R.id.tv_share_to_timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getView() != null)
                    ViewUtils.deBounceClick(getView().findViewById(R.id.tv_share_to_wechat_friends));
                AppLogger.e("tv_share_to_friends");
                jump2ShareToFriendFragment(layoutPosition, item);
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_share_to_contract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getView() != null)
                    ViewUtils.deBounceClick(getView().findViewById(R.id.tv_share_to_contract));
                AppLogger.d("tv_share_to_contract");
                MineShareDeviceFragmentPermissionsDispatcher.onReadContactsPermissionWithCheck(MineShareDeviceFragment.this);
                alertDialog.dismiss();
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * desc；跳转到通过联系人分享的界面
     */
    private void jump2ShareToContractFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("deviceinfo", whichClick);
        bundle.putParcelableArrayList("sharefriend", presenter.getJFGInfo(position));
        mineShareToContactFragment = MineShareToContactFragment.newInstance(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right
                        , R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, mineShareToContactFragment, "mineShareToContactFragment")
                .addToBackStack("mineShareDeviceFragment")
                .commit();
    }

    /**
     * desc:跳转到通过亲友分享
     */
    private void jump2ShareToFriendFragment(int position, DeviceBean item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("deviceinfo", item);
        bundle.putParcelableArrayList("hasSharefriend", presenter.getJFGInfo(position));
        shareToRelativeAndFriendFragment = MineShareToFriendFragment.newInstance(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right
                        , R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, shareToRelativeAndFriendFragment, "shareToRelativeAndFriendFragment")
                .addToBackStack("mineShareDeviceFragment")
                .commit();

        shareToRelativeAndFriendFragment.setOnShareSucceedListener(new MineShareToFriendFragment.OnShareSucceedListener() {
            @Override
            public void shareSucceed(int num, ArrayList<RelAndFriendBean> list) {
                if (num == 0) return;
                adapter.getItem(position).hasShareCount += num;
                adapter.notifyDataSetChanged();
                presenter.shareSucceedAdd(position,list);
            }
        });
    }

    @Override
    public void initRecycleView(ArrayList<DeviceBean> list) {
        hideLoadingDialog();
        recycleShareDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MineShareDeviceAdapter(getView().getContext(), list, null);
        recycleShareDeviceList.setAdapter(adapter);
        initAdaListener();
    }

    /**
     * 列表适配器的监听的器
     */
    private void initAdaListener() {
        adapter.setOnShareClickListener(new MineShareDeviceAdapter.OnShareClickListener() {
            @Override
            public void onShare(SuperViewHolder holder, int viewType, int layoutPosition, DeviceBean item) {
                if (NetUtils.getNetType(getContext()) == -1) {
                    ToastUtil.showNegativeToast(getString(R.string.OFFLINE_ERR_1));
                    return;
                }
                showShareDialog(layoutPosition, item);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int viewType, int position) {
                if (getView() != null) {
                    ViewUtils.deBounceClick(itemView);
                    AppLogger.e("tv_share_device_manger");
                    jump2ShareDeviceMangerFragment(adapter.getList().get(position), position);
                }
            }
        });
    }

    /**
     * 跳转到分享管理界面
     *
     * @param bean
     */
    @Override
    public void jump2ShareDeviceMangerFragment(DeviceBean bean, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("devicebean", bean);
        bundle.putParcelableArrayList("friendlist", presenter.getJFGInfo(position));
        mineDevicesShareManagerFragment = MineDevicesShareManagerFragment.newInstance(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right
                        , R.anim.slide_in_left, R.anim.slide_out_right)
                .add(android.R.id.content, mineDevicesShareManagerFragment, "mineDevicesShareManagerFragment")
                .addToBackStack("mineShareDeviceFragment")
                .commit();

        mineDevicesShareManagerFragment.setOncancleChangeListener(
                new MineDevicesShareManagerFragment.OnUnShareChangeListener() {
                    @Override
                    public void unShareChange(int num, ArrayList<String> arrayList) {
                        if (num == 0) return;
                        adapter.getItem(position).hasShareCount -= num;
                        adapter.notifyDataSetChanged();
                        presenter.unShareSucceedDel(position, arrayList);
                    }
                });
    }

    @Override
    public void showNoDeviceView() {
        llNoDevice.setVisibility(View.VISIBLE);
    }

    /**
     * 显示加载进度
     */
    @Override
    public void showLoadingDialog() {
//        LoadingDialog.showLoading(getFragmentManager(), getString(R.string.LOADING));
    }

    /**
     * 隐藏加载进度
     */
    @Override
    public void hideLoadingDialog() {
//        LoadingDialog.dismissLoading(getFragmentManager());
    }

    @OnClick(R.id.tv_toolbar_icon)
    public void onClick() {
        presenter.clearData();
        getFragmentManager().popBackStack();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.clearData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MineShareDeviceFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        if (permissions.length == 1) {
            if (TextUtils.equals(permissions[0], Manifest.permission.READ_CONTACTS) && grantResults[0] > -1) {
//                MineShareDeviceFragmentPermissionsDispatcher.onReadContactsPermissionWithCheck(this);
            }
        }
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    public void onReadContactsPermissionDenied() {
        setPermissionDialog(getString(R.string.Tap3_ShareDevice_Contacts));
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    public void onNeverAskAgainReadContactsPermission() {
        setPermissionDialog(getString(R.string.Tap3_ShareDevice_Contacts));
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    public void onReadContactsPermission() {
        jump2ShareToContractFragment();
    }


    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    public void showRationaleForCamera(PermissionRequest request) {
        AppLogger.d(JConstant.LOG_TAG.PERMISSION + "showRationaleForCamera");
        onNeverAskAgainReadContactsPermission();
    }

    public void setPermissionDialog(String permission) {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.permission_auth, "", permission))
                .setNegativeButton(getString(R.string.CANCEL), (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(getString(R.string.SETTINGS), (DialogInterface dialog, int which) -> {
                    openSetting();
                })
                .create()
                .show();
    }

    private void openSetting() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getContext().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getContext().getPackageName());
        }
        startActivity(localIntent);
    }
}
