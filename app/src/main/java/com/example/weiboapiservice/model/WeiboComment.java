package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.example.weiboapiservice.fragment.status.util.TimeLineUtil;

/**
 * Created by sqsong on 16-8-6.
 */
public class WeiboComment implements Parcelable {
    private long id;
    private String text;
    private String source;
    private WeiboUser user;
    private String mid;
    private String idstr;
    private String created_at;
    private WeiboStatus status;
    private WeiboComment reply_comment;
    private SpannableString spannableText;

    protected WeiboComment(Parcel in) {
        id = in.readLong();
        text = in.readString();
        source = in.readString();
        user = in.readParcelable(WeiboUser.class.getClassLoader());
        mid = in.readString();
        idstr = in.readString();
        created_at = in.readString();
        status = in.readParcelable(WeiboStatus.class.getClassLoader());
        reply_comment = in.readParcelable(WeiboComment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
        dest.writeString(source);
        dest.writeParcelable(user, flags);
        dest.writeString(mid);
        dest.writeString(idstr);
        dest.writeString(created_at);
        dest.writeParcelable(status, flags);
        dest.writeParcelable(reply_comment, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboComment> CREATOR = new Creator<WeiboComment>() {
        @Override
        public WeiboComment createFromParcel(Parcel in) {
            return new WeiboComment(in);
        }

        @Override
        public WeiboComment[] newArray(int size) {
            return new WeiboComment[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public WeiboUser getUser() {
        return user;
    }

    public void setUser(WeiboUser user) {
        this.user = user;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public WeiboStatus getStatus() {
        return status;
    }

    public void setStatus(WeiboStatus status) {
        this.status = status;
    }

    public WeiboComment getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(WeiboComment reply_comment) {
        this.reply_comment = reply_comment;
    }

    public SpannableString getSpannableText() {
        if (!TextUtils.isEmpty(spannableText)) {
            return spannableText;
        } else {
            return TimeLineUtil.convertNormalStringToSpannableString(text);
        }
    }

    public void setSpannableText(SpannableString spannableText) {
        this.spannableText = spannableText;
    }
}
