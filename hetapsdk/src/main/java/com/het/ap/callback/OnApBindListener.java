package com.het.ap.callback;

import android.content.Context;

public abstract class OnApBindListener {
    private int timeout = 10;//默认10秒
    private boolean isTimeoutTrigger=false;

    public OnApBindListener() {
    }

    public OnApBindListener(int timeout) {
        this.timeout = timeout;
    }


    public boolean isTimeoutTrigger() {
        return isTimeoutTrigger;
    }

    public void setTimeoutTrigger(boolean timeoutTrigger) {
        isTimeoutTrigger = timeoutTrigger;
    }

    public int getTimeout() {
        return timeout;
    }

    public abstract void onConnRouterTimeOut(Context context);
    public abstract void onBindFailed();
    public abstract void onBindSucess();
}
