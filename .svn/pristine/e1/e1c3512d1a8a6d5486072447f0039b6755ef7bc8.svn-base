package com.het.dx;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.het.dx.ui.BaseActivity;
import com.het.dx.ui.ScanActivity;

public class MainActivity extends BaseActivity {
    private EditText ssid_et,pass_et,mode_et;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onView() {
        ssid_et = (EditText) findViewById(R.id.ssid_id);
        pass_et = (EditText) findViewById(R.id.pass_id);
        mode_et = (EditText) findViewById(R.id.mode_id);
    }

    @Override
    protected void onDataInit() {
        ssid_et.setText(getSSid(this));
    }

    public void onScanWiFi(View view) {
        String ssid = ssid_et.getText().toString();
        String pass = pass_et.getText().toString();
        String mode = mode_et.getText().toString();
        if (TextUtils.isEmpty(ssid)){
            tips("ssid不能为空");
            return;
        }
        if (TextUtils.isEmpty(pass)){
            tips("pass不能为空");
            return;
        }
        if (TextUtils.isEmpty(mode)){
            tips("mode不能为空");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("ssid",ssid);
        intent.putExtra("pass",pass);
        intent.putExtra("mode",mode);
        intent.setClass(this, ScanActivity.class);
        startActivity(intent);
    }

    public static String getSSid(Context ctx) {
        WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        // 用来获取当前已连接上的wifi的信息
        if (mWifiManager == null) {
            return "";
        }
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null) {
            return "";
        }
        if (mWifiInfo.getSSID() == null) {
            return "";
        }

        String ssid = mWifiInfo.getSSID();
        // int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        // 16之后的版本 取ssid时会自动带“”. 汉方模块要去掉
        if (android.os.Build.VERSION.SDK_INT > 16) {
            if (!TextUtils.isEmpty(ssid)) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }
        return ssid;
    }
}
