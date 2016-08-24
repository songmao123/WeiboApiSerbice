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

import java.util.List;

/**
 * Created by 青松 on 2016/8/23.
 */
public class PhotoGridAdapter extends BaseQuickAdapter<PhotoItem> {

    private int imageHeight;
    private int maxSelectedCount;
    private List<PhotoItem> selectedItem;

    public PhotoGridAdapter(int layoutResId, List<PhotoItem> data, List<PhotoItem> selectItem, int maxSelectCount) {
        super(layoutResId, data);
        this.imageHeight = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(3) * 2) / 3;
        this.selectedItem = selectItem;
        this.maxSelectedCount = maxSelectCount;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PhotoItem photoItem) {
        ImageView photo_iv = helper.getView(R.id.photo_iv);
        final ImageView photo_check_iv = helper.getView(R.id.photo_check_iv);
        final View mask_view = helper.getView(R.id.mask_view);
        int position = helper.getAdapterPosition();
        String filePath = photoItem.getFilePath();

        View rootView = helper.getConvertView();
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.height = imageHeight;

        ViewGroup.LayoutParams params = photo_iv.getLayoutParams();
        if (position == 0) {
            photo_check_iv.setVisibility(View.GONE);
            int resId = Integer.valueOf(filePath);
            params.width = DensityUtil.dip2px(50);
            params.height = DensityUtil.dip2px(50);
            photo_iv.setImageResource(resId);
        } else {
            photo_check_iv.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(filePath).centerCrop().into(photo_iv);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (photoItem.isChecked()) {
                photo_check_iv.setImageResource(R.drawable.compose_photo_preview_right);
                mask_view.setVisibility(View.VISIBLE);
            } else {
                photo_check_iv.setImageResource(R.drawable.compose_photo_preview_default);
                mask_view.setVisibility(View.GONE);
            }

            photo_check_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (photoItem.isChecked()) {
                        photo_check_iv.setImageResource(R.drawable.compose_photo_preview_default);
                        photoItem.setChecked(false);
                        mask_view.setVisibility(View.GONE);
                        if (selectedItem.contains(photoItem)) {
                            selectedItem.remove(photoItem);
                        }
                    } else {
                        if (selectedItem.size() < maxSelectedCount) {
                            photo_check_iv.setImageResource(R.drawable.compose_photo_preview_right);
                            photoItem.setChecked(true);
                            mask_view.setVisibility(View.VISIBLE);
                            if (!selectedItem.contains(photoItem)) {
                                selectedItem.add(photoItem);
                            }
                        } else {
                            Toast.makeText(mContext, "最多选择" + maxSelectedCount + "张图片", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }


}
