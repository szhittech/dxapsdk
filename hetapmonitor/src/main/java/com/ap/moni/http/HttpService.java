package com.ap.moni.http;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface HttpService {
    @FormUrlEncoded
    @POST("{path}")
    Observable<ResponseBody> doPost(@Path("path") String path, @FieldMap Map<String, String> map);

    @GET("{path}")
    Observable<ResponseBody> doGet(@Path("path") String path, @QueryMap Map<String, String> map);

    @GET("{path}")
    Call<ResponseBody> doAsyncGet(@Path("path") String path, @QueryMap Map<String, String> map);
}
