package com.het.ap.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class Utils {
    public static String[] getGateWayIp(Context context) {
        String ip = getLocalIP(context);
        if (TextUtils.isEmpty(ip))
            return null;
        String[] ips = ip.split("\\.");
        ip = ips[0] + "." + ips[1] + "." + ips[2] + ".1";
        String bIp = ips[0] + "." + ips[1] + "." + ips[2] + ".255";
        if (ip.equalsIgnoreCase("0.0.0.1"))
            return null;
        return new String[]{ip, bIp};
    }
    /**
     * 获取本机
     *
     * @param ctx
     * @return
     */
    public static String getLocalIP(Context ctx) {
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long ip = di.ipAddress;
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        String ipStr = sb.toString();
        if (TextUtils.isEmpty(ipStr) || ipStr.equalsIgnoreCase("0.0.0.0")) {
            String ipStr1 = getLocalIpAddress();
            if (!TextUtils.isEmpty(ipStr1)) {
                return ipStr1;
            }
        }
        return sb.toString();
    }

    public static String getLocalIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    public static String getCapabilities(Context context, String ssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> list = wifiManager.getScanResults();
        for (ScanResult scResult : list) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                return scResult.capabilities;
            }
        }
        return null;
    }



    /**
     * automode(1)+ssid.len(1)+pass.len(1)+ssid+pass
     *
     * @return
     */
    public static byte[] package8100Data(String ssid, String password, int automode) {
        int ssidLen = ssid.getBytes().length;
        int passLen = 0;
        if (password != null) {
            passLen = password.length();
        }
        int bodyLen = ssidLen + passLen + 1 + 1 + 1;
        byte[] body = new byte[bodyLen];
        body[0] = (byte) automode;
        body[1] = (byte) ssidLen;
        body[2] = (byte) passLen;
        System.arraycopy(ssid.getBytes(), 0, body, 1 + 1 + 1, ssidLen);
        if (password != null) {
            System.arraycopy(password.getBytes(), 0, body, ssidLen + 1 + 1 + 1, passLen);
        }
        return body;
    }

    /**
     * 0---不加密（open)
     * 1---WEP
     * 2---WPA
     * 3---WPA2
     * 4---WPA/WPA2
     */
    public static int getSecurity(String cap) {
        if (cap != null) {
            if (cap.toUpperCase().contains("WEP")) {
                return 1;
            } else if (cap.toUpperCase().contains("WPA")) {
                //Security.add("WPA-PSK");
                if (cap.toUpperCase().contains("WPA2"))
                    return 4;
                return 2;
            } else if (cap.toUpperCase().contains("WPA2")) {
                //Security.add("WPA-PSK");
                return 3;
            } else {
                return 0;

            }
        }
        return 0;
    }

    /**
     * 判断是否能够连接上互联网
     */
    public static boolean connBaiduTest() {
        // 个人觉得使用MIUI这个链接有失效的风险
        final String checkUrl = "https://www.baidu.com";
        final int SOCKET_TIMEOUT_MS = 1000;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(checkUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(SOCKET_TIMEOUT_MS);
            connection.setReadTimeout(SOCKET_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.connect();

            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
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
