package com.modong.service.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.model.PhotoItem;
import com.modong.service.view.SmoothCheckBox;

import java.util.List;

/**
 * Created by 青松 on 2016/8/23.
 */
public class PhotoGridAdapter extends BaseQuickAdapter<PhotoItem> {

    public PhotoGridAdapter(int layoutResId, List<PhotoItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final PhotoItem photoItem) {
        ImageView photo_iv = helper.getView(R.id.photo_iv);
        SmoothCheckBox photo_cb = helper.getView(R.id.photo_cb);
        int position = helper.getAdapterPosition();
        String filePath = photoItem.getFilePath();
        if (position == 0) {
            photo_cb.setVisibility(View.GONE);
            int resId = Integer.valueOf(filePath);
            Glide.with(mContext).load(resId).into(photo_iv);
        } else {
            photo_cb.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(filePath).into(photo_iv);
            photo_cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    photoItem.setChecked(isChecked);
                }
            });
        }
    }
}
