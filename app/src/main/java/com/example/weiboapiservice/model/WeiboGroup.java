package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sqsong on 16-8-7.
 */
public class WeiboGroup implements Parcelable {

    private String id;
    private String idstr;
    private String name;
    private String mode;
    private Integer visible;
    private Integer like_count;
    private Integer member_count;
    private String description;
    private String profile_image_url;
    private WeiboUser user;
    private String created;

    protected WeiboGroup(Parcel in) {
        id = in.readString();
        idstr = in.readString();
        name = in.readString();
        mode = in.readString();
        description = in.readString();
        profile_image_url = in.readString();
        user = in.readParcelable(WeiboUser.class.getClassLoader());
        created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idstr);
        dest.writeString(name);
        dest.writeString(mode);
        dest.writeString(description);
        dest.writeString(profile_image_url);
        dest.writeParcelable(user, flags);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboGroup> CREATOR = new Creator<WeiboGroup>() {
        @Override
        public WeiboGroup createFromParcel(Parcel in) {
            return new WeiboGroup(in);
        }

        @Override
        public WeiboGroup[] newArray(int size) {
            return new WeiboGroup[size];
        }
    };
}
