package com.ap.moni.udp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ap.moni.util.APConst;
import com.ap.moni.util.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.observer.IObserver;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.packet.factory.vopen.Packet_open;
import com.het.udp.wifi.utils.ByteUtils;

import java.nio.ByteBuffer;

public class UdpEventHandle implements IObserver {
    private Context context;
    public static final short HET_AP_RECV = (short) 0x8100;
    public static final short HET_AP_REPLY = (short) 0x8200;
    private static Thread recv8200Thread = null;
    private Handler handler;

    public UdpEventHandle(Handler handler) {
        this.handler = handler;
    }

    public void start(Context context){
        this.context = context;
        try {
            UdpDataManager.getInstance().init(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UdpDataManager.registerObserver(this);
    }

    public void destroy(){
        try {
            UdpDataManager.getInstance().unBind(this.context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UdpDataManager.unregisterObserver(this);
    }

    @Override
    public void receive(PacketModel o) {
        if (o == null)
            return;
        recv(o);
    }


    public void recv(PacketModel packet) {
        if (HET_AP_RECV == packet.getCommand()) {
            UdpDeviceDataBean deviceInfo = packet.getDeviceInfo();
            byte[] body = packet.getBody();
            if (body != null) {
                if (deviceInfo != null) {
                    String deviceMacAddr = deviceInfo.getDeviceMac();
                    int deviceType = deviceInfo.getDeviceType();
                    int deviceSubType = deviceInfo.getDeviceSubType();
                }

                //random.byte(8)+automode(1)+ssid.len(1)+pass.len(1)+ssid+pass
                ByteBuffer bb = ByteBuffer.allocate(body.length);
                bb.put(body);
                bb.flip();
                byte[] ran = new byte[8];
                bb.get(ran);
                String random = new String(ran);
                byte hostCap = bb.get();
                byte ssidlen = bb.get();
                byte passlen = bb.get();
                byte[] s = new byte[ssidlen];
                byte[] p = new byte[passlen];
                bb.get(s);
                bb.get(p);
                String ssid = new String(s);
                String pass = new String(p);
                int hostType = hostCap & 0xF0;
                hostType >>=4;
                int cap = hostCap & 0x0F;
                ApbindBean apbindBean = new ApbindBean(random,ssid,pass,hostType,cap);
                if (recv8200Thread == null){
                    pack8200cmd(packet,apbindBean);
                }
            }
        }
    }

    private void pack8200cmd(final PacketModel packet,final ApbindBean apbindBean){
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        Logc.e("uu ######pack8200cmd ");
        if (APConst.deviceMac != null) {
            String targetIp = packet.getIp();
            int deviceType = APConst.deviceType;
            int deviceSubType = APConst.deviceSubType;
            String deviceMacAddr = APConst.deviceMac;// DriverInfo.deviceModel.getMacAddr();
            Logc.e("uu #####pack8200cmd" + deviceType + " " + deviceSubType + " mac" + deviceMacAddr);
            udpDeviceDataBean.setDeviceType((byte) deviceType);// TODO
            udpDeviceDataBean.setDeviceSubType((byte) deviceSubType);// TODO
            udpDeviceDataBean.setDeviceMac(deviceMacAddr);// TODO

            udpDeviceDataBean.setCommandType(HET_AP_REPLY);
            udpDeviceDataBean.setPacketStart(Packet_open.packetStart);
            udpDeviceDataBean.setIp(targetIp);
            packet.setDeviceInfo(udpDeviceDataBean);
            PacketUtils.out(packet);

            try {
                packet.setPort(APConst.listenerPort);
                Logc.e("##################pack8200cmd......" + ":" + packet.getPort() + " ip: "  + packet.getIp());
                Logc.e("uu ######pack8200cmd# " + ByteUtils.toHexString(packet.getData()));
                recv8200Thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i< 3;i++){
                            try {
                                UdpDataManager.getInstance().send(packet);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        destroy();
                        Message msg = Message.obtain();
                        msg.obj = apbindBean;
                        msg.what = 1;
                        handler.sendMessage(msg);

                    }
                });
                recv8200Thread.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Logc.e("uu ######ServiceAPI.getDriveInfo() is null ");
        }
    }
}
