package com.example.weiboapiservice.retrofit;

import android.text.TextUtils;

import com.example.weiboapiservice.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.Constants;

/**
 * Created by 青松 on 2016/8/4.
 */
public class WeiboApiFactory {

    public static WeiboApi createWeiboApi(String baseUrl, final String token) {
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = Constants.BASE_URL_WEIBO;
        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token).build();
                return chain.proceed(request);
            }
        });

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(WeiboApi.class);
    }

    public static WeiboApi createSplashApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().build();
                return chain.proceed(request);
            }
        });

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL_SPLASH)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(WeiboApi.class);
    }

}
