package com.example.weiboapiservice.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.weiboapiservice.model.WeiboPicture;

import java.util.List;

/**
 * Created by sqsong on 16-8-10.
 */
public class ImageShowUtil {

    private Context mContext;
    private SparseArray<ImageParams> imageParamArray;

    public ImageShowUtil(Context context) {
        this.mContext = context;
        this.imageParamArray = new SparseArray<>();
    }

    public void showStatusImages(LinearLayout container, List<WeiboPicture> images) {
        if (container == null || images.size() <= 0) return;
        container.removeAllViews();
        container.setDrawingCacheEnabled(false);
        if (images.size() > 9) {
            images = images.subList(0, 9);
        }
        int totalSize= images.size();
        ImageParams imageParams = generateImageParam(totalSize);
        for (int i = 0; i < imageParams.totalLine; i++) {
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                layoutParams.setMargins(0, DensityUtil.dip2px(5), 0, 0);
            }
            linearLayout.setLayoutParams(layoutParams);
            for (int j = 0; j < imageParams.imagePerLine && i * imageParams.imagePerLine + j < totalSize ; j++) {
                int position = i * imageParams.imagePerLine + j;
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (imageParams.imageWidth, imageParams.imageWidth);
                if (j < 2) {
                    params.setMargins(0, 0, DensityUtil.dip2px(5), 0);
                }
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(mContext).load(images.get(position).getThumbnail_pic()).centerCrop().crossFade().into(imageView);
                linearLayout.addView(imageView);
            }
            container.addView(linearLayout);
        }
    }

    private ImageParams generateImageParam(int size) {
        ImageParams imageParam = imageParamArray.get(size);
        if (imageParam == null) {
            imageParam = new ImageParams();
            int tempWidth = DensityUtil.getScreenWidth();
            if (size == 1) {
                imageParam.totalLine = 1;
                imageParam.imagePerLine = 1;
                imageParam.imageWidth = tempWidth;
            } else if (size == 2) {
                imageParam.totalLine = 1;
                imageParam.imagePerLine = 2;
                imageParam.imageWidth = (tempWidth - DensityUtil.dip2px(5)) / 2;
            } else if (size == 3) {
                imageParam.totalLine = 1;
                imageParam.imagePerLine = 3;
                imageParam.imageWidth = (tempWidth - DensityUtil.dip2px(5) * 2) / 3;
            } else if (size == 4) {
                imageParam.totalLine = 2;
                imageParam.imagePerLine = 2;
                imageParam.imageWidth = (tempWidth - DensityUtil.dip2px(5)) / 2;
            } else if (size == 5 || size == 6) {
                imageParam.totalLine = 2;
                imageParam.imagePerLine = 3;
                imageParam.imageWidth = (tempWidth - DensityUtil.dip2px(5) * 2) / 3;
            } else if (size  == 7 || size == 8 || size == 9) {
                imageParam.totalLine = 3;
                imageParam.imagePerLine = 3;
                imageParam.imageWidth = (tempWidth - DensityUtil.dip2px(5) * 2) / 3;
            }
            imageParamArray.put(size, imageParam);
        }
        return imageParam;
    }

    private class ImageParams {
        public int imageWidth;
        public int totalLine;
        public int imagePerLine;
    }
}
