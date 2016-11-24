package com.cylan.jiafeigou.n.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.dp.DpMsgDefine;
import com.cylan.jiafeigou.misc.JConstant;
import com.cylan.jiafeigou.n.mvp.model.DeviceBean;
import com.cylan.superadapter.IMulItemViewType;
import com.cylan.superadapter.SuperAdapter;
import com.cylan.superadapter.internal.SuperViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cylan.jiafeigou.misc.JConstant.NET_TYPE_RES;

/**
 * Created by hunt on 16-5-24.
 */

public class HomePageListAdapter extends SuperAdapter<DeviceBean> {

    private final static int[] msgContentRes = {R.string.receive_new_news,
            R.string.receive_new_news,
            R.string.receive_new_news,
            R.string.receive_new_news};
    private DeviceItemClickListener deviceItemClickListener;
    private DeviceItemLongClickListener deviceItemLongClickListener;

    private static final SimpleDateFormat format_0 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat format_1 = new SimpleDateFormat("yy/M/d", Locale.getDefault());

    private static final Date date = new Date();

    public HomePageListAdapter(Context context, List<DeviceBean> items, IMulItemViewType<DeviceBean> mulItemViewType) {
        super(context, items, mulItemViewType);
    }

    public void setDeviceItemClickListener(DeviceItemClickListener deviceItemClickListener) {
        this.deviceItemClickListener = deviceItemClickListener;
    }

    public void setDeviceItemLongClickListener(DeviceItemLongClickListener deviceItemLongClickListener) {
        this.deviceItemLongClickListener = deviceItemLongClickListener;
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, DeviceBean item) {
        holder.setOnClickListener(R.id.rLayout_device_item, deviceItemClickListener);
        holder.setOnLongClickListener(R.id.rLayout_device_item, deviceItemLongClickListener);
        handleState(holder, item);
    }


    private String getMessageContent(DeviceBean bean) {
        final int deviceType = bean.pid;
//        final int msgCount = bean.msgCount;
        final int msgCount = 0;
        return String.format(Locale.getDefault(),
                getContext().getString(msgContentRes[0]), msgCount);
    }

    private String convertTime(DeviceBean bean) {
//        final long timeInterval = System.currentTimeMillis() - bean.msgTime;
//        if (timeInterval <= 5 * 60 * 1000) {
//            return getContext().getString(R.string.JUST_NOW);
//        } else if (timeInterval <= 24 * 60 * 1000) {
//            date.setTime(bean.msgTime);
//            return format_0.format(date);
//        } else {
//            date.setTime(bean.msgTime);
//            return format_1.format(date);
//        }
        return "";
    }

    /**
     * 通过cid查找index
     *
     * @param cid
     * @return
     */
    public DeviceBean findTarget(final String cid) {
        if (getCount() == 0 || TextUtils.isEmpty(cid))
            return null;
        ArrayList<DeviceBean> arrayList = new ArrayList<>(getList());
        for (DeviceBean bean : arrayList) {
            if (TextUtils.equals(bean.uuid, cid)) {
                return bean;
            }
        }
        return null;
    }

    private void setItemState(SuperViewHolder holder, DeviceBean bean, DpMsgDefine.MsgNet net) {
        //0 net type 网络类型
        int resIdNet = net == null ? -1 : NET_TYPE_RES.get(net.net);
        if (resIdNet != -1)
            holder.setImageResource(R.id.img_device_state_0, resIdNet);
        //1 分享
        if (!TextUtils.isEmpty(bean.shareAccount)) {
            holder.setImageResource(R.id.img_device_state_1, R.drawable.icon_home_share);
        }
        //2 电量
        if (bean.pid == JConstant.OS_DOOR_BELL) {

        }
        //3 延时摄影

        //4 保护

    }

    /**
     * | net| 特征值|  描述 |
     * |---|---|---|
     * |NET_CONNECT | -1 | #绑定后的连接中 |
     * |NET_OFFLINE |  0 | #不在线 |
     * |NET_WIFI    |  1 | #WIFI网络 |
     * |NET_2G      |  2 | #2G网络 |
     * |NET_3G      |  3 | #3G网络 |
     * |NET_4G      |  4 | #4G网络  |
     * |NET_5G      |  5 | #5G网络  |
     *
     * @param bean
     * @return
     */
    private DpMsgDefine.MsgNet determineNet(DeviceBean bean) {
        if (bean.dataList != null) {
            for (DpMsgDefine.DpMsg dp : bean.dataList) {
                if (dp.msgId == 201) {
                    if (dp.o != null && (dp.o instanceof DpMsgDefine.MsgNet)) {
                        return (DpMsgDefine.MsgNet) dp.o;
                    }
                }
            }
        }
        return null;
    }

    private void handleState(SuperViewHolder holder, DeviceBean bean) {
        DpMsgDefine.MsgNet net = determineNet(bean);
        //门磁一直在线状态
        final int onLineState = net != null ? net.net : (bean.pid == JConstant.OS_MAGNET ? 1 : 0);
        final int deviceType = bean.pid;
        int iconRes = (onLineState != 0 && onLineState != -1) ? JConstant.onLineIconMap.get(deviceType)
                : JConstant.offLineIconMap.get(deviceType);
        //昵称
        holder.setText(R.id.tv_device_alias, TextUtils.isEmpty(bean.alias) ? bean.uuid : bean.alias);
        //图标
        holder.setBackgroundResource(R.id.img_device_icon, iconRes);
        //消息数
        holder.setText(R.id.tv_device_msg_count, getMessageContent(bean));
        //时间
        holder.setText(R.id.tv_device_msg_time, convertTime(bean));
        //右下角状态
        setItemState(holder, bean, net);
    }

    @Override
    protected IMulItemViewType<DeviceBean> offerMultiItemViewType() {
        return new IMulItemViewType<DeviceBean>() {
            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position, DeviceBean DeviceBean) {
                return 0;
            }

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.layout_item_home_page_list;
            }
        };
    }

    public interface DeviceItemClickListener extends View.OnClickListener {

    }

    public interface DeviceItemLongClickListener extends View.OnLongClickListener {

    }
}