package com.het.ap.tcp;

import com.het.ap.util.ClifeLog;
import com.het.ap.wifi.callback.IApSendData;
import com.het.ap.wifi.callback.IApTransDone;
import com.het.ap.util.ApConst;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

public class ApTcpHelper implements TcpHelper.OnReceiveEvent, IApSendData {
    private TcpHelper tcpHelper;
    private byte[] data_8100;
    private IApTransDone apTransDone;

    public ApTcpHelper() {
        tcpHelper = new TcpHelper();
        tcpHelper.setReceiveEvent(this);
    }

    @Override
    public void ReceiveBytes(byte[] iData) {
        ClifeLog.e(" uu== 收到数据:" + ByteUtils.toHexString(iData));
        parse(iData);
    }

    @Override
    public void ReceiveString(String iData) {
    }

    public void setApTransDone(IApTransDone apTransDone) {
        this.apTransDone = apTransDone;
    }

    private void parse(byte[] data) {
        if (data == null || data.length == 0)
            return;
        byte packetStart = data[0];
        PacketModel packets = new PacketModel();
        //来源数据命令字
        int cmd = ByteUtils.getCommandNew(data);
        if (packetStart == 0x5A) {
            //针对开放平台协议获取命令字，其数组索引号为31；
            cmd = ByteUtils.getCommandForOpen(data);
            //标记协议为开放平台协议
            packets.setOpenProtocol(true);
            int cmd_8200 = Contants.OPEN.BIND._HET_OPEN_BIND_RECV_SSIDINFO&0xFFFF;
            if (cmd_8200 == cmd) {
                ClifeLog.e(" uu== 收到8200:" + ByteUtils.toHexString(data));
                if (apTransDone != null) {
                    apTransDone.onTrasnDone();
                    apTransDone = null;
                    tcpHelper.stop();
                }
            }
        }
    }

    public void stop(){
        if (tcpHelper!=null){
            tcpHelper.stop();
        }
    }

    public void setBody(byte[] body) {
        data_8100 = package8100Data(body);
    }

    private byte[] package8100Data(byte[] body) {
        ClifeLog.i("package0100Data info :" + ByteUtils.toHexStrings(body));
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setDeviceMac(null);
        udpDeviceDataBean.setPacketStart((byte) 0x5A);
        udpDeviceDataBean.setCommandType(Contants.OPEN.BIND._HET_OPEN_BIND_SEND_SSIDINFO);
        p.setDeviceInfo(udpDeviceDataBean);
        p.setBody(body);
        PacketUtils.out(p);
        return p.getData();
    }

    @Override
    public boolean onSendData(String ip) {
        if (!tcpHelper.isInit()) {
            tcpHelper.connect(ip, ApConst.TCP_PORT);
        }
        if (tcpHelper.isConnected()) {
            ClifeLog.e(" uu== 正在发送8100指令 IP:" + ip + " body:" + ByteUtils.toHexString(data_8100));
//            ClifeLog.e(" uu== 正在发送8100指令 IP:" + ip + " body:" + Arrays.toString(data_8100));
            tcpHelper.send(data_8100);
        }
        return false;
    }
}
