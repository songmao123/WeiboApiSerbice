package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sqsong on 16-8-6.
 */
public class WeiboGeo implements Parcelable {

    private String longitude;
    private String latitude;
    private String city;
    private String province;
    private String city_name;
    private String province_name;
    private String address;
    private String pinyin;
    private String more;

    protected WeiboGeo(Parcel in) {
        longitude = in.readString();
        latitude = in.readString();
        city = in.readString();
        province = in.readString();
        city_name = in.readString();
        province_name = in.readString();
        address = in.readString();
        pinyin = in.readString();
        more = in.readString();
    }

    public static final Creator<WeiboGeo> CREATOR = new Creator<WeiboGeo>() {
        @Override
        public WeiboGeo createFromParcel(Parcel in) {
            return new WeiboGeo(in);
        }

        @Override
        public WeiboGeo[] newArray(int size) {
            return new WeiboGeo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(longitude);
        parcel.writeString(latitude);
        parcel.writeString(city);
        parcel.writeString(province);
        parcel.writeString(city_name);
        parcel.writeString(province_name);
        parcel.writeString(address);
        parcel.writeString(pinyin);
        parcel.writeString(more);
    }
}
