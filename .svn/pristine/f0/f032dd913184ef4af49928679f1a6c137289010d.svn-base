package com.het.dx.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.het.dx.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public abstract class BaseActivity extends AppCompatActivity {

    protected RxPermissions rxPermissions;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    //声明进度条对话框
    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions();
        this.setContentView(this.getLayoutId());
        progressDialog = new ProgressDialog(this);
        this.onView();
        this.onDataInit();
    }

    private void getPermissions() {
        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(this);
        }
        rxPermissions.request(permissions)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        tips("权限获取" + (aBoolean ? "成功" : "失败"));
                    }
                });
    }


    public void tips(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showDialog(@Nullable final CharSequence message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(BaseActivity.this)
                        .setTitle("温馨提示")
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    protected void showDialog(@Nullable final CharSequence message, final DialogInterface.OnClickListener ok) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(BaseActivity.this)
                        .setTitle("温馨提示")
                        .setMessage(message)
                        .setPositiveButton("确定", ok)
                        .setNegativeButton("取消", null)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    protected void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.dialog, null);
                final EditText ssid = (EditText) view.findViewById(R.id.ssid_id);
                final EditText pass = (EditText) view.findViewById(R.id.pass_id);
                new AlertDialog.Builder(BaseActivity.this)
                        .setTitle("WiFi连接")
                        .setView(view)
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("取消", null)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    protected void showLoading() {
        showLoading(null);
    }

    protected void showLoading(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(TextUtils.isEmpty(msg) ? "加载中..." : msg);
                progressDialog.show();
            }
        });
    }

    protected void hideLoading() {
        if (!progressDialog.isShowing())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }


    protected abstract int getLayoutId();

    protected abstract void onView();

    protected abstract void onDataInit();
}
