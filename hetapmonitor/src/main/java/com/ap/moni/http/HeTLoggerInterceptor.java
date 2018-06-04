package com.ap.moni.http;

import android.text.TextUtils;

import com.het.log.Logc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by zhy on 16/3/1.
 */
public class HeTLoggerInterceptor implements Interceptor {
    public static final String TAG = "uuok";
    private String tag;
    private boolean showResponse;

    public HeTLoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public HeTLoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Logc.e(tag, getRequestUrl(request));
        Response response = chain.proceed(request);
        Response result = LogcForResponse(response, request);
        return result;
    }

    private Response LogcForResponse(Response response, Request request) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            if (showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        if (isText(mediaType)) {
                            String resp = body.string();
                            Logc.e(tag, print(resp, request));
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            Logc.e(tag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!", false);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String print(String resp, Request request) throws IOException {
        String log = getRequestUrl(request) + "\r\n" + request.headers() + "response:" + resp;
        return log;
    }

    public static String getRequestUrl(Request request) throws UnsupportedEncodingException {
        String host = request.url().toString();
        if (request.method().equalsIgnoreCase("post")) {
            if (request.body() != null && request.body() instanceof FormBody) {
                FormBody requestBody = (FormBody) request.body();
                host = getRequest(host, processUrl(requestBody));
            }
        }
        host = URLDecoder.decode(host, "utf-8");
        return host;
    }

    private static Map<String, String> processUrl(FormBody body) {
        Map<String, String> param = new HashMap<>();
        for (int i = 0; i < body.size(); i++) {
            String name = body.encodedName(i);
            String value = body.encodedValue(i);
            try {
                value = URLDecoder.decode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            param.put(name, value);
        }
        return param;
    }

    private static String getRequest(String apiUrl, Map<String, String> mParam) {
        String tmp;
        StringBuilder sb = new StringBuilder();
        sb.append(apiUrl);
        sb.append("?");
        Iterator<String> it = mParam.keySet().iterator();
        while (it.hasNext()) {
            tmp = it.next();
            sb.append(tmp).append("=").append(mParam.get(tmp));
            if (it.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }


    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}

