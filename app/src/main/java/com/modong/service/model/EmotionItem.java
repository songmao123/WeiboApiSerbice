package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by 青松 on 2016/8/25.
 */
public class EmotionItem implements Parcelable{
    private String category;
    private List<Emotion> emotionList;

    public EmotionItem(){}

    protected EmotionItem(Parcel in) {
        category = in.readString();
        emotionList = in.createTypedArrayList(Emotion.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeTypedList(emotionList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EmotionItem> CREATOR = new Creator<EmotionItem>() {
        @Override
        public EmotionItem createFromParcel(Parcel in) {
            return new EmotionItem(in);
        }

        @Override
        public EmotionItem[] newArray(int size) {
            return new EmotionItem[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Emotion> getEmotionList() {
        return emotionList;
    }

    public void setEmotionList(List<Emotion> emotionList) {
        this.emotionList = emotionList;
    }
}
