package com.modong.service.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.adapter.ImageGalleryAdapter;
import com.modong.service.databinding.ActivityImagePreviewBinding;
import com.modong.service.utils.DensityUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends BaseActivity implements ImageGalleryAdapter.FullScreenImageLoader,
        View.OnClickListener {

    public static final String IMAGE_LIST = "image_list";
    public static final String IMAGE_POSITION = "image_position";

    private ActivityImagePreviewBinding mBinding;
    private PaletteColorType paletteColorType;
    private List<String> imageList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hiddenNavigationBar();
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_preview);

        getIntentParams();
        initEvents();
    }

    private void hiddenNavigationBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(IMAGE_POSITION, 0);
            imageList = intent.getStringArrayListExtra(IMAGE_LIST);
        }
    }

    @Override
    public void initEvents() {
        mBinding.rootFl.setOnClickListener(this);
        paletteColorType = PaletteColorType.VIBRANT;

        ImageGalleryAdapter galleryAdapter = new ImageGalleryAdapter(imageList);
        galleryAdapter.setFullScreenImageLoader(this);
        mBinding.viewpager.setAdapter(galleryAdapter);
        mBinding.viewpager.addOnPageChangeListener(onPageChangeListener);
        mBinding.viewpager.setCurrentItem(position);

        setTitlebarMargin();
        setToolbarText(position);
    }

    private void setTitlebarMargin() {
        int height = DensityUtil.getStatusBarHeight(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mBinding.titleRl.getLayoutParams();
        params.topMargin = height;
    }

    private void setToolbarText(final int position) {
        int totalPages = mBinding.viewpager.getAdapter().getCount();
        mBinding.indexTv.setText(String.format("%d/%d", (position + 1), totalPages));
    }

    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            mBinding.viewpager.setCurrentItem(position);
            setToolbarText(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}

    };

    @Override
    public void loadFullScreenImage(final PhotoViewAttacher attacher, final ImageView iv, String imageUrl, int width, final LinearLayout bglinearLayout) {
        if (TextUtils.isEmpty(imageUrl)) return;
        if (imageUrl.contains("thumbnail")) {
            imageUrl = imageUrl.replace("thumbnail", "large");
        }
        Picasso.with(this).load(imageUrl).resize(width, 0).into(iv, new Callback() {
            @Override
            public void onError() {}

            @Override
            public void onSuccess() {
                attacher.update();
                Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette, bglinearLayout);
                    }
                });
            }
        });


        /*Glide.with(this).load(imageUrl).asBitmap()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    iv.setImageBitmap(bitmap);
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            applyPalette(palette, bglinearLayout);
                        }
                    });
                }
        });*/
    }

    @Override
    public void onViewSingleTab() {
        ActivityCompat.finishAfterTransition(this);
    }

    private void applyPalette(Palette palette, LinearLayout bgLinearLayout){
        int bgColor = getBackgroundColor(palette);
        if (bgColor != -1)
            bgLinearLayout.setBackgroundColor(bgColor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private int getBackgroundColor(Palette palette) {
        int bgColor = -1;

        int vibrantColor = palette.getVibrantColor(0x000000);
        int lightVibrantColor = palette.getLightVibrantColor(0x000000);
        int darkVibrantColor = palette.getDarkVibrantColor(0x000000);

        int mutedColor = palette.getMutedColor(0x000000);
        int lightMutedColor = palette.getLightMutedColor(0x000000);
        int darkMutedColor = palette.getDarkMutedColor(0x000000);

        if (paletteColorType != null) {
            switch (paletteColorType) {
                case VIBRANT:
                    if (vibrantColor != 0) { // primary option
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) { // fallback options
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case LIGHT_VIBRANT:
                    if (lightVibrantColor != 0) { // primary option
                        bgColor = lightVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case DARK_VIBRANT:
                    if (darkVibrantColor != 0) { // primary option
                        bgColor = darkVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case MUTED:
                    if (mutedColor != 0) { // primary option
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) { // fallback options
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case LIGHT_MUTED:
                    if (lightMutedColor != 0) { // primary option
                        bgColor = lightMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case DARK_MUTED:
                    if (darkMutedColor != 0) { // primary option
                        bgColor = darkMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                default:
                    break;
            }
        }
        return bgColor;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.root_fl:
                ActivityCompat.finishAfterTransition(ImagePreviewActivity.this);
                break;
        }
    }

    public enum PaletteColorType {
        VIBRANT,
        LIGHT_VIBRANT,
        DARK_VIBRANT,
        MUTED,
        LIGHT_MUTED,
        DARK_MUTED
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.viewpager.removeOnPageChangeListener(onPageChangeListener);
    }
}
