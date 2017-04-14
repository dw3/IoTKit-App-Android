package com.cylan.jiafeigou.n.mvp.impl.bell;

import com.cylan.jiafeigou.base.wrapper.BasePresenter;
import com.cylan.jiafeigou.cache.db.module.DPEntity;
import com.cylan.jiafeigou.cache.db.module.Device;
import com.cylan.jiafeigou.cache.db.view.DBAction;
import com.cylan.jiafeigou.cache.db.view.DBOption;
import com.cylan.jiafeigou.cache.db.view.IDPEntity;
import com.cylan.jiafeigou.dp.DpMsgDefine;
import com.cylan.jiafeigou.dp.DpMsgMap;
import com.cylan.jiafeigou.n.mvp.contract.bell.DoorBellHomeContract;
import com.cylan.jiafeigou.n.mvp.model.BellCallRecordBean;
import com.cylan.jiafeigou.rx.RxBus;
import com.cylan.jiafeigou.rx.RxEvent;
import com.cylan.jiafeigou.support.log.AppLogger;
import com.cylan.jiafeigou.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cylan-hunt on 16-8-3.
 */
public class DBellHomePresenterImpl extends BasePresenter<DoorBellHomeContract.View>
        implements DoorBellHomeContract.Presenter {
    /**
     * 凌晨0点时间戳
     */
    private static final long todayInMidNight = TimeUtils.getTodayStartTime();
    private static final long yesterdayInMidNight = todayInMidNight - 24 * 60 * 60 * 1000L;
    private List<BellCallRecordBean> mRecords = new ArrayList<>();
    private boolean isFirst = true;
    private Subscription subscribe;

    private void notifyBellLowBattery() {
        if (isFirst) {
            isFirst = false;
            Device device = sourceManager.getDevice(mUUID);
            if (device != null && device.available()) {
                Integer battery = device.$(DpMsgMap.ID_206_BATTERY, 0);
                if (battery < 20) {//电量低
                    DBOption.DeviceOption option = device.option(DBOption.DeviceOption.class);
                    if (option != null && option.lastLowBatteryTime < todayInMidNight) {//新的一天
                        option.lastLowBatteryTime = System.currentTimeMillis();
                        device.setOption(option);
                        sourceManager.updateDevice(device);
                        mView.onBellBatteryDrainOut();
                    }
                }

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Device device = sourceManager.getDevice(mUUID);
        if (device == null) {
            mView.onDeviceUnBind();
        } else {
            mView.onShowProperty(device);
            registerSubscription(getClearDataSub());

        }
    }

    private Subscription getClearDataSub() {
        return RxBus.getCacheInstance().toObservable(RxEvent.ClearDataEvent.class)
                .filter(event -> event.msgId == DpMsgMap.ID_401_BELL_CALL_STATE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    mView.onBellRecordCleared();
                    mRecords.clear();
                }, e -> {
                    AppLogger.e(e.getMessage());
                    e.printStackTrace();
                });
    }

    @Override
    public void fetchBellRecordsList(boolean asc, long time) {
        subscribe = Observable.just(new DPEntity()
                .setMsgId(DpMsgMap.ID_401_BELL_CALL_STATE)
                .setVersion(time)
                .setAction(DBAction.QUERY)
                .setOption(new DBOption.SingleQueryOption(asc, 20))
                .setUuid(mUUID))
                .flatMap(this::perform)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.getResultCode() == 0) {
                        mView.onRecordsListRsp(parse(result.getResultResponse()));
                        notifyBellLowBattery();
                    }
                }, e -> {
                    if (e instanceof TimeoutException) {
                        fetchBellRecordsList(asc, time);
                    } else {
                        AppLogger.e(e.getMessage());
                        e.printStackTrace();
                    }
                }, () -> mView.onRecordsListRsp(null));
        registerSubscription(subscribe);
    }

    private List<BellCallRecordBean> parse(Collection<DpMsgDefine.DPBellCallRecord> response) {
        if (response == null) return null;
        List<BellCallRecordBean> result = new ArrayList<>();
        BellCallRecordBean record;
        for (DpMsgDefine.DPBellCallRecord callRecord : response) {
            record = BellCallRecordBean.parse(callRecord);
            result.add(record);
        }
        mRecords.addAll(result);
        return result;
    }

    private List<IDPEntity> build(List<BellCallRecordBean> items) {
        List<IDPEntity> result = new ArrayList<>();
        IDPEntity entity;
        for (BellCallRecordBean item : items) {
            entity = new DPEntity()
                    .setMsgId(DpMsgMap.ID_401_BELL_CALL_STATE)
                    .setVersion(item.version)
                    .setUuid(mUUID)
                    .setAction(DBAction.DELETED);
            result.add(entity);
        }
        return result;
    }

    @Override
    public void deleteBellCallRecord(List<BellCallRecordBean> list) {
        Subscription subscribe = Observable.just(build(list))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(this::perform)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.getResultCode() == 0) {
                        mView.onDeleteBellRecordSuccess(list);
                    }
                }, e -> {
                    AppLogger.e(e.getMessage());
                    e.printStackTrace();
                }, () -> {
                });
        registerSubscription(subscribe);
    }

    @Override
    public void cancelFetch() {
        if (subscribe != null && subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
    }
}
