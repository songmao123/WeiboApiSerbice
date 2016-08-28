package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by sqsong on 16-8-27.
 */
public class WeiboUserList implements Parcelable {

    private List<WeiboUser> users;
    private long previous_cursor;
    private long next_cursor;
    private long total_number;

    public WeiboUserList() {}

    protected WeiboUserList(Parcel in) {
        users = in.createTypedArrayList(WeiboUser.CREATOR);
        previous_cursor = in.readLong();
        next_cursor = in.readLong();
        total_number = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(users);
        dest.writeLong(previous_cursor);
        dest.writeLong(next_cursor);
        dest.writeLong(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboUserList> CREATOR = new Creator<WeiboUserList>() {
        @Override
        public WeiboUserList createFromParcel(Parcel in) {
            return new WeiboUserList(in);
        }

        @Override
        public WeiboUserList[] newArray(int size) {
            return new WeiboUserList[size];
        }
    };

    public List<WeiboUser> getUsers() {
        return users;
    }

    public void setUsers(List<WeiboUser> users) {
        this.users = users;
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
