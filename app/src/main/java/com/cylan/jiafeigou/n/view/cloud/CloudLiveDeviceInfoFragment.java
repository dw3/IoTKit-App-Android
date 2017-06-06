package com.cylan.jiafeigou.n.view.cloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.cache.db.module.Device;
import com.cylan.jiafeigou.dp.DpMsgDefine;
import com.cylan.jiafeigou.dp.DpMsgMap;
import com.cylan.jiafeigou.misc.JConstant;
import com.cylan.jiafeigou.n.base.BaseApplication;
import com.cylan.jiafeigou.n.mvp.contract.cloud.CloudLiveDeviceInfoContract;
import com.cylan.jiafeigou.n.mvp.impl.cloud.CloudLiveDeviceInfoPresenterImp;
import com.cylan.jiafeigou.utils.MiscUtils;
import com.cylan.jiafeigou.widget.dialog.BaseDialog;
import com.cylan.jiafeigou.widget.dialog.EditFragmentDialog;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cylan.jiafeigou.widget.dialog.EditFragmentDialog.KEY_LEFT_CONTENT;
import static com.cylan.jiafeigou.widget.dialog.EditFragmentDialog.KEY_RIGHT_CONTENT;
import static com.cylan.jiafeigou.widget.dialog.EditFragmentDialog.KEY_TITLE;
import static com.cylan.jiafeigou.widget.dialog.EditFragmentDialog.KEY_TOUCH_OUT_SIDE_DISMISS;

/**
 * 作者：zsl
 * 创建时间：2016/9/26
 * 描述：
 */
public class CloudLiveDeviceInfoFragment extends Fragment implements CloudLiveDeviceInfoContract.View {

    @BindView(R.id.iv_information_back)
    ImageView ivInformationBack;
    @BindView(R.id.tv_device_alias)
    TextView tvInformationFacilityName;
    @BindView(R.id.lLayout_information_facility_name)
    LinearLayout lLayoutInformationFacilityName;
    @BindView(R.id.tv_device_sdcard_state)
    TextView tvDeviceSdcardState;
    @BindView(R.id.tv_device_wifi_state)
    TextView tvDeviceWifiState;
    @BindView(R.id.tv_device_cid)
    TextView tvDeviceCid;
    @BindView(R.id.tv_device_storage)
    TextView tvDeviceStorage;
    @BindView(R.id.tv_device_mac)
    TextView tvDeviceMac;
    @BindView(R.id.tv_system_version)
    TextView tvSystemVersion;
    @BindView(R.id.tv_soft_version)
    TextView tvSoftVersion;

    private String uuid;
    private EditFragmentDialog editDialogFragment;
    private CloudLiveDeviceInfoContract.Presenter presenter;
    public OnChangeNameListener listener;

    public interface OnChangeNameListener {
        void changeName(String name);
    }

    public void setOnChangeNameListener(OnChangeNameListener listener) {
        this.listener = listener;
    }

    public static CloudLiveDeviceInfoFragment newInstance(Bundle bundle) {
        CloudLiveDeviceInfoFragment fragment = new CloudLiveDeviceInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_live_device_info, container, false);
        ButterKnife.bind(this, view);
        initPresenter();
        return view;
    }

    private void initPresenter() {
        uuid = getArguments().getString(JConstant.KEY_DEVICE_ITEM_UUID);
        presenter = new CloudLiveDeviceInfoPresenterImp(this, uuid);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDetails();
    }

    private void updateDetails() {
        DpMsgDefine.DPPrimary<String> mac = BaseApplication.getAppComponent().getSourceManager().getValue(uuid, DpMsgMap.ID_202_MAC, null);
        String m = MiscUtils.safeGet(mac, "");
        tvDeviceMac.setText(m);
        DpMsgDefine.DPPrimary<String> version = BaseApplication.getAppComponent().getSourceManager().getValue(uuid, DpMsgMap.ID_207_DEVICE_VERSION, null);
        String v = MiscUtils.safeGet(version, "");
        tvSoftVersion.setText(v);
        DpMsgDefine.DPPrimary<String> sVersion = BaseApplication.getAppComponent().getSourceManager().getValue(uuid, DpMsgMap.ID_208_DEVICE_SYS_VERSION, null);
        String sv = MiscUtils.safeGet(sVersion, "");
        tvSystemVersion.setText(sv);
        Device device = BaseApplication.getAppComponent().getSourceManager().getDevice(uuid);
        if (device != null) {
            tvInformationFacilityName.setText(TextUtils.isEmpty(device.alias) ? device.uuid : device.alias);
            tvDeviceCid.setText(device.uuid);
        }
        DpMsgDefine.DPSdStatus status = BaseApplication.getAppComponent().getSourceManager().getValue(uuid, DpMsgMap.ID_204_SDCARD_STORAGE, null);
        if (status == null || status.hasSdcard==0) {
            tvDeviceStorage.setText(getString(R.string.SD_NO));
        } else {
            if (status.err != 0) {
                //未初始化
                tvDeviceStorage.setText(getString(R.string.SD_NO));
            } else {
                if (status.total != 0) {
                    String content = new DecimalFormat("#0.00").format(((float) status.used / status.total));
                    tvDeviceStorage.setText(
                            String.format(getString(R.string.REMAIN_SPACE), content));
                }
            }
        }
    }

    @Override
    public void setPresenter(CloudLiveDeviceInfoContract.Presenter presenter) {

    }

    @Override
    public String getUuid() {
        return null;
    }

    @OnClick({R.id.iv_information_back, R.id.lLayout_information_facility_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_information_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.lLayout_information_facility_name:
                toEditAlias();
                break;
        }
    }

    /**
     * 编辑昵称
     */
    private void toEditAlias() {
        if (editDialogFragment == null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, getString(R.string.EQUIPMENT_NAME));
            bundle.putString(KEY_LEFT_CONTENT, getString(R.string.OK));
            bundle.putString(KEY_RIGHT_CONTENT, getString(R.string.CANCEL));
            bundle.putBoolean(KEY_TOUCH_OUT_SIDE_DISMISS, false);
            editDialogFragment = EditFragmentDialog.newInstance(bundle);
        }
        if (editDialogFragment.isVisible())
            return;
        editDialogFragment.show(getChildFragmentManager(), "editDialogFragment");
        editDialogFragment.setAction(new BaseDialog.BaseDialogAction() {
            @Override
            public void onDialogAction(int id, Object value) {
                if (presenter != null && value != null && value instanceof String) {
                    String content = (String) value;
                    Device device = BaseApplication.getAppComponent().getSourceManager().getDevice(uuid);
                    if (!TextUtils.isEmpty(content)
                            && !TextUtils.equals(device.alias, content)) {
                        tvInformationFacilityName.setText(content);
                        device.alias = content;
                        updateDetails();
                        if (listener != null) {
                            listener.changeName(content);
                        }

                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
