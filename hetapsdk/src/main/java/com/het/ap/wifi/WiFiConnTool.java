package com.het.ap.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;


import com.het.ap.util.ClifeLog;

import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13 0013.
 */
public class WiFiConnTool {
    private static WifiManager mWifiManager;

    private OnWifiStateChangeListener onWifiStateChangeListener;
    private OnWifiSupplicantStateChangeListener onWifiSupplicantStateChangeListener;
    private OnWifiScanResultsListener onWifiScanResultsListener;
    private OnWifiConnectintListener onWifiConnectintListener;
    private OnWifiIDLEListener onWifiIDLEListener;
    private OnWifiRSSIListener onWifiRSSIListener;
    private Context context;//上下文
    private WifiBroadcast mWifiReceiver;

    // 构造器
    public WiFiConnTool(Context context) {
        this.context = context;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        //注册wifi状态及接受消息的广播接收器
        registerReceiver(context);
    }


    //开启WIFI
    public void wifiOpen() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭WIFI
    public void wifiClose() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 注册广播
     *
     * @param context
     */
    private void registerReceiver(Context context) {
        //注册wifi信号监听器
        mWifiReceiver = new WifiBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//wifi信号变动广播
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//wifi扫描到的结果广播
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);//wifi信号变动广播
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);//请求状态变动广播


        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); //网络状态变化
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//连上与否
        intentFilter.addAction(WifiManager.EXTRA_SUPPLICANT_ERROR);//连上与否
        context.registerReceiver(mWifiReceiver, intentFilter);
        ClifeLog.e( "uu ########################## registerReceiver " + mWifiReceiver);
    }

    /**
     * 关闭wifi广播(取消注册)
     */
    public void closeReceiver() {
        ClifeLog.e("uu ########################## closeReceiver " + mWifiReceiver);
        if (mWifiReceiver != null) {
            context.unregisterReceiver(mWifiReceiver);
        }
    }

    /**
     * 连接一个新的网络
     */
    public boolean connectNewNetwork(List<ScanResult> results, String SSID, String pwd) {
        int netId = addWifiConfig(results, SSID, pwd);
        //netId不等于-1，则说明添加成功，开始连接
        if (netId != -1) {
            return mWifiManager.enableNetwork(netId, true);
        }
        return false;
    }

    /**
     * 连接一个新的网络
     */
    public boolean connectNewNetwork(ScanResult result, String pwd) {
        int netId = addWifiConfig(result, pwd);
        //netId不等于-1，则说明添加成功，开始连接
        System.out.println("#######uuuuuuuu netId:" + netId);
        if (netId != -1) {
            return mWifiManager.enableNetwork(netId, true);
        }
        return false;
    }

    /**
     * 添加指定WIFI的配置信息,原列表不存在此SSID
     *
     * @param wifiList 所有列表
     * @param ssid     要连接的wifi名称
     * @param pwd      wifi密码
     * @return
     */
    private int addWifiConfig(List<ScanResult> wifiList, String ssid, String pwd) {
        int wifiId = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult wifi = wifiList.get(i);
            if (wifi.SSID.equals(ssid)) {
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.allowedAuthAlgorithms.clear();
                wifiCong.allowedGroupCiphers.clear();
                wifiCong.allowedKeyManagement.clear();
                wifiCong.allowedPairwiseCiphers.clear();
                wifiCong.allowedProtocols.clear();

                wifiCong.SSID = "\"" + wifi.SSID + "\"";//\"转义字符，代表"
                //无密码的连接
                if (pwd == null || pwd.equals("")) {
                    wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                //有密码的连接
                else {
                    wifiCong.preSharedKey = "\"" + pwd + "\"";//WPA-PSK密码
                    wifiCong.hiddenSSID = false;
                    wifiCong.status = WifiConfiguration.Status.ENABLED;
                }
                wifiId = mWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                if (wifiId != -1) {
                    return wifiId;
                }
            }
        }
        return wifiId;
    }

    /**
     * 添加指定WIFI的配置信息,原列表不存在此SSID
     *
     * @param result 连接的wifi对象
     * @param pwd    wifi密码
     * @return
     */
    private int addWifiConfig(ScanResult result, String pwd) {
        int wifiId = -1;
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.allowedAuthAlgorithms.clear();
        wifiCong.allowedGroupCiphers.clear();
        wifiCong.allowedKeyManagement.clear();
        wifiCong.allowedPairwiseCiphers.clear();
        wifiCong.allowedProtocols.clear();

        wifiCong.SSID = "\"" + result.SSID + "\"";//\"转义字符，代表"
        //无密码的连接
        if (pwd == null || pwd.equals("")) {
//            wifiCong.wepKeys[0] = "";
            wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiCong.wepTxKeyIndex = 0;
            wifiCong.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //有密码的连接
        else {
            wifiCong.preSharedKey = "\"" + pwd + "\"";//WPA-PSK密码
            wifiCong.hiddenSSID = false;
            wifiCong.status = WifiConfiguration.Status.ENABLED;
        }
        wifiId = mWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
        if (wifiId != -1) {
            return wifiId;
        }
        return wifiId;
    }

    /**
     * 开始扫描wifi
     */
    public void startScan() {
        if (mWifiManager != null) {
            mWifiManager.startScan();
        }
    }

    private boolean isStartScanMode = true;
    private Long sleepTime = 3000L;//睡眠时间

    /**
     * 启动扫描模式
     */
    public void startScanMode() {
        if (isStartScanMode) {
            isStartScanMode = false;
            new ScanThread().start();
        }
    }

    /**
     * 设置扫描周期(每隔多少毫秒扫描一次，不设置默认为3秒)
     *
     * @param time 毫秒数
     */
    public void setScanCycle(Long time) {
        sleepTime = time;
    }


    /**
     * 关闭扫描模式
     */
    public void stopScanMode() {
        isStartScanMode = true;
    }

    /**
     * 连接配置好的指定ID的网络
     *
     * @param networkId
     * @return
     */
    public boolean enableNetWork(int networkId) {
        if (mWifiManager != null) {
            return mWifiManager.enableNetwork(networkId, true);
        }
        return false;
    }

    /**
     * 获取已连接上的wifi名称
     *
     * @return
     */
    public String getConnectWifiSSID() {
        if (mWifiManager != null) {
            return mWifiManager.getConnectionInfo().getSSID();
        }
        return "";
    }

    /**
     * 获取已连接上的wifi名称
     *
     * @return
     */
    public WifiInfo getConnectInfo() {
        if (mWifiManager != null) {
            return mWifiManager.getConnectionInfo();
        }
        return null;
    }

    /**
     * 断开指定ID的网络
     *
     * @param netId wifi的id
     */
    public void disconnectWifi(int netId) {
        if (mWifiManager != null) {
            mWifiManager.disableNetwork(netId);
            mWifiManager.disconnect();
        }
    }

    /**
     * 取消保存
     */
    public void removeNetWork(WifiConfiguration wifiConfiguration) {
        if (mWifiManager != null && wifiConfiguration != null) {
            //移除网络
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
            //重新保存配置
            mWifiManager.saveConfiguration();
            mWifiManager.startScan();//重新扫描
        }
    }

    /**
     * 取消保存
     */
    public void removeNetWork(int networkId) {
        //移除网络
        mWifiManager.removeNetwork(networkId);
        //重新保存配置
        mWifiManager.saveConfiguration();
        mWifiManager.startScan();//重新扫描
    }


    /**
     * 获取wifi状态，返回int值，默认为已关闭
     *
     * @return
     */
    public int getWifiState() {
        return STATE;
    }

    /**
     * 设置wifi开关
     *
     * @param enabled false关闭,true打开
     * @return
     */
    public void setWifiEnabled(boolean enabled) {
        //判断wifi是不是未找到的状态，如果不是就进行设置
        if (STATE != UNKNOWN) {
            if (enabled) {
                //如果状态为未连接，则开始打开
                if (STATE == DISABLED) {
                    mWifiManager.setWifiEnabled(enabled);
                }
            } else {
                //如果状态为已打开，则开始关闭
                if (STATE == ENABLE) {
                    mWifiManager.setWifiEnabled(enabled);
                }
            }
        }
    }

    /**
     * 设置wifi扫描结果触发的监听
     *
     * @param listener
     */
    public void setOnScanResultsListener(OnWifiScanResultsListener listener) {
        onWifiScanResultsListener = listener;
    }

    /**
     * 设置wifi扫描结果触发的监听
     *
     * @param listener
     */
    public void setOnWifiSupplicantStateChangeListener(OnWifiSupplicantStateChangeListener listener) {
        onWifiSupplicantStateChangeListener = listener;
    }

    /**
     * 设置wifi信号改变触发的监听
     *
     * @param listener
     */
    public void setOnRSSIListener(OnWifiRSSIListener listener) {
        onWifiRSSIListener = listener;
    }

    /**
     * 设置wifi正在连接
     *
     * @param listener
     */
    public void setOnWifiConnectingListener(OnWifiConnectintListener listener) {
        onWifiConnectintListener = listener;
    }

    /**
     * 设置wifi闲置中
     *
     * @param listener
     */
    public void setOnWifiIDLEListener(OnWifiIDLEListener listener) {
        onWifiIDLEListener = listener;
    }

    /**
     * 设置wifi信状态改变触发的监听
     *
     * @param listener
     */
    public void setOnWifiStateChangeListener(OnWifiStateChangeListener listener) {
        onWifiStateChangeListener = listener;
    }


    /**
     * wifi的状态，wifi已断开
     */
    public static final int DISABLED = 01;

    /**
     * wifi的状态，wifi正在关闭
     */
    public static final int DISABLING = 02;

    /**
     * wifi的状态，wifi已打开
     */
    public static final int ENABLE = 03;

    /**
     * wifi的状态，正在打开
     */
    public static final int ENABLING = 04;

    /**
     * wifi的状态，未找到
     */
    public static final int UNKNOWN = 05;

    /**
     * wifi的状态
     */
    private int STATE = 01;


    /**
     * wifi信号接口，实现这个接口，当wifi信号改变时会触发
     */
    public static interface OnWifiRSSIListener {

        /**
         * 获取扫描接口
         */
        void onWifiRSSI(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }

    /**
     * wifi扫描接口，实现这个接口，当扫描到新的网络会触发
     */
    public static interface OnWifiScanResultsListener {

        /**
         * 获取扫描到的wifi列表
         */
        void onWifiScanResults(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }

    /**
     * wifi请求改变，实现这个接口，当发生请求时触发
     */
    public static interface OnWifiSupplicantStateChangeListener {

        /**
         * wifi请求改变
         */
        void OnWifiSupplicantStateChange(List<ScanResult> scanResult, List<WifiConfiguration> configurations);
    }

    /**
     * wifi连接成功回调
     */
    public static interface OnWifiConnectSuccessListener {

        /**
         * wifi连接密码成功回调
         *
         * @param wifiInfo 当前连接成功的wifi对象
         * @param isLock   是否有密码
         */
        void onWifiSuccess(WifiInfo wifiInfo, boolean isLock);

        void onWiFiDHCP(WifiInfo wifiInfo);
    }

    /**
     * wifi连接成功回调
     */
    public static interface OnWifiConnectintListener {

        void onWifiAuth(WifiInfo wifiInfo, boolean isLock);

        /**
         * wifi连接中回调
         *
         * @param wifiInfo 当前连接的wifi对象
         * @param isLock   是否有密码
         */
        void onWifiConnecting(WifiInfo wifiInfo, boolean isLock);

        /**
         * wifi连接密码成功回调
         *
         * @param wifiInfo 当前连接成功的wifi对象
         * @param isLock   是否有密码
         */
        void onWifiSuccess(WifiInfo wifiInfo, boolean isLock);

        /**
         * wifi连接密码错误回调
         *
         * @param configuration 当前连接错误的wifi对象
         */
        void onWifiPWDError(WifiConfiguration configuration);
    }

    /**
     * wifi闲置回调
     */
    public static interface OnWifiIDLEListener {

        /**
         * wifi闲置回调
         */
        void onWifiIDLE();
    }

    /**
     * wifi状态改变，实现这个接口，当wifi的状态改变时会触发
     */
    public static interface OnWifiStateChangeListener {
        /**
         * wifi状态改变
         */
        void onWifiStateChange(int state);

    }

    public static WifiManager getWifiManager() {
        return mWifiManager;
    }

    /**
     * 根据SSid找出已配置的消息
     *
     * @param SSID
     * @return
     */
    public WifiConfiguration getWifiConfiguration(String SSID) {
        WifiConfiguration wifiConfiguration = null;
        if (mWifiManager != null) {
            for (WifiConfiguration wcg : mWifiManager.getConfiguredNetworks()) {
                if (wcg.SSID.equals(SSID)) {
                    wifiConfiguration = wcg;
                    break;
                }
            }
        }
        return wifiConfiguration;
    }

    /**
     * 根据SSid找出已扫描的消息
     *
     * @param SSID
     * @return
     */
    public ScanResult getScanResult(String SSID) {
        ScanResult result = null;
        if (mWifiManager != null) {
            for (ScanResult s : mWifiManager.getScanResults()) {
                if (s.SSID.equals(SSID.replace("\"", ""))) {
                    result = s;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取wifi加密类型转换成String显示
     *
     * @param capability 加密字符串
     * @param context    上下文，用来进行文字国际化
     * @return
     */
    public static String getCapability(String capability, Context context) {
        String capabilityInfo = "";
        if (capability.contains("WPA2")) {
            if (capability.contains("WPA")) {
                capabilityInfo = "通过WPA/WPA2进行保护";
            } else if (capability.contains("WPS")) {
                capabilityInfo = "通过WPA2进行保护(可使用WPS)";
            } else {
                capabilityInfo = "通过WPA2进行保护";
            }
        } else if (capability.contains("WPS")) {
            capabilityInfo = "可使用WPS";
        }

        return capabilityInfo;
    }

    /**
     * 获取wifi加密类型
     */
    public static int getCapabilityType(String capability) {
        int type = 0;
        if (capability.contains("WPA2")) {
            if (capability.contains("WPA")) {
                type = 3;
            } else if (capability.contains("WPS")) {
                type = 3;
            } else {
                type = 3;
            }
        } else if (capability.contains("WPS")) {
            type = 1;
        }

        return type;
    }

    /**
     * 扫描线程
     */
    class ScanThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStartScanMode) {
                try {
                    //每隔3秒钟扫描一次，可以设置
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isStartScanMode) {
                    if (mWifiManager != null) {
                        //如果是为连接状态，则开始扫描
                        if (getWifiState() == ENABLE) {
                            startScan();//开始扫描
                        }
                    }
                }
            }
        }
    }


    /**
     * 接受wifi状态的广播
     */
    public class WifiBroadcast extends BroadcastReceiver {

        @SuppressWarnings("static-access")
        @Override
        public void onReceive(Context context, Intent intent) {
            //wifi状态改变
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    //赋值状态为已关闭
                    STATE = DISABLED;
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(DISABLED);
                    }
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
                    //赋值状态为正在关闭
                    STATE = DISABLING;
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(DISABLING);
                    }
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    //赋值状态为正在打开
                    STATE = ENABLING;
                    if (onWifiStateChangeListener != null) {
                        //wifi正在打开时，开始扫描
                        mWifiManager.startScan();
                        onWifiStateChangeListener.onWifiStateChange(ENABLING);
                    }
                } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    //赋值状态为已打开
                    STATE = ENABLE;
                    if (onWifiStateChangeListener != null) {
                        //wifi已打开时，开始扫描
                        mWifiManager.startScan();
                        onWifiStateChangeListener.onWifiStateChange(ENABLE);
                    }
                } else {
                    //赋值状态为未找到
                    STATE = UNKNOWN;
                    if (onWifiStateChangeListener != null) {
                        onWifiStateChangeListener.onWifiStateChange(UNKNOWN);
                    }
                }
            }
            //wifi扫描结果
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                if (onWifiScanResultsListener != null) {
                    onWifiScanResultsListener.onWifiScanResults(mWifiManager.getScanResults(), mWifiManager.getConfiguredNetworks());
                }
            }
            //wifi信号改变
            if (WifiManager.RSSI_CHANGED_ACTION.equals(intent.getAction())) {
                if (onWifiRSSIListener != null) {
                    onWifiRSSIListener.onWifiRSSI(mWifiManager.getScanResults(), mWifiManager.getConfiguredNetworks());
                }
            }


            //wifi请求状态改变
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (onWifiIDLEListener != null) {
                    //wifi闲置
                    if (mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.IDLE)) {
                        onWifiIDLEListener.onWifiIDLE();
                    }
                }
                if (onWifiConnectintListener != null) {
                    //wifi正在身份认证
                    if (mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.SCANNING)) {
                        ScanResult result = getScanResult(mWifiManager.getConnectionInfo().getSSID());
                        //isLock是否需要密码
                        boolean isLock = true;
                        if (result != null) {
                            isLock = (getCapabilityType(result.capabilities) == 0 || getCapabilityType(result.capabilities) == 1) ? false : true;
                        }
                        onWifiConnectintListener.onWifiAuth(mWifiManager.getConnectionInfo(), isLock);
                    }
                    //wifi正在连接中
                    if (mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.CONNECTING)) {
                        ScanResult result = getScanResult(mWifiManager.getConnectionInfo().getSSID());
                        //isLock是否需要密码
                        boolean isLock = true;
                        if (result != null) {
                            isLock = (getCapabilityType(result.capabilities) == 0 || getCapabilityType(result.capabilities) == 1) ? false : true;
                        }
                        onWifiConnectintListener.onWifiConnecting(mWifiManager.getConnectionInfo(), isLock);
                    }
                    //wifi连接成功
                    if (mWifiManager.getConnectionInfo().getDetailedStateOf(supplicantState).equals(DetailedState.OBTAINING_IPADDR)) {
                        ScanResult result = getScanResult(mWifiManager.getConnectionInfo().getSSID());
                        //isLock是否需要密码
                        boolean isLock = true;
                        if (result != null) {
                            isLock = (getCapabilityType(result.capabilities) == 0 || getCapabilityType(result.capabilities) == 1) ? false : true;
                        }
                        onWifiConnectintListener.onWifiSuccess(mWifiManager.getConnectionInfo(), isLock);
                    }

                    //wifi密码错误
                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                        System.out.println("@@@@@@=====  WiFi 密码错误");
                        WifiConfiguration wcg = getWifiConfiguration(mWifiManager.getConnectionInfo().getSSID());
                        //移除当前网络
                        removeNetWork(wcg);
                        //wifi密码错误时触发
                        onWifiConnectintListener.onWifiPWDError(wcg);
                    }
                }
                if (onWifiSupplicantStateChangeListener != null) {
                    onWifiSupplicantStateChangeListener.OnWifiSupplicantStateChange(mWifiManager.getScanResults(), mWifiManager.getConfiguredNetworks());
                }
            }
        }
    }


    /**
     * 获取本机
     *
     * @param ctx
     * @return
     */
    public static String getLocalIP(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long ip = di.ipAddress;
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        String ipStr = sb.toString();
        if (TextUtils.isEmpty(ipStr) || ipStr.equalsIgnoreCase("0.0.0.0")) {
            String ipStr1 = getLocalIpAddress();
            if (!TextUtils.isEmpty(ipStr1)) {
                return ipStr1;
            } else {
                return null;
            }
        }
        return sb.toString();
    }

    public static String getLocalIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 判断是否能够连接上互联网
     */
    public static boolean isInternet() {
        // 个人觉得使用MIUI这个链接有失效的风险
        final String checkUrl = "https://www.baidu.com";
        final int SOCKET_TIMEOUT_MS = 1000;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(checkUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(SOCKET_TIMEOUT_MS);
            connection.setReadTimeout(SOCKET_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.connect();

            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
