package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by sqsong on 16-8-7.
 */
public class WeiboStatusList implements Parcelable {

    private List<WeiboStatus> statuses;
    private int total_number;


    protected WeiboStatusList(Parcel in) {
        statuses = in.createTypedArrayList(WeiboStatus.CREATOR);
        total_number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(statuses);
        dest.writeInt(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboStatusList> CREATOR = new Creator<WeiboStatusList>() {
        @Override
        public WeiboStatusList createFromParcel(Parcel in) {
            return new WeiboStatusList(in);
        }

        @Override
        public WeiboStatusList[] newArray(int size) {
            return new WeiboStatusList[size];
        }
    };

    public List<WeiboStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WeiboStatus> statuses) {
        this.statuses = statuses;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}
