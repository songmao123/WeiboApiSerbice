package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 青松 on 2016/8/16.
 */
public class WeiboCommentList implements Parcelable {
    private List<WeiboComment> comments;
    private long previous_cursor;
    private long next_cursor;
    private long total_number;

    protected WeiboCommentList(Parcel in) {
        comments = in.createTypedArrayList(WeiboComment.CREATOR);
        previous_cursor = in.readLong();
        next_cursor = in.readLong();
        total_number = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(comments);
        dest.writeLong(previous_cursor);
        dest.writeLong(next_cursor);
        dest.writeLong(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboCommentList> CREATOR = new Creator<WeiboCommentList>() {
        @Override
        public WeiboCommentList createFromParcel(Parcel in) {
            return new WeiboCommentList(in);
        }

        @Override
        public WeiboCommentList[] newArray(int size) {
            return new WeiboCommentList[size];
        }
    };

    public List<WeiboComment> getComments() {
        return comments;
    }

    public void setComments(List<WeiboComment> comments) {
        this.comments = comments;
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
