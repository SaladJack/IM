package com.saladjack.im.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by saladjack on 17/1/25.
 */

public class RetrofitMananger {
    private static RetrofitMananger sInstance;
    private Retrofit retrofit;
    private RetrofitMananger(){}
    public static RetrofitMananger getInstance(){
        if(sInstance == null)
            sInstance = new RetrofitMananger();
        return sInstance;
    }
    private void build() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(HttpUtil.TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HttpUtil.BASE_URL)
                .build();
    }

    public Retrofit getRetrofit(){
        if(retrofit == null)
            build();
        return retrofit;
    }

}
