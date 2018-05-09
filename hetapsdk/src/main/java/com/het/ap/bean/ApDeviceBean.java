package com.het.ap.bean;

import android.net.wifi.ScanResult;

import java.io.Serializable;

public class ApDeviceBean implements Serializable {
    private String ssid;
    private String bssid;
    private ScanResult scanResult;

    public ApDeviceBean(ScanResult scanResult) {
        this.scanResult = scanResult;
        this.ssid = scanResult.SSID;
        this.bssid = scanResult.BSSID;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    @Override
    public String toString() {
        return "ApDeviceBean{" +
                "ssid='" + ssid + '\'' +
                ", bssid='" + bssid + '\'' +
                ", scanResult=" + scanResult +
                '}';
    }
}
