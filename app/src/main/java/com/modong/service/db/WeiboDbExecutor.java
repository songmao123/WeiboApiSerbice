package com.modong.service.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.modong.service.BaseApplication;
import com.modong.service.model.AccessToken;
import com.modong.service.model.WeiboUser;

/**
 * Created by 青松 on 2016/8/15.
 */
public class WeiboDbExecutor {

    private final Context mContext;
    private WeiboDbHelper mHelper;
    private static WeiboDbExecutor instance;

    private WeiboDbExecutor() {
        mContext = BaseApplication.getInstance();
        mHelper = new WeiboDbHelper(mContext);
    }

    public static WeiboDbExecutor getInstance() {
        if (instance == null) {
            synchronized (WeiboDbExecutor.class) {
                if (instance == null) {
                    instance = new WeiboDbExecutor();
                }
            }
        }
        return instance;
    }

    public synchronized void insertTokenInfo(AccessToken token, boolean isUpdate) {
        long startTime = System.currentTimeMillis();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(WeiboDbConstants.COLUMN_UID, token.getUid());
            values.put(WeiboDbConstants.COLUMN_ACCESS_TOKEN, token.getAccess_token());
            values.put(WeiboDbConstants.COLUMN_EXPIRES_IN, token.getExpires_in());
            values.put(WeiboDbConstants.COLUMN_REFRESH_TOKEN, token.getRefresh_token());
            if (isUpdate) {
                database.update(WeiboDbConstants.TABLE_ACCESS_TOKEN, values, WeiboDbConstants.COLUMN_UID + " = ?", new String[]{String.valueOf(token.getUid())});
            } else {
                database.insert(WeiboDbConstants.TABLE_ACCESS_TOKEN, null, values);
            }
            Log.i("sqsong", "save token to database take time: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public synchronized AccessToken getTokenInfoFromDB() {
        long startTime = System.currentTimeMillis();
        AccessToken token = null;
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(WeiboDbConstants.TABLE_ACCESS_TOKEN, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                token = new AccessToken();
                token.setUid(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_UID)));
                token.setAccess_token(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_ACCESS_TOKEN)));
                token.setExpires_in(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_EXPIRES_IN)));
                token.setRefresh_token(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_REFRESH_TOKEN)));
            }
            Log.i("sqsong", "get token from database take time: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return token;
    }

    public synchronized void insertLoginUser(WeiboUser user) {
        if (user == null) {
            Log.e("sqsong", "insert user is null...");
            return;
        }
        long startTime = System.currentTimeMillis();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(WeiboDbConstants.COLUMN_USER_ID, user.getId());
            values.put(WeiboDbConstants.COLUMN_USER_IDSTR, user.getIdstr());
            values.put(WeiboDbConstants.COLUMN_USER_SCREEN_NAME, user.getScreen_name());
            values.put(WeiboDbConstants.COLUMN_USER_NAME, user.getName());
            values.put(WeiboDbConstants.COLUMN_USER_PROVINCE, user.getProvince());
            values.put(WeiboDbConstants.COLUMN_USER_CITY, user.getCity());
            values.put(WeiboDbConstants.COLUMN_USER_LOCATION, user.getLocation());
            values.put(WeiboDbConstants.COLUMN_USER_DESCRIPTION, user.getDescription());
            values.put(WeiboDbConstants.COLUMN_USER_URL, user.getUrl());
            values.put(WeiboDbConstants.COLUMN_USER_PROFILE_IMAGE_URL, user.getProfile_image_url());
            values.put(WeiboDbConstants.COLUMN_USER_COVER_IMAGE_PHONE, user.getCover_image_phone());
            values.put(WeiboDbConstants.COLUMN_USER_PROFILE_URL, user.getProfile_url());
            values.put(WeiboDbConstants.COLUMN_USER_DOMAIN, user.getDomain());
            values.put(WeiboDbConstants.COLUMN_USER_WEIHAO, user.getWeihao());
            values.put(WeiboDbConstants.COLUMN_USER_GENDER, user.getGender());
            values.put(WeiboDbConstants.COLUMN_USER_FOLLOWERS_COUNT, user.getFollowers_count());
            values.put(WeiboDbConstants.COLUMN_USER_FRIENDS_COUNT, user.getFriends_count());
            values.put(WeiboDbConstants.COLUMN_USER_STATUSES_COUNT, user.getStatuses_count());
            values.put(WeiboDbConstants.COLUMN_USER_FAVOURITES_COUNT, user.getFavourites_count());
            values.put(WeiboDbConstants.COLUMN_USER_CREATED_AT, user.getCreated_at());
            values.put(WeiboDbConstants.COLUMN_USER_FOLLOWING, user.isFollowing() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_ALLOW_ALL_ACT_MSG, user.isAllow_all_act_msg() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_GEO_ENABLED, user.isGeo_enabled() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_VERIFIED, user.isVerified() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_VERIFIED_TYPE, user.getVerified_type());
            values.put(WeiboDbConstants.COLUMN_USER_REMARK, user.getRemark());
            values.put(WeiboDbConstants.COLUMN_USER_STATUS_ID, user.getStatus().getId());
            values.put(WeiboDbConstants.COLUMN_USER_ALLOW_ALL_COMMENT, user.isAllow_all_comment() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_AVATAR_LARGE, user.getAvatar_large());
            values.put(WeiboDbConstants.COLUMN_USER_AVATAR_HD, user.getAvatar_hd());
            values.put(WeiboDbConstants.COLUMN_USER_VERIFIED_REASON, user.getVerified_reason());
            values.put(WeiboDbConstants.COLUMN_USER_FOLLOW_ME, user.isFollow_me() ? 1 : 0);
            values.put(WeiboDbConstants.COLUMN_USER_ONLINE_STATUS, user.getOnline_status());
            values.put(WeiboDbConstants.COLUMN_USER_BI_FOLLOWERS_COUNT, user.getBi_followers_count());
            values.put(WeiboDbConstants.COLUMN_USER_LANG, user.getLang());
            database.insert(WeiboDbConstants.TABLE_WEIBO_USER, null, values);
            Log.i("sqsong", "save user info to database take time: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public synchronized WeiboUser getWeiboUserFromDB(long userId) {
        long startTime = System.currentTimeMillis();
        WeiboUser user = null;
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(WeiboDbConstants.TABLE_WEIBO_USER, null, WeiboDbConstants.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                user = new WeiboUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_ID)));
                user.setIdstr(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_IDSTR)));
                user.setScreen_name(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_SCREEN_NAME)));
                user.setName(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_NAME)));
                user.setProvince(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_PROVINCE)));
                user.setCity(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_CITY)));
                user.setLocation(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_LOCATION)));
                user.setDescription(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_DESCRIPTION)));
                user.setUrl(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_URL)));
                user.setProfile_image_url(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_PROFILE_IMAGE_URL)));
                user.setCover_image_phone(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_COVER_IMAGE_PHONE)));
                user.setProfile_url(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_PROFILE_URL)));
                user.setDomain(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_DOMAIN)));
                user.setWeihao(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_WEIHAO)));
                user.setGender(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_GENDER)));
                user.setFollowers_count(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_FOLLOWERS_COUNT)));
                user.setFriends_count(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_FRIENDS_COUNT)));
                user.setStatuses_count(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_STATUSES_COUNT)));
                user.setFavourites_count(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_FAVOURITES_COUNT)));
                user.setCreated_at(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_CREATED_AT)));
                user.setFollowing(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_FOLLOWING)) == 1 ? true : false);
                user.setAllow_all_act_msg(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_ALLOW_ALL_ACT_MSG)) == 1 ? true : false);
                user.setGeo_enabled(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_GEO_ENABLED)) == 1 ? true : false);
                user.setVerified(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_VERIFIED)) == 1 ? true : false);
                user.setVerified_type(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_VERIFIED_TYPE)));
                user.setRemark(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_REMARK)));
                user.setStatus_id(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_STATUS_ID)));
                user.setAllow_all_comment(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_ALLOW_ALL_COMMENT)) == 1 ? true : false);
                user.setAvatar_large(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_AVATAR_LARGE)));
                user.setAvatar_hd(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_AVATAR_HD)));
                user.setVerified_reason(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_VERIFIED_REASON)));
                user.setFollow_me(cursor.getLong(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_FOLLOW_ME)) == 1 ? true : false);
                user.setOnline_status(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_ONLINE_STATUS)));
                user.setBi_followers_count(cursor.getInt(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_BI_FOLLOWERS_COUNT)));
                user.setLang(cursor.getString(cursor.getColumnIndex(WeiboDbConstants.COLUMN_USER_LANG)));
                Log.i("sqsong", "get user info from database take time: " + (System.currentTimeMillis() - startTime) + "ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            database.close();
        }
        return user;
    }
}
