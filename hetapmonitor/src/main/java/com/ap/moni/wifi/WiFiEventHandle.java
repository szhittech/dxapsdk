package com.ap.moni.wifi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ap.moni.udp.ApbindBean;
import com.ap.moni.util.Logc;
import com.ap.moni.wifi.tool.WiFiTool;
import com.ap.moni.wifi.tool.callback.WiFiConnCallback;

public class WiFiEventHandle {
    private WiFiTool wifiConnect;
    private Handler handler;

    public WiFiEventHandle(Handler handler,Context context) {
        this.handler = handler;
        wifiConnect = new WiFiTool(context);
    }


    public void connect(final ApbindBean apbindBean){
        wifiConnect.conn(apbindBean.getSsid(), apbindBean.getPassword(), new WiFiConnCallback() {
            @Override
            public int onWiFiConnected(String ssid, String password) {
                Logc.i("onWiFiConnected " + ssid + " " + password);
                return 0;
            }

            @Override
            public boolean onInternetConnected(String ip) {
                Logc.i("onInternetConnected " + ip);
                Message msg = Message.obtain();
                msg.obj = apbindBean;
                msg.what = 2;
                handler.sendMessage(msg);
                return true;
            }

            @Override
            public void onFailed(String msg) {
                Logc.i("onFailed " + msg);
            }
        });
    }
}
