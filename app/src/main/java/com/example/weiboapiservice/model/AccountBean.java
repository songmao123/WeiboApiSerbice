package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by sqsong on 16-8-6.
 */
public class AccountBean extends RealmObject implements Parcelable {

    private AccessToken accessToken;
    private WeiboUser user;
    @Ignore
    private WeiboGroups groups;

    public AccountBean() {}

    protected AccountBean(Parcel in) {
        accessToken = in.readParcelable(AccessToken.class.getClassLoader());
        user = in.readParcelable(WeiboUser.class.getClassLoader());
        groups = in.readParcelable(WeiboGroups.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(accessToken, flags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(groups, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AccountBean> CREATOR = new Creator<AccountBean>() {
        @Override
        public AccountBean createFromParcel(Parcel in) {
            return new AccountBean(in);
        }

        @Override
        public AccountBean[] newArray(int size) {
            return new AccountBean[size];
        }
    };

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public WeiboUser getUser() {
        return user;
    }

    public void setUser(WeiboUser user) {
        this.user = user;
    }

    public WeiboGroups getGroups() {
        return groups;
    }

    public void setGroups(WeiboGroups groups) {
        this.groups = groups;
    }
}
