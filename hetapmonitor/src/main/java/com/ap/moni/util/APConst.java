package com.het.gateway.util;
import com.clife.smarthome.common.DriverContants;
import com.clife.smarthome.common.DriverInfo;
import com.clife.smarthome.common.SocketModel;

public class APConst {
    static {
        broadcastAddress = IPUtil.getPCBroadcastAddress();
        localIp = IPUtil.getPCIpAddress();
        deviceMac = IPUtil.getDeviceMac();
        broadcastAddress = DriverInfo.getBroadcastAddress();

        getPlatformInfo();
    }

    public static void getPlatformInfo(){
        SocketModel socketModel = DriverInfo.getSocketInfo();
        localIp = socketModel.getIp();
        deviceMac = socketModel.getMac();
        deviceType = DriverContants.deviceType;
        deviceSubType = DriverContants.deviceSubType;
        broadcastAddress = DriverInfo.getBroadcastAddress();
    }



    public static Integer deviceType = 80;
    public static Integer deviceSubType = 5;

    public static String deviceMac;

    public static String localIp;

    public static String broadcastAddress;

}
