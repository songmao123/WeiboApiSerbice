package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by 青松 on 2016/8/23.
 */
public class PhotoFolderItem implements Parcelable {

    private String folderName;
    private boolean isChecked;
    private ArrayList<PhotoItem> photos;

    public PhotoFolderItem(){}

    protected PhotoFolderItem(Parcel in) {
        folderName = in.readString();
        isChecked = in.readByte() != 0;
        photos = in.createTypedArrayList(PhotoItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeTypedList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoFolderItem> CREATOR = new Creator<PhotoFolderItem>() {
        @Override
        public PhotoFolderItem createFromParcel(Parcel in) {
            return new PhotoFolderItem(in);
        }

        @Override
        public PhotoFolderItem[] newArray(int size) {
            return new PhotoFolderItem[size];
        }
    };

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PhotoItem> photos) {
        this.photos = photos;
    }
}
