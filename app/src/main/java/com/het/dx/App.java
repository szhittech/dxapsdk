package com.het.dx;

import android.app.Application;

import com.fsix.mqtt.MqttConnManager;
import com.het.websocket.WsBootstrap;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WsBootstrap.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MqttConnManager.getInstances().stop();

        WsBootstrap.destroy(this);
    }
}
