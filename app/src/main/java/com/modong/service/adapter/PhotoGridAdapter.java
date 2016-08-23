package com.modong.service.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.model.PhotoItem;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.SmoothCheckBox;

import java.util.List;

/**
 * Created by 青松 on 2016/8/23.
 */
public class PhotoGridAdapter extends BaseQuickAdapter<PhotoItem> {

    private int imageHeight;

    public PhotoGridAdapter(int layoutResId, List<PhotoItem> data) {
        super(layoutResId, data);
        this.imageHeight = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(3) * 2) / 3;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PhotoItem photoItem) {
        ImageView photo_iv = helper.getView(R.id.photo_iv);
        final View mask_view = helper.getView(R.id.mask_view);
        SmoothCheckBox photo_cb = helper.getView(R.id.photo_cb);
        int position = helper.getAdapterPosition();
        String filePath = photoItem.getFilePath();

        View rootView = helper.getConvertView();
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.height = imageHeight;

        ViewGroup.LayoutParams params = photo_iv.getLayoutParams();
        if (position == 0) {
            photo_cb.setVisibility(View.GONE);
            int resId = Integer.valueOf(filePath);
            params.width = DensityUtil.dip2px(50);
            params.height = DensityUtil.dip2px(50);
            photo_iv.setImageResource(resId);
        } else {
            photo_cb.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(filePath).centerCrop().into(photo_iv);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            photo_cb.setOnCheckedChangeListener(null);
            photo_cb.setChecked(photoItem.isChecked(), false);

            photo_cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    photoItem.setChecked(isChecked);
                    if (isChecked) {
                        mask_view.setVisibility(View.VISIBLE);
                    } else {
                        mask_view.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
