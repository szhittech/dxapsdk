package com.het.ap.callback;

import android.content.Context;

import com.het.ap.bean.ApDeviceBean;

public abstract class OnApScanListener {
    private boolean isconnApCountTrigger=false;
    private int connApCount = 15;//默认15秒

    public OnApScanListener() {
    }

    public OnApScanListener(int timeout) {
        this.connApCount = timeout;
    }

    public boolean isconnApCountTrigger() {
        return isconnApCountTrigger;
    }

    public void setIsconnApCountTrigger(boolean isconnApCountTrigger) {
        this.isconnApCountTrigger = isconnApCountTrigger;
    }

    public int getConnApCount() {
        return connApCount;
    }

    public abstract void onConnDeviceApTimeout(Context context, String ssid);
    public abstract void onScanResult(ApDeviceBean apDeviceBean);
    public abstract void onScanFailed();
}
