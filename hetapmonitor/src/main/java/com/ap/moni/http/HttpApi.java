package com.ap.moni.http;

import com.ap.moni.util.Logc;
import com.google.gson.reflect.TypeToken;
import com.het.basic.model.ApiResult;
import com.het.basic.utils.GsonUtil;
import com.het.core.model.ServerModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TreeMap;

import okhttp3.ResponseBody;

public class HttpApi {
    public static ServerModel getWiFiBindConfig(String random, String monitorMac, String deviceCode, int hostType) {
        String domain = ApiContants.domainMap.get(hostType);
        String getWiFiBindConfig_url = "/v1/device/init/getWiFiBindConfig";
        TreeMap map = new TreeMap();
        map.put("mac",monitorMac);
        map.put("deviceCode",deviceCode);
        map.put("bindCode",random);
        map.put("moduleType","1");
        try {
            retrofit2.Response responseBody = HttApi.doAsyncGet(domain, getWiFiBindConfig_url, map);
            String bodyString = ((ResponseBody) responseBody.body()).string();
            if (bodyString != null) {
                Logc.i(bodyString);
                Type type = (new TypeToken<ApiResult<ServerModel>>() {}).getType();
                ApiResult<ServerModel> apiResult = GsonUtil.getInstance().toObject(bodyString, type);
                if (apiResult.getCode() == 0) {
                    ServerModel sences = apiResult.getData();
                    return sences;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
