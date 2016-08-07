package com.example.weiboapiservice.retrofit;

import com.example.weiboapiservice.model.SplashData;
import com.example.weiboapiservice.model.WeiboGroups;
import com.example.weiboapiservice.model.WeiboStatusList;
import com.example.weiboapiservice.model.WeiboUser;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 青松 on 2016/8/4.
 */
public interface WeiboApi {

    /** 获取启动页背景图片*/
    @GET("1080*1776")
    Observable<SplashData> getSplashData();

    /** 获取某个用户信息*/
    @GET("users/show.json")
    Observable<WeiboUser> getUserInfo(@Query("uid") long uid);

    /** 获取好友分组列表*/
    @GET("friendships/groups.json")
    Observable<WeiboGroups> getFriendGroups();

    /** 获取用户及其关注用户得的微博*/
    @GET("statuses/friends_timeline.json")
    Observable<WeiboStatusList> getHomeStatusLists(@Query("page") int page, @Query("count") int count);
}
