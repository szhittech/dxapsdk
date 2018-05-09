package com.het.ap.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;

import com.het.ap.bean.ApDeviceBean;
import com.het.ap.util.ClifeLog;
import com.het.ap.wifi.callback.IApTransDone;
import com.het.ap.wifi.callback.IApSendData;
import com.het.ap.callback.OnApScanLinstaner;
import com.het.ap.callback.OnApBindListener;
import com.het.ap.util.Utils;
import com.het.udp.wifi.utils.ByteUtils;

import java.util.ArrayList;
import java.util.List;

public class ApScanner implements IApTransDone{
    private Context context;
    private Thread scanThread = null, connThread = null,doneThread;
    private boolean isScanning = true;
    private boolean connecting = true;
    // Wifi管理类
    private WifiUtils mWifiUtils;
    // 扫描结果列表
    private List<ScanResult> list = new ArrayList<ScanResult>();
    private String filter = null;//"HET_";
    private OnApScanLinstaner onApScanLinstaner;

    private OnApBindListener onApBindListener;

    private String mDeviceApWiFi, mRouterSsid, mRouterPassword;

    private ScanResult mRouterScanResult;

    private WifiAutoConnectManager wifiautoconnect;

    private IApSendData apSendData;

    private long scanTimeout = 100;

    private long bindTimeout = 100;

    public ApScanner(Context context) {
        this.context = context;
        mWifiUtils = new WifiUtils(context);
        wifiautoconnect = new WifiAutoConnectManager(context);
    }

    public void setOnApScanLinstaner(OnApScanLinstaner onApScanLinstaner) {
        this.onApScanLinstaner = onApScanLinstaner;
    }

    public void setOnApBindListener(OnApBindListener onApBindListener) {
        this.onApBindListener = onApBindListener;
    }

    public void setFilter(String filter) {
        this.filter = "HET_"+filter;
    }

    public void setRouterSsid(String mRouterSsid) {
        this.mRouterSsid = mRouterSsid;
    }

    public void setRouterPassword(String mRouterPassword) {
        this.mRouterPassword = mRouterPassword;
    }

    public void setApSendData(IApSendData apSendData) {
        this.apSendData = apSendData;
    }

    public void setScanTimeout(long scanTimeout) {
        this.scanTimeout = scanTimeout;
    }

    public void setBindTimeout(long bindTimeout) {
        this.bindTimeout = bindTimeout;
    }

    public void startScan() {
        ClifeLog.i("uu==== start scan...");
        isScanning = true;
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long scanCount=0;
                    while (isScanning) {
                        mWifiUtils.WifiOpen();//开启WiFI
                        mWifiUtils.WifiStartScan();//扫描附近WiFi
                        Thread.sleep(3000);//休眠3s，不休眠则会在程序首次开启WIFI时候，处理getScanResults结果，wifiResultList.size()发生异常
                        list = mWifiUtils.getScanResults();
                        mWifiUtils.getConfiguration();
                        if (list != null) {
                            for (int i = list.size() - 1; i >= 0; i--) {
                                final ScanResult scanResult = list.get(i);
                                if (scanResult == null) {
                                    continue;
                                }
                                if (TextUtils.isEmpty(scanResult.SSID)) {
                                    continue;
                                }
                                //顺便找到App当前连接的路由器ScanResult信息
                                if (!TextUtils.isEmpty(mRouterSsid) && mRouterSsid.equalsIgnoreCase(scanResult.SSID)) {
                                    mRouterScanResult = scanResult;
                                }
                                String targetSsidd = scanResult.SSID;
                                //根据热点名称过滤
                                if (filter == null || targetSsidd.toUpperCase().startsWith(filter.toUpperCase())) {//LSConfigure_
                                    ClifeLog.i("==uu===扫描到设备 " + targetSsidd);
                                    if (onApScanLinstaner != null) {
                                        onApScanLinstaner.onScanResult(new ApDeviceBean(scanResult));
                                    }
                                }
                            }
                        }
                        scanCount =+ 3;
                        if (scanCount >= scanTimeout){
                            if (onApScanLinstaner != null) {
                                onApScanLinstaner.onScanFailed();
                                release();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ClifeLog.i("uu==== 扫描线程停止");
                }

            }
        });
        scanThread.setName("scanThread-" + ByteUtils.getCurrentTime());
        scanThread.start();
    }

    public void stopScan() {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        isScanning = false;
    }


    public void connApDevice(final String ssid, final String password) {
        if (TextUtils.isEmpty(ssid)) {
            return;
        }
        mDeviceApWiFi = ssid;
        if (connThread != null) {
            connThread.interrupt();
            connThread = null;
        }
        connecting = true;
        ClifeLog.i("uu== ############## connect ssid " + ssid + " password " + password);
        connThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isconnected = false;
                try {
                    boolean aotuConnAp = true;
                    long bindTimes = 0;
                    int connApCount = 0;
                    int circle = 0;
                    int reConnTimes = 0;
                    connectWiFi(ssid, password);
                    Thread.sleep(3000);
                    String current = WifiUtils.getSSid(context);
                    boolean isConnAp = checkWiFiConnectState(current);
                    String[] ip = null;
                    while (connecting) {
                        //如果已经发送过8100 那么是不需要再重复连接热点
                        if (!isConnAp) {
                            connApCount++;
                            if (onApScanLinstaner != null) {
                                int cCount = onApScanLinstaner.getConnApCount();
                                ClifeLog.w("uu== connApCount:" + connApCount + " ApCountTrigger:" + cCount + " ApCountTrigger:" + onApScanLinstaner.isconnApCountTrigger());
                                if (connApCount >= cCount) {
                                    if (!onApScanLinstaner.isconnApCountTrigger()) {
                                        onApScanLinstaner.onConnDeviceApTimeout(context, ssid);
                                        onApScanLinstaner.setIsconnApCountTrigger(true);
                                        aotuConnAp = false;
                                    }
                                }
                            }

                            circle++;
                            //如果过了7次还未连上，则重新连接
                            if (circle >= 7) {
                                //计数器清0
                                circle = 0;
                                if (onApScanLinstaner == null || aotuConnAp) {
                                    ClifeLog.e("==uu===重新连接热点 " + circle);
                                    if (reConnTimes % 2 == 0) {
                                        reConnAp(ssid, password);
                                    } else {
                                        connectWiFi(ssid, password);
                                    }
                                    reConnTimes++;
                                    Thread.sleep(1000);
                                }
                            }
                        }
                        current = WifiUtils.getSSid(context);
                        isConnAp = checkWiFiConnectState(current);
                        ip = Utils.getGateWayIp(context);
                        isConnAp = (isConnAp && (ip != null));
                        if (isConnAp) {
                            //连接上了AP热点设备
                            ClifeLog.e("uu== 热点连接成功[" + current + "] ip:" + ip[0]);
                            if (ip != null) {
                                if (apSendData != null) {
                                    apSendData.onSendData(ip[0]);
                                }
                            } else {
                                ClifeLog.d("uu== 已经连上热点:" + current + " 正在获取Ip地址:" + ip);
                            }
                        } else {
                            ClifeLog.e(circle + " uu== 未连上设备热点，当前连接WiFi:" + current + " 设备热点:" + mDeviceApWiFi + " ip:" + ((ip != null && ip.length > 0) ? ip[0] : "") + " connected:" + isConnAp + " connApCount:" + connApCount + " ApCountTrigger:" + (onApScanLinstaner == null ? "" : onApScanLinstaner.isconnApCountTrigger()));
                        }
                        bindTimes++;
                        if (bindTimes >= bindTimeout){
                            if (onApBindListener != null) {
                                onApBindListener.onBindFailed();
                                release();
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ClifeLog.d("uu== ======connect.exception " + e.getMessage());
                } finally {
                    if (onApScanLinstaner != null) {
                        onApScanLinstaner.setIsconnApCountTrigger(false);
                    }
                }
            }
        });
        connThread.setName("connectWiFi");
        connThread.start();
    }


    private void backConnRouter() {
        if (onApBindListener != null) {
            onApBindListener.setTimeoutTrigger(false);
        }
        if (doneThread != null) {
            doneThread.interrupt();
            doneThread = null;
        }
        if (doneThread == null) {
            doneThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int connCout = 1,time=0;
                        boolean autoConn = true;
                        ClifeLog.e("uu==========开始连接路由器====ssid:"+mRouterSsid +" "+mRouterPassword);
                        boolean isconnected = connectWiFi(mRouterSsid, mRouterPassword);
                        while (doneThread != null) {
                            if (onApBindListener != null) {
                                boolean internet = Utils.connBaiduTest();
                                if (internet) {
                                    ClifeLog.e("uu==========互联网畅通====");
                                    onApBindListener.onBindSucess();
                                    if (doneThread != null) {
                                        doneThread.interrupt();
                                        doneThread = null;
                                        release();
                                    }
                                    break;
                                } else {
                                    time++;
                                    ClifeLog.i("uu=======互联网已断开====connCout:" + connCout );
                                    if (doneThread != null) {
                                        ClifeLog.i("uu=======互联网已断开====timeout:" + onApBindListener.getTimeout() + " " + onApBindListener.isTimeoutTrigger());
                                        if (time > onApBindListener.getTimeout() && !onApBindListener.isTimeoutTrigger()) {
                                            onApBindListener.onConnRouterTimeOut(context);
                                            onApBindListener.setTimeoutTrigger(true);
                                            autoConn = false;
                                        }
                                    }
                                }

                                if (connCout % 3 == 0&&autoConn) {
                                    connectWiFi(mRouterSsid, mRouterPassword);
                                }
                                connCout++;
                            }
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            doneThread.setName("notifyReturnNetDone");
            doneThread.start();
        }
    }

    private boolean checkWiFiConnectState(String current) {
        //判断连接上热点AP
        if (!TextUtils.isEmpty(mDeviceApWiFi) && !TextUtils.isEmpty(current)) {
            if (mDeviceApWiFi.equalsIgnoreCase(current)) {
                return true;
            }
        }
        return false;
    }

    private boolean reConnAp(final String ssid, final String password) {
        WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
        if (!TextUtils.isEmpty(password)) {
            type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
        }
        try {
            wifiautoconnect.processConn(ssid, password, type);
        } catch (Exception e) {

        }
        return false;
    }

    private boolean connNoPass(String wifiPassword, String wifiItemSSID) {
        // 没有配置好信息，配置
        WifiAdmin wiFiAdmin = mWifiUtils.getWifiAdmin();
        if (wifiPassword != null) {
            int netId = wiFiAdmin
                    .addWifiConfig(list, wifiItemSSID, wifiPassword);
            ClifeLog.i("uu== ====connNoPass:" + netId);
            if (netId != -1) {
                wiFiAdmin.getConfiguration();// 添加了配置信息，要重新得到配置信息
                if (wiFiAdmin.ConnectWifi(netId)) {
                    // 连接成功，刷新UI
                    return true;
                }
            } else {
                // 网络连接错误
            }
        } else {
        }
        return false;
    }


    private boolean connectWiFi(final String ssid, final String password) {
        String current = WifiUtils.getSSid(context);
        ClifeLog.e("uu== ====connectWiFi 当前WIFI信息:" + current + " 目标热点:" + ssid + " PASS:" + password);
        if (ssid != null && current != null && current.equalsIgnoreCase(ssid))
            return true;
        /**连接无密码WIFI**/
        if (TextUtils.isEmpty(password)) {
            mWifiUtils.getConfiguration();
            int wifiItemId = mWifiUtils.IsConfiguration("\"" + ssid + "\"");
            ClifeLog.i("uu== ====connectWiFi wifiItemId:" + wifiItemId);
            if (wifiItemId != -1) {
                mWifiUtils.addNetwork(ssid, password);
                if (mWifiUtils.ConnectWifi(wifiItemId)) {
                    //连接指定WIFI
                    ClifeLog.i("uu=====connectWiFi 正在连接指定WIFI:" + ssid + " wifiItemId:" + wifiItemId);
                } else {
                    ClifeLog.i("uu=====connectWiFi 正在连接指定WIFI失败...:" + ssid + " wifiItemId:" + wifiItemId);
                }
            } else if (connNoPass(ssid, "")) {
                ;//else if (mWifiUtils.isConnectNoPass(mDeviceApScanResult)) {
                ClifeLog.i("uu=====connectWiFi 经判断,已经连接WIFI:" + ssid);
                return true;
            } else {
                ClifeLog.i("uu=====connectWiFi 正在连接的指定WIFI失败:" + ssid + " wifiItemId:" + wifiItemId);
                mWifiUtils.removeNetWork(mWifiUtils.getWifiConfiguration1("\"" + ssid + "\""));
                mWifiUtils.addNetwork(ssid, password);
            }
            return false;
        } else {
            /**连接有密码WIFI**/
            if (!TextUtils.isEmpty(password)) {
                int netId = mWifiUtils.AddWifiConfig(list, ssid, password);
                if (netId != -1) {
                    mWifiUtils.getConfiguration();
                    //添加了配置信息，要重新得到配置信息
                    if (mWifiUtils.ConnectWifi(netId)) {
                        ClifeLog.i("uu====connectWiFi正在连接有密码WIFI:" + ssid + " " + password);
                    }
                } else {
                    ClifeLog.i("uu====connectWiFi有密码WIFI连接失败:" + ssid + " " + password);
                    return false;
                }
            } else {
                ClifeLog.i("uu====connectWiFi WIFI密码不能为空:" + ssid + " " + password);
            }
        }

        return true;
    }

    public byte[] getBody() {
        if (mRouterScanResult == null) {
            ClifeLog.e("==uu==mRouterScanResult is null");
            return null;
        }
        if (mRouterSsid == null) {
            ClifeLog.e("==uu==mRouterSsid is null");
            return null;
        }
        if (mRouterPassword == null) {
            ClifeLog.e("==uu==mRouterPassword is null");
            return null;
        }
        int automode = Utils.getSecurity(mRouterScanResult.capabilities);
        return Utils.package8100Data(mRouterSsid, mRouterPassword, automode);
    }

    @Override
    public void onTrasnDone() {
        connecting = false;
        if (connThread!=null) {
            connThread.interrupt();
            connThread = null;
        }
        backConnRouter();
    }

    public void release(){
        isScanning = false;
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        connecting = false;
        if (connThread!=null) {
            connThread.interrupt();
            connThread = null;
        }

        if (doneThread != null) {
            doneThread.interrupt();
            doneThread = null;
        }

        ClifeLog.e("uu==========release====");
    }
}
