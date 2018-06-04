package com.ap.moni.http;

import java.util.HashMap;
import java.util.Map;

public class ApiContants {

    public enum AuthMethod{
        GET, POST, PUT, DELETE;
    }

    public static Map<Integer, String> domainMap = new HashMap<Integer, String>();

    static{
        domainMap.put(0, "https://dp.clife.net");
        domainMap.put(1, "https://pre.clife.cn");
        domainMap.put(2, "https://dp.clife.net");
    }

    //获取token信息，方便后续调用云端api接口
    public final static String access_token_url = "/v1/user/third/bind";

    //根据产品ID获取协议列表
    public final static String protocol_list_url = "/v1/device/protoManage/getProtocolListByProductId";

    //获取绑定地址
    public final static String getWiFiBindConfig_url = "/v1/device/init/getWiFiBindConfig";

    //获取mac的产品ID
    public final static String getProductIdByMac_url = "/v1/device/getProductIdByMac";

    //获取mac的获取设备ID
    public final static String getDeviceMac_url = "/v1/device/getDeviceMac";

    //获取场景列表
    //http://weixin.clife.cn/clife-wechat/wechat/hotel/scene/sceneList
    public final static String sceneList_url = "/v1/app/expert/userScene/sceneList/v1.0";

//	https://dp.clife.net/v1/app/expert/userScene/sceneList/v1.0?appId=30590&timestamp=1515482704029&paged=false&experience=0&parent.sceneName=%E9%85%92%E5%BA%97-%E7%9D%A1%E7%9C%A0

    public final static String sceneRules_url = "/v1/app/expert/userScene/gateway/getSceneRules/";

}
