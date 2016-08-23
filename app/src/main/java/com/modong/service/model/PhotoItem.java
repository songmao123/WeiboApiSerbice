package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2016/8/23.
 */
public class PhotoItem implements Parcelable {
    private String filePath;
    private boolean isChecked;

    public PhotoItem(String filePath) {
        this.filePath = filePath;
    }

    protected PhotoItem(Parcel in) {
        filePath = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel in) {
            return new PhotoItem(in);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
