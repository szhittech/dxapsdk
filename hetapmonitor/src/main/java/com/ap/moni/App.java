package com.ap.moni;

import android.app.Application;

import com.het.udp.core.Utils.SystemUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String processName = SystemUtils.getProcessName(this, android.os.Process.myPid());
        if (processName.equals(this.getPackageName())) {

        }
    }
}
