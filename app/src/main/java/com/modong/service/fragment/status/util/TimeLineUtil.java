package com.modong.service.fragment.status.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;

import com.modong.service.R;
import com.modong.service.model.WeiboUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sqsong on 16-8-11.
 */
public class TimeLineUtil {

    public static SpannableString convertNormalStringToSpannableString(String text) {
        SpannableString spannable = new SpannableString(text);
        Linkify.addLinks(spannable, WeiboPattern.EMOTION_URL, WeiboPattern.MENTION_SCHEME);
        Linkify.addLinks(spannable, WeiboPattern.WEB_URL, WeiboPattern.WEB_SCHEME);
        Linkify.addLinks(spannable, WeiboPattern.MENTION_URL, WeiboPattern.MENTION_SCHEME);
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

    public static String getPublishTime(String time) {
        try {
            StringBuffer buffer = new StringBuffer();
            Calendar createCal = Calendar.getInstance();
            if (time.length() == 13) {
                try {
                    createCal.setTimeInMillis(Long.parseLong(time));
                } catch (Exception e) {
                    createCal.setTimeInMillis(Date.parse(time));
                }
            } else {
                createCal.setTimeInMillis(Date.parse(time));
            }
            Calendar currentcal = Calendar.getInstance();
            currentcal.setTimeInMillis(System.currentTimeMillis());
            long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;
            if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) { // 同一月
                if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) { // 同一天
                    if (diffTime < 60) {
                        buffer.append("刚刚");
                    } else if (diffTime < 3600) {
                        buffer.append((diffTime / 60) + "分钟前");
                    } else {
                        buffer.append((diffTime / 3600) + "小时前");
                    }
//                    if (diffTime < 3600 && diffTime >= 60) {
//                        buffer.append((diffTime / 60) + "分钟前");
//                    } else if (diffTime < 60) {
//                        buffer.append("刚刚");
//                    } else {
//                        buffer.append("今天").append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
//                    }
                } else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) {// 前一天
                    buffer.append("昨天").append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                }
            }
            if (buffer.length() == 0) {
                buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
            }
            String timeStr = buffer.toString();
            if (currentcal.get(Calendar.YEAR) != createCal.get(Calendar.YEAR)) {
                timeStr = createCal.get(Calendar.YEAR) + "-" + timeStr;
            }
            return timeStr;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getFormatTime(String originTime) {
        String time = "";
        try {
            long parse = Date.parse(originTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(parse);
            int publishYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(Calendar.YEAR);
            String format = "MM-dd HH:mm";
            if (publishYear != currentYear) {
                format = "yyyy-MM-dd HH:mm";
            }
            time = formatDate(parse, format);
        } catch (Exception e) {
            time = originTime;
            e.printStackTrace();
        }
        return time;
    }

    public static String getCounter(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 100 * 1000) {
            return new DecimalFormat("#.0").format(count * 1.0f / 1000) + "k";
        } else {
            return new DecimalFormat("#").format(count * 1.0f / 1000) + "k";
        }
    }

    public static String formatDate(long time, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return (new SimpleDateFormat(format)).format(cal.getTime());
    }

    public static void setImageVerified(ImageView imgVerified, WeiboUser user) {
        // 2014-08-27 新增判断，VerifiedType存在为null的情况
        if (user == null) {
            imgVerified.setVisibility(View.GONE);
            return;
        }

        // 黄V
        if (user.getVerified_type() == 0) {
            imgVerified.setImageResource(R.drawable.avatar_vip);
        }
        // 200:初级达人 220:高级达人
        else if (user.getVerified_type() == 200 || user.getVerified_type() == 220) {
            imgVerified.setImageResource(R.drawable.avatar_grassroot);
        }
        // 蓝V
        else if (user.getVerified_type() > 0) {
            imgVerified.setImageResource(R.drawable.avatar_enterprise_vip);
        }
        if (user.getVerified_type() >= 0)
            imgVerified.setVisibility(View.VISIBLE);
        else
            imgVerified.setVisibility(View.GONE);
    }

    public static int getUserGenderResource(String genderString) {
        int resId;
        if ("m".equals(genderString)) {
            resId = R.drawable.ic_gender_male;
        } else if ("f".equals(genderString)) {
            resId = R.drawable.ic_gender_female;
        } else {
            resId = R.drawable.ic_gender_unknow;
        }
        return resId;
    }

}
