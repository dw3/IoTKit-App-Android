package com.cylan.jiafeigou.cache.db.view;

import com.cylan.entity.jniCall.JFGAccount;
import com.cylan.entity.jniCall.JFGDevice;
import com.cylan.jiafeigou.cache.db.module.Account;
import com.cylan.jiafeigou.cache.db.module.DPEntity;
import com.cylan.jiafeigou.cache.db.module.Device;

import java.util.List;

import rx.Observable;

/**
 * Created by yanzhendong on 2017/2/27.
 */

public interface IDBHelper {

    Observable saveDPByte(String uuid, Long version, Integer msgId, byte[] bytes);

    //junk code
    Observable<DPEntity> deleteDPMsgNotConfirm(String uuid, Long version, Integer msgId);

    //junk code
    Observable<DPEntity> deleteDPMsgWithConfirm(String uuid, Long version, Integer msgId);

    Observable<Boolean> deleteDPMsgWithConfirm(String uuid, Integer msgId);

    Observable<List<DPEntity>> queryUnConfirmDpMsgWithTag(String uuid, Integer msgId, IAction action);

    Observable<List<DPEntity>> queryUnConfirmDpMsg(String uuid, Integer msgId);

    Observable<List<DPEntity>> markDPMsgWithConfirm(String uuid, Long version, Integer msgId, IAction action);

    Observable<List<DPEntity>> markDPMsgNotConfirm(String uuid, Long version, Integer msgId, IAction action);

    Observable<List<DPEntity>> queryDPMsg(String uuid, Long version, Integer msgId, Boolean asc, Integer limit);

    Observable<List<DPEntity>> queryDPMsgByUuid(String uuid);


    Observable<DPEntity> saveDpMsg(String account, String server, String uuid, Long version, Integer msgId, byte[] bytes, String action, String state);

    Observable<DPEntity> saveOrUpdate(String account, String server, String uuid, Long version, Integer msgId, byte[] bytes, String action, String state);

    Observable<List<DPEntity>> queryDPMsg(String account, String server, String uuid, Long version, Integer msgId, Boolean asc, Integer limit, IAction action, IState state);

    Observable<List<DPEntity>> markDPMsg(String account, String server, String uuid, Long version, Integer msgId, IAction action, IState state);

    Observable<DPEntity> deleteDPMsgForce(String account, String server, String uuid, Long version, Integer msgId);

    Observable<Account> updateAccount(JFGAccount account);

    Observable<Account> getActiveAccount();

    Observable<Device> updateDevice(JFGDevice[] device);

    Observable<Device> getDevice(String uuid);

    Observable<List<Device>> getAccountDevice(String account);

    Observable<List<DPEntity>> getAllSavedDPMsgByAccount(String account);

    Observable<List<DPEntity>> getActiveAccountSavedDPMsg();
}