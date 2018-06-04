package com.het.gateway.bean;

public class ApbindBean {
    private String random;
    private String ssid;
    private String password;
    private int hostType;
    private int capabilities;

    public ApbindBean(String random, String ssid, String password, int hostType, int capabilities) {
        this.random = random;
        this.ssid = ssid;
        this.password = password;
        this.hostType = hostType;
        this.capabilities = capabilities;
    }

    @Override
    public String toString() {
        return "ApbindBean{" +
                "random='" + random + '\'' +
                ", ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", hostType=" + hostType +
                ", capabilities=" + capabilities +
                '}';
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHostType() {
        return hostType;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public int getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(int capabilities) {
        this.capabilities = capabilities;
    }
}
