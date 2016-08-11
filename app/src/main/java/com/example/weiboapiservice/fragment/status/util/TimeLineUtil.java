package com.example.weiboapiservice.fragment.status.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;

/**
 * Created by sqsong on 16-8-11.
 */
public class TimeLineUtil {

    public static SpannableString convertNormalStringToSpannableString(String text) {
        SpannableString spannable = new SpannableString(text);
        Linkify.addLinks(spannable, WeiboPattern.EMOTION_URL, WeiboPattern.MENTION_SCHEME);
        Linkify.addLinks(spannable, WeiboPattern.WEB_URL, WeiboPattern.WEB_SCHEME);
        Linkify.addLinks(spannable, WeiboPattern.TOPIC_URL, WeiboPattern.TOPIC_SCHEME);
        URLSpan[] urlSpans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        WeiboURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            weiboSpan = new WeiboURLSpan(urlSpan.getURL());
            int start = spannable.getSpanStart(urlSpan);
            int end = spannable.getSpanEnd(urlSpan);
            spannable.removeSpan(urlSpan);
            spannable.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

}
