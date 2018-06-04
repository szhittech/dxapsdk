package com.het.core.utils;

/**
 * Created by uuxia-mac on 15/3/20.
 */
public class Contants {
    /**
     * 输出包的包头长度
     */
    public static final int HET_LENGTH_BASIC_OUT_HEADER = 27;
    /**
     * 新协议输出包的包头长度V41
     */
    public static final int HET_LENGTH_NEW_BASIC_OUT_HEADER = 18;

    /**
     * 新协议输出包的包头长度V42
     */
    public static final int HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42 = 39;
    /**
     * 包尾长度
     */
    public static final int HET_LENGTH_BASIC_TAIL = 1;

    /**
     * 小循环控制发送配置数据
     */
    public static final short HET_LAN_SEND_CONFIG_REQ = 0x4007;
    /**
     * 小循环控制发送运行数据
     */
    public static final short HET_LAN_SEND_RUN_REQ = 0x4005;

    /**
     * 新协议AP绑定设备返回命令字
     */
    public static final short HET_NEW_BIND_RESPON_PROTOCOL_VERSION = 0x0001;
    /**
     * 新协议AP绑定设备返回命令字
     */
    public static final short HET_NEW_BIND_RESPON_PROTOCOL_VERSION_CHECK = 0x4001;
    /**
     * SmartLink绑定应答
     */
    public static final short HET_SMARTLINK_SEND_SERVER_INFO_RES = 0x0010;
    /**
     * 超时
     */
    public static final short HET_TIME_OUT = 0x1000;
    /**
     * 发送退出路由指令给设备
     */
    public static final short HET_SMARTLINK_SEND_EXIT_ROUTER = 0x4020;
    /**
     * 小循环控制设备定时广播允许数据
     */
    public static final short HET_LAN_TIMER_RUNNING = 0x0005;
    /**
     * 小循环控制设备回复配置数据
     */
    public static final short HET_LAN_SEND_CONFIG_RSP = 0x0007;
    /**
     * 小循环控制查询设备配置+运行数据
     */
    public static final short HET_LAN_QUERY_CONFIG_AND_RUNNING = 0x0017;
    /**
     * 新协议AP绑定
     */
    public static final short HET_NEW_BIND_REQ_PROTOCOL_VERSION = 0x4001;
    /**
     * SmartLink绑定请求
     */
    public static final short HET_SMARTLINK_SEND_SERVER_INFO_REQ = 0x4010;


    //***************************开放平台****************************//

    public final class OPEN {
        /**
         * 开放平台协议包长度
         */
        public static final int HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN = 35;
        /**
         * 开放平台协议包长度去掉5A头后包长度
         */
        public static final int HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A = 34;

        /**
         * 设备绑定指令集合
         */
        public final class BIND {
            /**
             * 手机扫描指令,要求设备收到该指令做0400回复
             */
            public static final short _HET_DISCOVER_DEVICE = 0x0100;
            /**
             * 发送Userkey、ServerId、port给设备
             */
            public static final short _HET_OPEN_BIND_SEND_SERVERINFO = 0x0200;
            /**
             * 发现设备
             */
            public static final short _HET_OPEN_BIND_DISCOVER_DEVICE = 0x0400;

            /**
             * 请求加密秘钥
             */
            public static final short _HET_OPEN_BIND_REQUEST_KEY = 0x0402;
            /**
             * 加密秘钥回复
             */
            public static final short _HET_OPEN_BIND_RESPONE_KEY = 0x0202;

            /**
             * 请求认证服务器
             */
            public static final short _HET_OPEN_BIND_AUTH_REQ = 0x0401;
            /**
             * 认证服务器回复
             */
            public static final short _HET_OPEN_BIND_AUTH_RES = 0x0201;
        }

        /**
         * 控制参数指令集合
         */
        public final class CONFIG {
            /**
             * 控制参数指令：上传控制参数（终端App），下发控制参数（App终端）
             */
            public static final short _HET_OPEN_CONFIG_SEND = 0x0104;
            /**
             * 控制参数指令：上传控制参数回复（App终端）下发控制参数回复（终端App）
             */
            public static final short _HET_OPEN_CONFIG_RECV = 0x0204;
            /**
             * 控制参数指令：请求设备上传控制参数（App终端） 数据内容:NULL
             */
            public static final short _HET_OPEN_CONFIG_REQ = 0x0404;
            /**
             * 控制参数指令：应答服务器请求设备上传控制参数（终端App）
             */
            public static final short _HET_OPEN_CONFIG_RES = 0x0304;
        }

        /**
         * 运行参数指令集合
         */
        public final class RUN {
            /**
             * 运行参数指令：上传运行参数（终端App） 周期性上传
             */
            public static final short _HET_OPEN_RUN_RECV = 0x0105;
            /**
             * 运行参数指令：上传运行参数（Server终端） 周期性上传
             */
            public static final short _HET_OPEN_RUN_RECV_RES = 0x0205;
            /**
             * 运行参数指令：请求设备发送运行状态（App终端）
             */
            public static final short _HET_OPEN_RUN_REQ = 0x0405;
            /**
             * 运行参数指令：应答服务器请求上传运行参数（终端App） 非周期性上传，立即上传
             */
            public static final short _HET_OPEN_RUN_RES = 0x0305;
            /**
             * 上传历史数据
             */
            public static final short _HET_OPEN_UPLOAD_HISTORY = 0x1104;
            /**
             * 上传历史数据回复
             */
            public static final short _HET_OPEN_UPLOAD_HISTORY_RES = 0x1204;
        }

        /**
         * 设备运行故障信息
         */
        public final class RUNERROR {
            /**
             * 运行故障参数指令：设备运行故障信息上传（终端App）
             */
            public static final short _HET_OPEN_RUN_ERR_RECV = 0x010E;
        }

        public final class HEART {
            /**
             * 上传心跳包
             */
            public static final short _HET_OPEN_HEART_REQ = 0x0408;

            /**
             * 服务器心跳包
             */
            public static final short _HET_OPEN_HEART_RSP = 0x0208;
        }
    }

}
