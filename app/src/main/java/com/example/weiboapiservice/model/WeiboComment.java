package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

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
}
