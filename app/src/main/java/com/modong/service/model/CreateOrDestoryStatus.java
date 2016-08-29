package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/8/29.
 */
public class CreateOrDestoryStatus implements Parcelable {

    private WeiboStatus status;

    protected CreateOrDestoryStatus(Parcel in) {
        status = in.readParcelable(WeiboStatus.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(status, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreateOrDestoryStatus> CREATOR = new Creator<CreateOrDestoryStatus>() {
        @Override
        public CreateOrDestoryStatus createFromParcel(Parcel in) {
            return new CreateOrDestoryStatus(in);
        }

        @Override
        public CreateOrDestoryStatus[] newArray(int size) {
            return new CreateOrDestoryStatus[size];
        }
    };

    public WeiboStatus getStatus() {
        return status;
    }

    public void setStatus(WeiboStatus status) {
        this.status = status;
    }
}
