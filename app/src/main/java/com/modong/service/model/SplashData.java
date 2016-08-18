package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/8/4.
 */
public class SplashData implements Parcelable {
    private String text;
    private String img;

    protected SplashData(Parcel in) {
        text = in.readString();
        img = in.readString();
    }

    public static final Creator<SplashData> CREATOR = new Creator<SplashData>() {
        @Override
        public SplashData createFromParcel(Parcel in) {
            return new SplashData(in);
        }

        @Override
        public SplashData[] newArray(int size) {
            return new SplashData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(img);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
