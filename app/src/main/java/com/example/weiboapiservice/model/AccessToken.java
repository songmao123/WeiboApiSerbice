package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by sqsong on 16-8-7.
 */
public class AccessToken implements Parcelable {

    private long uid;
    private String access_token;
    private long expires_in;
    private String refresh_token;
    private String validDate;

    public AccessToken() {}

    protected AccessToken(Parcel in) {
        uid = in.readLong();
        access_token = in.readString();
        expires_in = in.readLong();
        refresh_token = in.readString();
        validDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(access_token);
        dest.writeLong(expires_in);
        dest.writeString(refresh_token);
        dest.writeString(validDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AccessToken> CREATOR = new Creator<AccessToken>() {
        @Override
        public AccessToken createFromParcel(Parcel in) {
            return new AccessToken(in);
        }

        @Override
        public AccessToken[] newArray(int size) {
            return new AccessToken[size];
        }
    };

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }
}
