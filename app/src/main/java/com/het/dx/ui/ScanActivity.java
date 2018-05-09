package com.het.dx.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.het.ap.HeTApApi;
import com.het.ap.bean.ApDeviceBean;
import com.het.ap.callback.OnApBindListener;
import com.het.ap.callback.OnApScanLinstaner;
import com.het.dx.R;
import com.het.dx.adpter.WiFiAdpter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScanActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private WiFiAdpter wiFiAdpter;
    private ListView listView;
    private List<ApDeviceBean> list = new ArrayList<>();
    private Set<String> filter = new HashSet<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void onView() {
        wiFiAdpter = new WiFiAdpter(this,list);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(wiFiAdpter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onDataInit() {
        Intent intent = getIntent();
        String ssid = intent.getStringExtra("ssid");
        String pass = intent.getStringExtra("pass");
        String mode = intent.getStringExtra("mode");

        HeTApApi.getInstance().setLogEnable(true);

        HeTApApi.getInstance().scan(this,ssid,pass,mode,onApScanLinstaner);
        showLoading("扫描Ap热点中...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HeTApApi.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HeTApApi.getInstance().stop();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ApDeviceBean target = list.get(i);
        if (target==null)
            return;
        HeTApApi.getInstance().bind(target,null,onApBindListener);
        showLoading("绑定["+ target.getSsid() +"]设备中...");
    }


    private OnApScanLinstaner onApScanLinstaner = new OnApScanLinstaner() {
        @Override
        public void onConnDeviceApTimeout(Context context, String ssid) {
            hideLoading();
            showDialog("连接设备热点["+ ssid+"]失败，是否尝试手动连接", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                    startActivity(wifiSettingsIntent);
                }
            });
        }

        @Override
        public void onScanResult(ApDeviceBean apDeviceBean) {
            if (filter.contains(apDeviceBean.getBssid()))
                return;
            filter.add(apDeviceBean.getBssid());
            list.add(apDeviceBean);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    wiFiAdpter.notifyChanged();
                    hideLoading();
                }
            });
        }

        @Override
        public void onScanFailed() {

        }
    };

    private OnApBindListener onApBindListener = new OnApBindListener() {
        @Override
        public void onConnRouterTimeOut(Context context) {
            hideLoading();
            showDialog("连接Wi-Fi，是否尝试手动连接路由器", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                    startActivity(wifiSettingsIntent);
                }
            });
        }

        @Override
        public void onBindFailed() {
            hideLoading();
            showDialog("配网失败", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ScanActivity.this.finish();
                }
            });
        }

        @Override
        public void onBindSucess() {
            hideLoading();
            showDialog("恭喜，配网成功", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ScanActivity.this.finish();
                }
            });
        }


    };


}
