package com.modong.service.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.modong.service.fragment.status.util.TimeLineUtil;

import java.util.List;

/**
 * Created by sqsong on 16-8-6.
 */
public class WeiboStatus implements Parcelable {

    private long id;
    private long mid;
    private String idstr;
    private String text;
    private String source;
    private boolean favorited;
    private boolean truncated;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;
    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;
    private List<WeiboPicture> pic_urls;
    private WeiboGeo geo;
    private WeiboUser user;
    private WeiboStatus retweeted_status;
    private int reposts_count;
    private int comments_count;
    private int attitudes_count;
    private int mlevel;
    private String created_at;
    private SpannableString spannableText;


    protected WeiboStatus(Parcel in) {
        id = in.readLong();
        mid = in.readLong();
        idstr = in.readString();
        text = in.readString();
        source = in.readString();
        favorited = in.readByte() != 0;
        truncated = in.readByte() != 0;
        in_reply_to_status_id = in.readString();
        in_reply_to_user_id = in.readString();
        in_reply_to_screen_name = in.readString();
        thumbnail_pic = in.readString();
        bmiddle_pic = in.readString();
        original_pic = in.readString();
        pic_urls = in.createTypedArrayList(WeiboPicture.CREATOR);
        geo = in.readParcelable(WeiboGeo.class.getClassLoader());
        user = in.readParcelable(WeiboUser.class.getClassLoader());
        retweeted_status = in.readParcelable(WeiboStatus.class.getClassLoader());
        reposts_count = in.readInt();
        comments_count = in.readInt();
        attitudes_count = in.readInt();
        mlevel = in.readInt();
        created_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(mid);
        dest.writeString(idstr);
        dest.writeString(text);
        dest.writeString(source);
        dest.writeByte((byte) (favorited ? 1 : 0));
        dest.writeByte((byte) (truncated ? 1 : 0));
        dest.writeString(in_reply_to_status_id);
        dest.writeString(in_reply_to_user_id);
        dest.writeString(in_reply_to_screen_name);
        dest.writeString(thumbnail_pic);
        dest.writeString(bmiddle_pic);
        dest.writeString(original_pic);
        dest.writeTypedList(pic_urls);
        dest.writeParcelable(geo, flags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(retweeted_status, flags);
        dest.writeInt(reposts_count);
        dest.writeInt(comments_count);
        dest.writeInt(attitudes_count);
        dest.writeInt(mlevel);
        dest.writeString(created_at);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WeiboStatus> CREATOR = new Creator<WeiboStatus>() {
        @Override
        public WeiboStatus createFromParcel(Parcel in) {
            return new WeiboStatus(in);
        }

        @Override
        public WeiboStatus[] newArray(int size) {
            return new WeiboStatus[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public List<WeiboPicture> getPic_urls() {
        return pic_urls;
    }

    public void setPic_urls(List<WeiboPicture> pic_urls) {
        this.pic_urls = pic_urls;
    }

    public WeiboGeo getGeo() {
        return geo;
    }

    public void setGeo(WeiboGeo geo) {
        this.geo = geo;
    }

    public WeiboUser getUser() {
        return user;
    }

    public void setUser(WeiboUser user) {
        this.user = user;
    }

    public WeiboStatus getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(WeiboStatus retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public int getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(int reposts_count) {
        this.reposts_count = reposts_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(int attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public int getMlevel() {
        return mlevel;
    }

    public void setMlevel(int mlevel) {
        this.mlevel = mlevel;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public SpannableString getSpannableText() {
        if (!TextUtils.isEmpty(spannableText)) {
            return spannableText;
        } else {
            return TimeLineUtil.convertNormalStringToSpannableString(text);
        }
    }

    public void setSpannableText(SpannableString spannableText) {
        this.spannableText = spannableText;
    }
}
