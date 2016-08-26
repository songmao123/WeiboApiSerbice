package com.modong.service.db;

/**
 * Created by 青松 on 2016/8/15.
 */
public class WeiboDbConstants {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "weibo.db";
    public static final String TABLE_ACCESS_TOKEN = "access_token";
    public static final String TABLE_WEIBO_USER = "weibo_user";

    //Token Constant
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_ACCESS_TOKEN = "access_token";
    public static final String COLUMN_EXPIRES_IN = "expires_in";
    public static final String COLUMN_REFRESH_TOKEN = "refresh_token";

    public static final String SQL_CREATE_TOKEN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCESS_TOKEN
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_UID + " LONG, "
            + COLUMN_ACCESS_TOKEN + " TEXT, " + COLUMN_EXPIRES_IN + " LONG, "
            + COLUMN_REFRESH_TOKEN + " TEXT)";

    //WeiboUser Constan
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_IDSTR = "idstr";
    public static final String COLUMN_USER_SCREEN_NAME = "screen_name";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PROVINCE = "province";
    public static final String COLUMN_USER_CITY = "city";
    public static final String COLUMN_USER_LOCATION = "location";
    public static final String COLUMN_USER_DESCRIPTION = "description";
    public static final String COLUMN_USER_URL = "url";
    public static final String COLUMN_USER_PROFILE_IMAGE_URL = "profile_image_url";
    public static final String COLUMN_USER_COVER_IMAGE_PHONE = "cover_image_phone";
    public static final String COLUMN_USER_PROFILE_URL = "profile_url";
    public static final String COLUMN_USER_DOMAIN = "domain";
    public static final String COLUMN_USER_WEIHAO = "weihao";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_FOLLOWERS_COUNT = "followers_count";
    public static final String COLUMN_USER_FRIENDS_COUNT = "friends_count";
    public static final String COLUMN_USER_STATUSES_COUNT = "statuses_count";
    public static final String COLUMN_USER_FAVOURITES_COUNT = "favourites_count";
    public static final String COLUMN_USER_CREATED_AT = "created_at";
    public static final String COLUMN_USER_FOLLOWING = "following";
    public static final String COLUMN_USER_ALLOW_ALL_ACT_MSG = "allow_all_act_msg";
    public static final String COLUMN_USER_GEO_ENABLED = "geo_enabled";
    public static final String COLUMN_USER_VERIFIED = "verified";
    public static final String COLUMN_USER_VERIFIED_TYPE = "verified_type";
    public static final String COLUMN_USER_REMARK = "remark";
    public static final String COLUMN_USER_STATUS_ID = "status_id";
    public static final String COLUMN_USER_ALLOW_ALL_COMMENT = "allow_all_comment";
    public static final String COLUMN_USER_AVATAR_LARGE = "avatar_large";
    public static final String COLUMN_USER_AVATAR_HD = "avatar_hd";
    public static final String COLUMN_USER_VERIFIED_REASON= "verified_reason";
    public static final String COLUMN_USER_FOLLOW_ME = "follow_me";
    public static final String COLUMN_USER_ONLINE_STATUS = "online_status";
    public static final String COLUMN_USER_BI_FOLLOWERS_COUNT = "bi_followers_count";
    public static final String COLUMN_USER_LANG = "lang";

    public static final String SQL_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_WEIBO_USER
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_ID + " LONG, "
            + COLUMN_USER_IDSTR + " TEXT, " + COLUMN_USER_SCREEN_NAME + " TEXT, "
            + COLUMN_USER_NAME + " TEXT, " + COLUMN_USER_PROVINCE + " INTEGER, "
            + COLUMN_USER_CITY + " INTEGER, " + COLUMN_USER_LOCATION + " TEXT, "
            + COLUMN_USER_DESCRIPTION + " TEXT, "+ COLUMN_USER_URL + " TEXT, "
            + COLUMN_USER_PROFILE_IMAGE_URL + " TEXT, "+ COLUMN_USER_COVER_IMAGE_PHONE + " TEXT, "
            + COLUMN_USER_PROFILE_URL + " TEXT, "+ COLUMN_USER_DOMAIN + " TEXT, "
            + COLUMN_USER_WEIHAO + " TEXT, "+ COLUMN_USER_GENDER + " TEXT, "
            + COLUMN_USER_FOLLOWERS_COUNT + " INTEGER, "+ COLUMN_USER_FRIENDS_COUNT + " INTEGER, "
            + COLUMN_USER_STATUSES_COUNT + " INTEGER, "+ COLUMN_USER_FAVOURITES_COUNT + " INTEGER, "
            + COLUMN_USER_CREATED_AT + " TEXT, "+ COLUMN_USER_FOLLOWING + " BOOLEAN, "
            + COLUMN_USER_ALLOW_ALL_ACT_MSG + " BOOLEAN, "+ COLUMN_USER_GEO_ENABLED + " BOOLEAN, "
            + COLUMN_USER_VERIFIED + " BOOLEAN, "+ COLUMN_USER_VERIFIED_TYPE + " INTEGER, "
            + COLUMN_USER_STATUS_ID + " LONG, "+ COLUMN_USER_REMARK + " TEXT, "
            + COLUMN_USER_ALLOW_ALL_COMMENT + " BOOLEAN, "+ COLUMN_USER_AVATAR_LARGE + " TEXT, "
            + COLUMN_USER_AVATAR_HD + " TEXT, "+ COLUMN_USER_VERIFIED_REASON + " TEXT, "
            + COLUMN_USER_FOLLOW_ME + " BOOLEAN, "+ COLUMN_USER_ONLINE_STATUS + " INTEGER, "
            + COLUMN_USER_BI_FOLLOWERS_COUNT + " INTEGER, "+ COLUMN_USER_LANG + " TEXT)";


    //Emoji
    public static final String COLUMN_PHRASE = "phrase";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_HOT = "hot";
    public static final String COLUMN_COMMON = "common";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_PICID = "picid";
    public static final String COLUMN_PICBYTES = "pic_bytes";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + "emoji.db"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PHRASE + " TEXT, "
            + COLUMN_TYPE + " TEXT, "
            + COLUMN_URL + " TEXT, "
            + COLUMN_PICBYTES + " BLOB, "
            + COLUMN_HOT + " INTEGER, "
            + COLUMN_COMMON + " INTEGER, "
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_ICON + " TEXT, "
            + COLUMN_VALUE + " TEXT, "
            + COLUMN_PICID + " TEXT)";

    //Emotions
    public static final String TABLE_NAME_EMOTION = "emotion";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_NAME = "name";
}
