package com.modong.service.adapter;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.utils.DensityUtil;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Created by 青松 on 2016/10/27.
 */

public class PhotoWallAdapter extends BaseQuickAdapter<String> {

    private int screenWidth;

    public PhotoWallAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
        this.screenWidth = DensityUtil.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, String path) {
        CardView cardView = viewHolder.getView(R.id.card_view);
        ImageView imageView = viewHolder.getView(R.id.image_iv);

        ViewGroup.LayoutParams params = cardView.getLayoutParams();
        params.height = getRandomHeight(screenWidth);
        Glide.with(mContext).load(new File(path)).into(imageView);
    }

    private int getRandomHeight(int width) {
        int imageWidth = (width - DensityUtil.dip2px(5) * 3) / 2;
        int[] array = {imageWidth / 2, imageWidth, (int) (imageWidth * 1.5)};
        int random = new Random().nextInt(array.length);
        return imageWidth/*array[random]*/;
    }
}
