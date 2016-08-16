package com.example.weiboapiservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 青松 on 2016/8/16.
 */
public class RepostStatusList implements Parcelable {
    private List<WeiboStatus> reposts;
    private long previous_cursor;
    private long next_cursor;
    private long total_number;

    protected RepostStatusList(Parcel in) {
        reposts = in.createTypedArrayList(WeiboStatus.CREATOR);
        previous_cursor = in.readLong();
        next_cursor = in.readLong();
        total_number = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(reposts);
        dest.writeLong(previous_cursor);
        dest.writeLong(next_cursor);
        dest.writeLong(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RepostStatusList> CREATOR = new Creator<RepostStatusList>() {
        @Override
        public RepostStatusList createFromParcel(Parcel in) {
            return new RepostStatusList(in);
        }

        @Override
        public RepostStatusList[] newArray(int size) {
            return new RepostStatusList[size];
        }
    };

    public List<WeiboStatus> getReposts() {
        return reposts;
    }

    public void setReposts(List<WeiboStatus> reposts) {
        this.reposts = reposts;
    }

    public long getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(long previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public long getTotal_number() {
        return total_number;
    }

    public void setTotal_number(long total_number) {
        this.total_number = total_number;
    }
}
