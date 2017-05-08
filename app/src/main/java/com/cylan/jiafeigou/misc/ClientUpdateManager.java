package com.cylan.jiafeigou.misc;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.cylan.ex.JfgException;
import com.cylan.jiafeigou.IRemoteService;
import com.cylan.jiafeigou.IRemoteServiceCallback;
import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.dp.DpUtils;
import com.cylan.jiafeigou.misc.bind.UdpConstant;
import com.cylan.jiafeigou.n.base.BaseApplication;
import com.cylan.jiafeigou.rx.RxBus;
import com.cylan.jiafeigou.rx.RxEvent;
import com.cylan.jiafeigou.support.log.AppLogger;
import com.cylan.jiafeigou.support.network.NetMonitor;
import com.cylan.jiafeigou.utils.CloseUtils;
import com.cylan.jiafeigou.utils.ContextUtils;
import com.cylan.jiafeigou.utils.FileUtils;
import com.cylan.jiafeigou.utils.MiscUtils;
import com.cylan.jiafeigou.utils.NetUtils;
import com.cylan.jiafeigou.utils.PackageUtils;
import com.cylan.jiafeigou.utils.PreferencesUtils;
import com.cylan.udpMsgPack.JfgUdpMsg;
import com.google.gson.Gson;

import org.msgpack.MessagePack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION;
import static com.cylan.jiafeigou.misc.bind.UdpConstant.F_ACK;
import static com.cylan.jiafeigou.misc.bind.UdpConstant.F_PING_ACK;

/**
 * 作者：zsl
 * 创建时间：2017/4/1
 * 描述：
 */
public class ClientUpdateManager {
    private static final String TAG = "ClientUpdateManager";
    private static ClientUpdateManager instance;
    private IRemoteService mService = null;

    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews contentView;
    private NotificationCompat.Builder cBuilder;
    private NotificationManager nm;
    private Subscription checkLocalSub;

    private ClientUpdateManager() {
    }

    public static ClientUpdateManager getInstance() {
        if (instance == null)
            synchronized (ClientUpdateManager.class) {
                if (instance == null)
                    instance = new ClientUpdateManager();
            }
        return instance;
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IRemoteService.Stub.asInterface(service);
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                Log.d(this.getClass().getSimpleName(), "RemoteException: " + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                Log.d(this.getClass().getSimpleName(), "RemoteException: " + e);
            }
            mService = null;
        }
    };

    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
        @Override
        public void onDownloadStarted(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadStarted:" + taskId);
        }

        @Override
        public void onDownloadPaused(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadPaused:" + taskId);
        }

        @Override
        public void onDownloadProcess(long taskId, double percent, long downloadedLength) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadProcess:" + taskId + " percent:" + percent);
            // 改变通知栏
        }

        @Override
        public void onDownloadFinished(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadFinished:" + taskId);
        }

        @Override
        public void onDownloadRebuildStart(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadRebuildStart:" + taskId);
        }

        @Override
        public void onDownloadRebuildFinished(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadRebuildFinished:" + taskId);
            AppLogger.d("test_downF:下载完成了");
        }

        @Override
        public void onDownloadCompleted(long taskId) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onDownloadCompleted:" + taskId);
            AppLogger.d("test_downC:下载完成了");
            release(ContextUtils.getContext());
        }

        @Override
        public void onFailedReason(long taskId, int reason) throws RemoteException {
            Log.d("IRemoteServiceCallback", "onFailedReason:" + taskId);
        }
    };

    /**
     * 获取文件大小
     *
     * @param file return
     * @throws Exception
     */

    private long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                AppLogger.d("getF:" + size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 通知栏进度
     */
    public void createNotification() {
        // 获取系统服务来初始化对象
        nm = (NotificationManager) ContextUtils.getContext()
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(ContextUtils.getContext());
//        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(R.mipmap.ic_launcher);// 设置顶部状态栏的小图标
        cBuilder.setTicker("正在更新加菲狗");// 在顶部状态栏中的提示信息
        cBuilder.setContentTitle("加菲狗升级程序");// 设置通知中心的标题
        cBuilder.setContentText("正在下载中");// 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());
        cBuilder.setAutoCancel(true);
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
    }

    /**
     * 发送通知
     */
    private void sent() {
        notification = cBuilder.build();
        // 发送该通知
        nm.notify(7, notification);
    }

    /**
     * 根据id清除通知
     */
    public void clear() {
        // 取消通知
        nm.cancelAll();
    }

    private static NetMonitor netMonitor;


    private void prepareNetMonitor() {
        AppLogger.d("注册网络 广播");
        if (netMonitor == null)
            netMonitor = NetMonitor.getNetMonitor();
        netMonitor.registerNet((Context context, Intent intent) -> {
            int net = NetUtils.getJfgNetType();
            if (net == 0) {
                //失败了.
                //升级过程,认定失败
                updatingTaskHashMap.clear();
            }
        }, new String[]{ConnectivityManager.CONNECTIVITY_ACTION,
                NETWORK_STATE_CHANGED_ACTION});
    }

    public void release(Context context) {
        try {
            if (mService != null) {
                mService.unregisterCallback(mCallback);
                mService = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        context.unbindService(serviceConnection);

        if (checkLocalSub != null && !checkLocalSub.isUnsubscribed()) {
            checkLocalSub.unsubscribe();
        }
    }


    /**
     * @param url
     * @param versionName
     * @param versionCode:
     */
    public void enqueue(String url, String versionName, String versionCode, DownloadListener downloadListener) {
        try {
            int currentVersionCode = PackageUtils.getAppVersionCode(ContextUtils.getContext());
            int remoteVersionCode = Integer.parseInt(versionCode);
            if (currentVersionCode >= remoteVersionCode) {
                AppLogger.d("不需要升级");
                return;
            }
        } catch (Exception e) {
            AppLogger.e("err:" + MiscUtils.getErr(e));
        }
        String fileName = versionName + ".apk";
        final File file = new File(JConstant.MISC_PATH, fileName);
        new File(JConstant.MISC_PATH).mkdir();
        if (file.exists()) {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();
                long fileSize = response.body().contentLength();
                AppLogger.d("文件大小:" + fileSize);
                if (fileSize == file.length()) {
                    AppLogger.d("文件存在,完整");
                    if (downloadListener != null) {
                        downloadListener.finished(file);
                    }
                    return;
                }
                FileUtils.delete(JConstant.MISC_PATH, fileName);
            } catch (IOException e) {
                AppLogger.e("err:" + MiscUtils.getErr(e));
            }
        }
        final Request request = new Request.Builder().url(url).build();
        final Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.toString());
                FileUtils.delete(JConstant.MISC_PATH, fileName);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    if (downloadListener != null) downloadListener.start(total);
                    Log.d(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.d(TAG, "current------>" + current);
                        if (downloadListener != null) downloadListener.process(current, total);
                    }
                    fos.flush();
                    if (downloadListener != null)
                        downloadListener.finished(new File(JConstant.MISC_PATH, fileName));
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    if (downloadListener != null)
                        downloadListener.failed(e);
                    FileUtils.delete(JConstant.MISC_PATH, fileName);
                } finally {
                    CloseUtils.close(is);
                    CloseUtils.close(fos);
                }
            }
        });
    }

    private Map<String, PackageDownloadTask> downloadMap = new HashMap<>();
    private HashMap<String, FirmWareUpdatingTask> updatingTaskHashMap = new HashMap<>();

    /**
     * 下载文件
     */
    public void downLoadFile(RxEvent.CheckDevVersionRsp rsp, DownloadListener listener) {
        Log.d(TAG, "开始下载: " + rsp);
        if (rsp == null) return;
        PackageDownloadTask packageDownloadAction = downloadMap.get(rsp.uuid);
        if (packageDownloadAction != null) {
            packageDownloadAction.setDownloadListener(listener);
            RxEvent.CheckDevVersionRsp remainRsp = packageDownloadAction.getCheckDevVersionRsp();
            if (remainRsp != null && remainRsp.downloadState == JConstant.D.DOWNLOADING)
                return;
        }
        packageDownloadAction = new PackageDownloadTask(rsp);
        packageDownloadAction.setDownloadListener(listener);
        downloadMap.put(rsp.uuid, packageDownloadAction);
        Observable.just("go")
                .subscribeOn(Schedulers.newThread())
                .subscribe(packageDownloadAction, AppLogger::e);
    }

    public PackageDownloadTask getUpdateAction(String uuid) {
        return downloadMap.get(uuid);
    }

    public interface DownloadListener {

        void start(long totalByte);

        void failed(Throwable throwable);

        void finished(File file);

        void process(long currentByte, long totalByte);
    }

    /**
     * call方法跑了一个 okhttp的文件下载.
     */
    public final class PackageDownloadTask implements Action1<Object> {

        private RxEvent.CheckDevVersionRsp rsp;
        private DownloadListener downloadListener;

        public RxEvent.CheckDevVersionRsp getCheckDevVersionRsp() {
            return rsp;
        }

        public PackageDownloadTask(RxEvent.CheckDevVersionRsp checkDevVersionRsp) {
            this.rsp = checkDevVersionRsp;
        }

        public void setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
        }

        @Override
        public void call(Object o) {
            prepareNetMonitor();
            final File file = new File(rsp.fileDir, rsp.fileName);
            new File(rsp.fileDir).mkdir();
            if (file.exists()) {
                try {
                    Request request = new Request.Builder()
                            .url(rsp.url)
                            .build();
                    Response response = new OkHttpClient().newCall(request).execute();
                    long fileSize = response.body().contentLength();
                    AppLogger.d("文件大小:" + fileSize);
                    if (fileSize == file.length()) {
                        AppLogger.d("文件存在,完整");
                        rsp.downloadState = JConstant.D.SUCCESS;
                        rsp.lastUpdateTime = System.currentTimeMillis();
                        updateInfo(rsp.uuid, rsp);
                        if (downloadListener != null) {
                            downloadListener.finished(file);
                        }
                        return;
                    }
                } catch (IOException e) {
                    AppLogger.e("err:" + MiscUtils.getErr(e));
                }
            }
            //文件失败了
            FileUtils.delete(JConstant.MISC_PATH, rsp.fileDir + File.separator + rsp.fileName);
            final Request request = new Request.Builder().url(rsp.url).build();
            final Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, e.toString());
                    FileUtils.delete(JConstant.MISC_PATH, rsp.fileName);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        long total = response.body().contentLength();
                        rsp.downloadState = JConstant.D.DOWNLOADING;
                        rsp.lastUpdateTime = System.currentTimeMillis();
                        updateInfo(rsp.uuid, rsp);
                        if (downloadListener != null) {
                            downloadListener.start(total);
                        }
                        Log.d(TAG, "total------>" + total);
                        long current = 0;
                        is = response.body().byteStream();
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            current += len;
                            fos.write(buf, 0, len);
                            Log.d(TAG, "current------>" + current);
                            rsp.downloadState = JConstant.D.DOWNLOADING;
                            rsp.lastUpdateTime = System.currentTimeMillis();
                            if (downloadListener != null) {
                                downloadListener.process(current, total);
                            }
                        }
                        rsp.downloadState = JConstant.D.SUCCESS;
                        rsp.lastUpdateTime = System.currentTimeMillis();
                        updateInfo(rsp.uuid, rsp);
                        fos.flush();
                        if (downloadListener != null) {
                            downloadListener.finished(new File(rsp.fileDir, rsp.fileName));
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        rsp.downloadState = JConstant.D.FAILED;
                        rsp.lastUpdateTime = System.currentTimeMillis();
                        updateInfo(rsp.uuid, rsp);
                        if (downloadListener != null) {
                            downloadListener.failed(e);
                        }
                        FileUtils.delete(JConstant.MISC_PATH, rsp.fileName);
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }
            });
        }

        private Gson gson = new Gson();

        void updateInfo(String uuid, RxEvent.CheckDevVersionRsp checkDevVersionRsp) {
            PreferencesUtils.putString(JConstant.KEY_FIRMWARE_CONTENT + uuid, gson.toJson(checkDevVersionRsp));
        }
    }

    /**
     * 固件升级的 进度模拟流程.
     * <pnet>
     * 因为升级只需要发送一个udp消息{带上}
     */
    public final class FirmWareUpdatingTask implements Action1<String>, SimulatePercent.OnAction {
        private FUpdatingListener listener;
        private String uuid;
        private SimulatePercent simulatePercent;

        public FirmWareUpdatingTask(String uuid) {
            this.uuid = uuid;
            simulatePercent = new SimulatePercent();
            simulatePercent.setOnAction(this);
        }

        /**
         * {@link com.cylan.jiafeigou.misc.JConstant.U#FAILED_30S}
         * {@link com.cylan.jiafeigou.misc.JConstant.U#FAILED_60S}
         * {@link com.cylan.jiafeigou.misc.JConstant.U#IDLE}
         * {@link com.cylan.jiafeigou.misc.JConstant.U#UPDATING}
         * {@link com.cylan.jiafeigou.misc.JConstant.U#SUCCESS}
         */
        private int updateState;

        public int getUpdateState() {
            return updateState;
        }

        public int getSimulatePercent() {
            if (simulatePercent == null) return 0;
            return simulatePercent.getProgress();
        }

        public void setListener(FUpdatingListener listener) {
            this.listener = listener;
        }

        @Override
        public void call(String s) {
            prepareNetMonitor();
            //1.发送一个fping,等待fpingRsp,从中读取ip,port.
            RxBus.getCacheInstance().toObservable(RxEvent.LocalUdpMsg.class)
                    .subscribeOn(Schedulers.newThread())
                    .timeout(10, TimeUnit.SECONDS)//设备无响应
                    .flatMap(localUdpMsg -> {
                        MessagePack msgPack = new MessagePack();
                        try {
                            JfgUdpMsg.UdpHeader header = msgPack.read(localUdpMsg.data, JfgUdpMsg.UdpHeader.class);
                            Log.d(TAG, "cmd:" + header.cmd + ",");
                            if (TextUtils.equals(F_PING_ACK, header.cmd)) {
                                //得到fping结果
                                JfgUdpMsg.UdpRecvHeard recvHeard = msgPack.read(localUdpMsg.data, JfgUdpMsg.UdpRecvHeard.class);
                                if (TextUtils.equals(recvHeard.cid, uuid)) {
                                    throw new HelperBreaker().setValue(localUdpMsg);
                                } else Log.d(TAG, "不是同一个设备:" + uuid + ",cid:" + recvHeard.cid);
                            }
                            return Observable.just(null);
                        } catch (IOException e) {
                            return Observable.just(null);
                        }
                    })
                    .filter(ret -> ret != null)
                    .subscribe(ret -> AppLogger.d("got your rsp : " + uuid + " "),
                            //err发生,整个订阅链就结束
                            throwable -> {
                                if (throwable instanceof HelperBreaker) {
                                    AppLogger.d("got your rsp : " + uuid + " " + ((HelperBreaker) throwable).localUdpMsg);
                                    prepareSending(((HelperBreaker) throwable).localUdpMsg.ip, ((HelperBreaker) throwable).localUdpMsg.port);
                                } else if (throwable instanceof TimeoutException) {
                                    AppLogger.d("fping timeout : " + uuid);
                                    handleTimeout(JConstant.U.FAILED_FPING_ERR);
                                }
                            });
            try {
                BaseApplication.getAppComponent().getCmd().sendLocalMessage(UdpConstant.IP, UdpConstant.PORT, new JfgUdpMsg.Ping().toBytes());
                BaseApplication.getAppComponent().getCmd().sendLocalMessage(UdpConstant.IP, UdpConstant.PORT, new JfgUdpMsg.FPing().toBytes());
                AppLogger.d("send fping :" + UdpConstant.IP);
            } catch (JfgException e) {
                e.printStackTrace();
            }
            makeSimulatePercent();
        }

        private void prepareSending(String remoteIp, int port) {
            String content = PreferencesUtils.getString(JConstant.KEY_FIRMWARE_CONTENT + uuid);
            final RxEvent.CheckDevVersionRsp description = new Gson().fromJson(content, RxEvent.CheckDevVersionRsp.class);
            String localIp = NetUtils.getReadableIp();
            String localUrl = "http://" + localIp + ":8765/" + JConstant.MISC_PATH + File.separator + description.fileName;
            AppLogger.d("ip:" + localIp + ",localUrl" + localUrl);
            if (listener != null) listener.start();
            resetRspRecv(true);
            makeUpdateRspRecv(30);//30s
            makeUpdateRspRecv(66);//60s
            try {
                BaseApplication.getAppComponent().getCmd().sendLocalMessage(remoteIp, (short) port, new UdpConstant.UdpFirmwareUpdate(localUrl, uuid, remoteIp, 8765).toBytes());
                BaseApplication.getAppComponent().getCmd().sendLocalMessage(remoteIp, (short) port, new UdpConstant.UdpFirmwareUpdate(localUrl, uuid, remoteIp, 8765).toBytes());
            } catch (JfgException e) {
                AppLogger.e("发送升级包失败?" + MiscUtils.getErr(e));
            }
            //1.ping 设备,得到pingAck
        }

        private void makeSimulatePercent() {
            AppLogger.d("开始模拟升级进度");
            if (simulatePercent == null) {
                simulatePercent = new SimulatePercent();
                simulatePercent.setOnAction(this);
            }
            simulatePercent.stop();
            simulatePercent.start();
        }

        private CompositeSubscription compositeSubscription = new CompositeSubscription();

        private void resetRspRecv(boolean needInit) {
            compositeSubscription.unsubscribe();
            if (needInit)
                compositeSubscription = new CompositeSubscription();
        }

        private void addSub(Subscription subscription) {
            if (compositeSubscription != null)
                compositeSubscription.add(subscription);
        }

        /**
         * @param timeout :超时
         */
        private void makeUpdateRspRecv(int timeout) {
            Subscription subscription = RxBus.getCacheInstance().toObservable(RxEvent.LocalUdpMsg.class)
                    .subscribeOn(Schedulers.newThread())
                    .timeout(timeout, TimeUnit.SECONDS)//设备无响应
                    .filter(ret -> !TextUtils.isEmpty(ret.ip) && ret.port != 0)
                    .flatMap(localUdpMsg -> {
                        MessagePack msgPack = new MessagePack();
                        try {
                            JfgUdpMsg.UdpHeader header = msgPack.read(localUdpMsg.data, JfgUdpMsg.UdpHeader.class);
                            if (TextUtils.equals(F_ACK, header.cmd)) {
                                //得到fping结果
                                JfgUdpMsg.UdpRecvHeard recvHeard = msgPack.read(localUdpMsg.data, JfgUdpMsg.UdpRecvHeard.class);
                                if (TextUtils.equals(recvHeard.cid, uuid)) {
                                    throw new HelperBreaker().setValue(localUdpMsg);
                                } else Log.d(TAG, "不是同一个设备:" + uuid + ",cid:" + recvHeard.cid);
                            }
                            return Observable.just(null);
                        } catch (IOException e) {
                            return Observable.just(null);
                        }
                    })
                    .filter(ret -> ret != null)
                    .subscribe(ret -> {
                    }, throwable -> {
                        if (throwable instanceof HelperBreaker) {
                            //此处发生之后,表明整个订阅链结束了,不会再发生60s超时的回调
                            handleResult(uuid, timeout, ((HelperBreaker) throwable).localUdpMsg.data);
                            Log.d(TAG, "Client: " + ((HelperBreaker) throwable).localUdpMsg);
                        } else if (throwable instanceof TimeoutException) {
                            int err = timeout == 30 ? JConstant.U.FAILED_30S : JConstant.U.FAILED_60S;
                            handleTimeout(err);
                        }
                        resetRspRecv(false);
                    });
            addSub(subscription);
        }

        private void handleTimeout(int code) {
            if (listener != null) listener.err(code);
            AppLogger.d("fping timeout : " + uuid + " " + listener);
        }

        private void handleResult(String uuid, int tag, byte[] data) {
            if (simulatePercent != null) simulatePercent.stop();
            try {
                UdpConstant.FAck fAck = DpUtils.unpackData(data, UdpConstant.FAck.class);
                if (fAck != null && fAck.ret != 0) {
                    this.updateState = JConstant.U.FAILED_DEVICE_FAILED;
                    if (listener != null) listener.err(this.updateState);
                    if (simulatePercent != null) simulatePercent.stop();
                } else if (fAck != null) {//相应,成功了.
                    this.updateState = JConstant.U.SUCCESS;
                    if (simulatePercent != null) simulatePercent.boost();
                    AppLogger.d("升级成功,清空配置");
                    PreferencesUtils.remove(JConstant.KEY_FIRMWARE_CONTENT + uuid);
                }
            } catch (IOException e) {
                AppLogger.e("err:" + MiscUtils.getErr(e));
            }
            AppLogger.d(String.format(Locale.getDefault(), "got %s firmware rsp :,uuid,%s,data:%s ", tag, uuid, data));
        }

        @Override
        public void actionDone() {
            updateState = JConstant.U.SUCCESS;
            if (listener != null) listener.success();

        }

        @Override
        public void actionPercent(int percent) {
            updateState = JConstant.U.UPDATING;
            if (listener != null) listener.progress(percent);
        }
    }

    private static class HelperBreaker extends RuntimeException {
        private RxEvent.LocalUdpMsg localUdpMsg;

        public HelperBreaker setValue(RxEvent.LocalUdpMsg localUdpMsg) {
            this.localUdpMsg = localUdpMsg;
            return this;
        }

        public HelperBreaker() {
        }

        public HelperBreaker(String detailMessage) {
            super(detailMessage);
        }

        public HelperBreaker(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
            this.initCause(throwable);
        }

        public HelperBreaker(Throwable throwable) {
            super(throwable);
        }
    }

    public interface FUpdatingListener {

        void start();

        void progress(int percent);

        void err(int errCode);

        void success();
    }

    public void removeTask(String uuid) {
        updatingTaskHashMap.remove(uuid);
    }

    public void enqueue(String uuid, FUpdatingListener listener) {

        FirmWareUpdatingTask task = getUpdatingTask(uuid);
        if (task == null) {
            task = new FirmWareUpdatingTask(uuid);
            task.setListener(listener);
            updatingTaskHashMap.put(uuid, task);
        } else {
            task.setListener(listener);
            return;
        }
        Observable.just("go")
                .subscribeOn(Schedulers.newThread())
                .subscribe(task, AppLogger::e);
    }

    public FirmWareUpdatingTask getUpdatingTask(String uuid) {
        return updatingTaskHashMap.get(uuid);
    }


}
