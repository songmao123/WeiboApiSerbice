package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by sqsong on 16-8-7.
 */
public class WeiboGroups implements Parcelable {

    private List<WeiboGroup> lists;
    private int total_number;

    protected WeiboGroups(Parcel in) {
        lists = in.createTypedArrayList(WeiboGroup.CREATOR);
        total_number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(lists);
        dest.writeInt(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboGroups> CREATOR = new Creator<WeiboGroups>() {
        @Override
        public WeiboGroups createFromParcel(Parcel in) {
            return new WeiboGroups(in);
        }

        @Override
        public WeiboGroups[] newArray(int size) {
            return new WeiboGroups[size];
        }
    };
}
