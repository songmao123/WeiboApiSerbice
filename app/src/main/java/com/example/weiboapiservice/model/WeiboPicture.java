package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/8/8.
 */
public class WeiboPicture implements Parcelable {

    private String thumbnail_pic;

    protected WeiboPicture(Parcel in) {
        thumbnail_pic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail_pic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboPicture> CREATOR = new Creator<WeiboPicture>() {
        @Override
        public WeiboPicture createFromParcel(Parcel in) {
            return new WeiboPicture(in);
        }

        @Override
        public WeiboPicture[] newArray(int size) {
            return new WeiboPicture[size];
        }
    };

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }
}
