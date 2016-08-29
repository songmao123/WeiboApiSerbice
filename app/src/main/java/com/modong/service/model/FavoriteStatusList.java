package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 青松 on 2016/8/29.
 */
public class FavoriteStatusList implements Parcelable {

    private List<FavoriteStatus> favorites;
    private int total_number;

    public FavoriteStatusList() {
    }

    protected FavoriteStatusList(Parcel in) {
        favorites = in.createTypedArrayList(FavoriteStatus.CREATOR);
        total_number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(favorites);
        dest.writeInt(total_number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavoriteStatusList> CREATOR = new Creator<FavoriteStatusList>() {
        @Override
        public FavoriteStatusList createFromParcel(Parcel in) {
            return new FavoriteStatusList(in);
        }

        @Override
        public FavoriteStatusList[] newArray(int size) {
            return new FavoriteStatusList[size];
        }
    };

    public List<FavoriteStatus> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteStatus> favorites) {
        this.favorites = favorites;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public static class FavoriteStatus implements Parcelable {
        private WeiboStatus status;
        private String favorited_time;

        public FavoriteStatus() {
        }

        protected FavoriteStatus(Parcel in) {
            status = in.readParcelable(WeiboStatus.class.getClassLoader());
            favorited_time = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(status, flags);
            dest.writeString(favorited_time);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<FavoriteStatus> CREATOR = new Creator<FavoriteStatus>() {
            @Override
            public FavoriteStatus createFromParcel(Parcel in) {
                return new FavoriteStatus(in);
            }

            @Override
            public FavoriteStatus[] newArray(int size) {
                return new FavoriteStatus[size];
            }
        };

        public WeiboStatus getStatus() {
            return status;
        }

        public void setStatus(WeiboStatus status) {
            this.status = status;
        }

        public String getFavorited_time() {
            return favorited_time;
        }

        public void setFavorited_time(String favorited_time) {
            this.favorited_time = favorited_time;
        }
    }

}
