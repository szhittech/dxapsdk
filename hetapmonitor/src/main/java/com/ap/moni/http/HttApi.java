package com.het.gateway.sdk.http;

import com.het.basic.base.helper.RxSchedulers;
import com.het.gateway.sdk.interceptor.HeTLoggerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class HttApi {
    public static Observable<ResponseBody> doPost(String host, String path, Map param) {
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HeTLoggerInterceptor("uuok", true))
                        .connectTimeout(60000, TimeUnit.SECONDS)
                        .readTimeout(60000, TimeUnit.SECONDS)
                        .writeTimeout(60000, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 使用RxJava作为回调适配器
                .build()
                .create(HttpService.class)
                .doPost(path, param)
                .compose(RxSchedulers.io_main());
    }

    public static Observable<ResponseBody> doGet(String host, String path, Map param) {
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HeTLoggerInterceptor("uuok", true))
                        .connectTimeout(60000, TimeUnit.SECONDS)
                        .readTimeout(60000, TimeUnit.SECONDS)
                        .writeTimeout(60000, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 使用RxJava作为回调适配器
                .build()
                .create(HttpService.class)
                .doGet(path, param)
                .compose(RxSchedulers.io_main());
    }

    public static Response doAsyncGet(String host, String path, Map param) throws IOException {
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HeTLoggerInterceptor("uuok", true))
                        .connectTimeout(60000, TimeUnit.SECONDS)
                        .readTimeout(60000, TimeUnit.SECONDS)
                        .writeTimeout(60000, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 使用RxJava作为回调适配器
                .build()
                .create(HttpService.class)
                .doAsyncGet(path, param).execute();

    }
}
