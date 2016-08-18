package com.modong.service.fragment.status.util;

import android.util.Patterns;

import com.modong.service.BuildConfig;

import java.util.regex.Pattern;

/**
 * Created by sqsong on 16-8-10.
 */
public class WeiboPattern {

    public static final Pattern WEB_URL = Pattern.compile(Patterns.WEB_URL.pattern(), Pattern.CASE_INSENSITIVE);
//            Pattern
//            .compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");
    public static final Pattern TOPIC_URL = Pattern
            .compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
    public static final Pattern MENTION_URL = Pattern
            .compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

    public static final String WEB_SCHEME = "http://";
    public static final String TOPIC_SCHEME = BuildConfig.APPLICATION_ID + ".topic://";
    public static final String MENTION_SCHEME = BuildConfig.APPLICATION_ID + ".mention://";

}
