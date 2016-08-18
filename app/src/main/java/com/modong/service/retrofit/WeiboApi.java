package com.modong.service.retrofit;

import com.modong.service.model.RepostStatusList;
import com.modong.service.model.SplashData;
import com.modong.service.model.WeiboCommentList;
import com.modong.service.model.WeiboGroups;
import com.modong.service.model.WeiboStatus;
import com.modong.service.model.WeiboStatusList;
import com.modong.service.model.WeiboUser;

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

    /** 通过Uid获取某个用户信息*/
    @GET("users/show.json")
    Observable<WeiboUser> getUserInfoByUid(@Query("uid") long uid);

    /** 通过screen_name获取某个用户信息*/
    @GET("users/show.json")
    Observable<WeiboUser> getUserInfoByScreenName(@Query("screen_name") String screenName);

    /** 获取好友分组列表*/
    @GET("friendships/groups.json")
    Observable<WeiboGroups> getFriendGroups();

    /** 获取用户及其关注用户得的微博*/
    @GET("statuses/friends_timeline.json")
    Observable<WeiboStatusList> getHomeStatusLists(@Query("page") int page, @Query("count") int count);

    /** 获取某个用户发布的微博*/
    @GET("statuses/home_timeline.json")
    Observable<WeiboStatusList> getUserStatusLists(@Query("screen_name") String screenName, @Query("page") int page, @Query("count") int count);

    /** 获取单条微博信息*/
    @GET("statuses/show.json")
    Observable<WeiboStatus> getSingleWeiboStatus(@Query("id") long statusId);

    /** 获取某条微博被转发的微博列表*/
    @GET("statuses/repost_timeline.json")
    Observable<RepostStatusList> getRepostList(@Query("id") long statusId, @Query("source") String source, @Query("access_token") String access_token, @Query("page") int pageIndex);

    /** 获取某条微博的评论列表*/
    @GET("comments/show.json ")
    Observable<WeiboCommentList> getCommentList(@Query("id") long statusId, @Query("page") int pageIndex);
}
