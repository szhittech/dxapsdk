package com.het.ap;

import android.content.Context;
import android.text.TextUtils;

import com.het.ap.bean.ApDeviceBean;
import com.het.ap.callback.OnApScanListener;
import com.het.ap.callback.OnApBindListener;
import com.het.ap.tcp.ApTcpHelper;
import com.het.ap.util.ClifeLog;
import com.het.ap.wifi.ApScanner;

public class HeTApApi {
    private static HeTApApi INSTANCE = null;
    private ApScanner apScanner;
    private ApTcpHelper apTcpHelper;

    public static HeTApApi getInstance() {
        if (INSTANCE == null) {
            synchronized (HeTApApi.class) {
                if (null == INSTANCE) {
                    INSTANCE = new HeTApApi();
                }
            }
        }
        return INSTANCE;
    }

    private void init(Context context){
        apScanner = new ApScanner(context);
        apTcpHelper = new ApTcpHelper();
    }

    public void scan(Context context, String ssid, String pass, String deviceMode, OnApScanListener onApScanListener, long scanTimeout){
        stop();
        init(context);
        apScanner.setScanTimeout(scanTimeout);
        apScanner.setRouterPassword(pass);
        apScanner.setRouterSsid(ssid);
        apScanner.setFilter(deviceMode);
        apScanner.setOnApScanListener(onApScanListener);
        apScanner.startScan();
    }

    public void scan(Context context,String ssid, String pass, String deviceMode, OnApScanListener onApScanListener){
        scan(context,ssid,pass,deviceMode, onApScanListener,100);
    }

    public void bind(ApDeviceBean apDeviceBean,String apPassword,OnApBindListener onApBindListener,long bindTimeout){
        if (apDeviceBean == null || TextUtils.isEmpty(apDeviceBean.getSsid())) {
            ClifeLog.e("apDeviceBean is null");
            return;
        }

        apTcpHelper.setBody(apScanner.getBody());
        apTcpHelper.setApTransDone(apScanner);

        apScanner.setBindTimeout(bindTimeout);
        apScanner.stopScan();
        apScanner.setApSendData(apTcpHelper);
        apScanner.setOnApBindListener(onApBindListener);
        apScanner.connApDevice(apDeviceBean.getSsid(),apPassword);
    }

    public void bind(ApDeviceBean apDeviceBean,String apPassword,OnApBindListener onApBindListener){
       bind(apDeviceBean,apPassword,onApBindListener,100);
    }

    public void stop(){
        if (apScanner!=null){
            apScanner.release();
        }
        if (apTcpHelper!=null){
            apScanner.stopScan();
        }
    }

    public void setLogEnable(boolean on){
        ClifeLog.DEBUG = on;
    }
}
