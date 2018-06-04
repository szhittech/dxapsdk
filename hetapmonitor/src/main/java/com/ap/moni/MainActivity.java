package com.ap.moni;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ap.moni.ap.WiFiApManager;
import com.ap.moni.base.BaseActivity;
import com.ap.moni.http.HttpApi;
import com.ap.moni.udp.ApbindBean;
import com.ap.moni.udp.UdpEventHandle;
import com.ap.moni.util.APConst;
import com.ap.moni.util.Base64;
import com.ap.moni.util.LogTool;
import com.ap.moni.wifi.WiFiEventHandle;
import com.het.core.HeTCoreServiceManager;
import com.het.core.core.base.BaseLogic;
import com.het.core.model.HetCoreModel;
import com.het.core.model.ServerModel;
import com.het.core.utils.Base64Utils;
import com.het.core.utils.PrefersUtils;

import java.util.Random;


public class MainActivity extends BaseActivity implements LogTool.ILogNotify {
    private EditText device_typeid, device_subtypeid, device_code, ssid_id;
    private TextView logger;
    private ScrollView scrollView;
    private String ssid = "HET_00";
    private Button start_id;

    private UdpEventHandle udpEventHandle;
    private WiFiEventHandle wiFiEventHandle;

    private String monitorMac = "aaaaaa";
    private String deviceCode;

    private LogTool logTool;// = new LogTool(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onView() {
        device_typeid = (EditText) findViewById(R.id.device_typeid);
        device_subtypeid = (EditText) findViewById(R.id.device_subtypeid);
        device_code = (EditText) findViewById(R.id.device_code);
        ssid_id = (EditText) findViewById(R.id.ssid_id);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        logger = (TextView) findViewById(R.id.logger);
        start_id = findViewById(R.id.start_id);

        start_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideInputMethod();
                onStartMonitor();
            }
        });
    }

    @Override
    protected void onDataInit() {
        udpEventHandle = new UdpEventHandle(handler);
        wiFiEventHandle = new WiFiEventHandle(handler,this);
        logTool = new LogTool(this);
        startLiveLog();
    }

    private void startLiveLog(){
        if (logTool!=null){
            logTool.startLiveLogThread();
        }
    }

    private void stopLiveLog(){
        if (logTool!=null){
            logTool.stopLiveLogThread();
        }
    }

    private boolean createSsid() {
        String deviceTypeId = device_typeid.getText().toString();
        String deviceSubTypeId = device_subtypeid.getText().toString();
        deviceCode = device_code.getText().toString();
        if (TextUtils.isEmpty(deviceTypeId)) {
            tips("请输入设备大类");
            return false;
        }
        if (TextUtils.isEmpty(deviceSubTypeId)) {
            tips("请输入设备小类");
            return false;
        }
        if (TextUtils.isEmpty(deviceCode)) {
            tips("请输入设备编码");
            return false;
        }

        APConst.deviceType = Integer.parseInt(deviceTypeId);
        String a = Integer.toHexString(APConst.deviceType);
        if (a.length() == 1) {
            a = "0" + a;
        }
        APConst.deviceSubType = Integer.parseInt(deviceSubTypeId);
        String b = Integer.toHexString(APConst.deviceSubType);
        APConst.deviceMac = monitorMac;

        if (b.length() == 1) {
            b = "0" + b;
        }
        ssid = "HET_00";
        ssid += a;
        ssid += b;
        ssid += "_1234";
        ssid_id.setText(ssid);
        return true;
    }

    public void onGenerateSSID(View view) {
        createSsid();
    }

    public void onStartMonitor() {
        if (!createSsid())
            return;
        monitorMac = monitorMac + new Random().nextInt(1000000);
        //注册handler
        WiFiApManager.getInstance().setWiFiApStateListener(new WiFiApManager.WiFiApStateListener() {

            @Override
            public void onApStateEnabled(final String ssid, final String password, final int security) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "热点设置成功，ssid:" + ssid + " pass：" + password + " security:" + security, Toast.LENGTH_LONG).show();
//                        showLog("热点设置成功，ssid:" + ssid + " pass：" + password + " security:" + security);
                        udpEventHandle.start(MainActivity.this);
                    }
                });
            }

            @Override
            public void onApStateDisabled() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApStateDisabled", Toast.LENGTH_LONG).show();
//                        showLog("onApStateDisabled");
                    }
                });
            }

            @Override
            public void onApStateFailed() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onApStateFailed", Toast.LENGTH_LONG).show();
//                        showLog("onApStateFailed");
                    }
                });
            }
        });
        //开启wifi热点
        WiFiApManager.getInstance().turnOnWifiAp(this, ssid);
    }

    private void showLog(String text) {
        Message msg = Message.obtain();
        msg.obj = text;
        msg.what = 0;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    logger.append(msg.obj.toString() + "\r\n");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case 1://UDP 8100/8200交互完毕
//                    onSopMonitor(null);
                    ApbindBean apbindBean = (ApbindBean) msg.obj;
                    wiFiEventHandle.connect(apbindBean);
                    break;
                case 2://设备连接上了路由器
                    ApbindBean apData = (ApbindBean) msg.obj;
                    getConfig(apData);
                    break;

                default:
                    break;
            }

        }
    };

    private void getConfig(final ApbindBean apData) {
        PrefersUtils.init(this).save("isAuth", false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ServerModel data = HttpApi.getWiFiBindConfig(apData.getRandom(), monitorMac, deviceCode, apData.getHostType());
                PrefersUtils.init(MainActivity.this).save("AuthServerInfo", Base64Utils.getBase64Data(data));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {
                            HetCoreModel hetCoreModel = new HetCoreModel();
                            hetCoreModel.setDeviceCode(deviceCode);
                            hetCoreModel.setDeviceMacAddr(monitorMac);
//                    Logic.getInstance().getLogic(false).startLogic(data);
                            HeTCoreServiceManager.getInstance().setCoreModel(hetCoreModel);
//                    HeTCoreServiceManager.getInstance().setHostType(apData.getHostType());
                            HeTCoreServiceManager.getInstance().start(MainActivity.this);
                        }
                    }
                });
            }
        }).start();

    }

    public void onSopMonitor(View view) {
        WiFiApManager.getInstance().closeWifiAp(this);
        udpEventHandle.destroy();
    }

    @Override
    public void notify(String text) {
        showLog(text);
    }
}
