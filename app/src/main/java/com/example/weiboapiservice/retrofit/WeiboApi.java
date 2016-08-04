package com.example.weiboapiservice.retrofit;

import com.example.weiboapiservice.model.SplashData;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by 青松 on 2016/8/4.
 */
public interface WeiboApi {

    @GET("1080*1776")
    Observable<SplashData> getSplashData();

}
