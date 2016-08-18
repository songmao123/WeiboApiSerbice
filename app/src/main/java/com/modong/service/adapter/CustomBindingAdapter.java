package com.modong.service.adapter;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.modong.service.R;
import com.squareup.picasso.Picasso;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by sqsong on 16-7-10.
 */
public class CustomBindingAdapter {

    @BindingAdapter({"imageUrl", "type"})
    public static void loadImage(final View view, final String url, int type) {
        if (type == 1) {
            Picasso.with(view.getContext()).load(url)/*.bitmapTransform(new CropCircleTransformation(imageView.getContext()))*/
                    .placeholder(R.drawable.ic_account_circle_grey)
                    .error(R.drawable.ic_account_circle_grey).into((ImageView) view);
        } else if (type == 2) {
            Glide.with(view.getContext()).load(url).placeholder(R.drawable.header).centerCrop().into((ImageView) view);
        } else if (type == 3) {
            Glide.with(view.getContext()).load(url).bitmapTransform(new BlurTransformation(view.getContext(), 25))
                    .placeholder(R.drawable.header)
                    .error(R.drawable.header).into((ImageView) view);
        } else {
            view.post(new Runnable() {
                @Override
                public void run() {
                    int width = view.getWidth();
                    int height = view.getHeight();
                    Glide.with(view.getContext()).load(url).asBitmap()
                            .into(new SimpleTarget<Bitmap>(width, height) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    view.setBackgroundDrawable(new BitmapDrawable(bitmap));
                                }
                            });
                }
            });
        }
    }
}
