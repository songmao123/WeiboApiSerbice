package com.modong.service.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.modong.service.R;
import com.modong.service.utils.DensityUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sqsong on 16-8-18.
 */
public class ImageGalleryAdapter extends PagerAdapter {

    private final List<String> images;
    private FullScreenImageLoader fullScreenImageLoader;

    public interface FullScreenImageLoader {
        void loadFullScreenImage(PhotoViewAttacher attacher, ImageView iv, String imageUrl, int width, LinearLayout bglinearLayout);
        void onViewSingleTab();
    }

    public ImageGalleryAdapter(List<String> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_fullscreen_image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        String image = images.get(position);
        int width = DensityUtil.getScreenWidth();
        PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                fullScreenImageLoader.onViewSingleTab();
            }

            @Override
            public void onOutsidePhotoTap() {
                fullScreenImageLoader.onViewSingleTab();
            }
        });
        fullScreenImageLoader.loadFullScreenImage(attacher, imageView, image, width, linearLayout);
        container.addView(view, 0);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setFullScreenImageLoader(FullScreenImageLoader loader) {
        this.fullScreenImageLoader = loader;
    }
}
