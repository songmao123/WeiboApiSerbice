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
import com.modong.service.utils.CommonUtils;
import com.modong.service.utils.CommonUtils.PaletteColorType;
import com.modong.service.utils.DensityUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewActivity extends BaseActivity implements ImageGalleryAdapter.FullScreenImageLoader,
        View.OnClickListener {

    public static final String IMAGE_LIST = "image_list";
    public static final String IMAGE_POSITION = "image_position";
    public static final String LOCAL_IMAGE = "local_image";

    private int position;
    private boolean isLocal;
    private List<String> imageList;
    private PaletteColorType paletteColorType;
    private ActivityImagePreviewBinding mBinding;

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
            isLocal = intent.getBooleanExtra(LOCAL_IMAGE, false);
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
        if (isLocal) {
            Picasso.with(this).load(new File(imageUrl)).into(iv, new Callback() {
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
        } else {
            if (imageUrl.contains("thumbnail")) {
                imageUrl = imageUrl.replace("thumbnail", "large");
            }
            Picasso.with(this).load(imageUrl).into(iv, new Callback() {
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
        }
    }

    @Override
    public void onViewSingleTab() {
        ActivityCompat.finishAfterTransition(this);
    }

    private void applyPalette(Palette palette, LinearLayout bgLinearLayout){
        int bgColor = CommonUtils.getBackgroundColor(paletteColorType, palette);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.root_fl:
                ActivityCompat.finishAfterTransition(ImagePreviewActivity.this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.viewpager.removeOnPageChangeListener(onPageChangeListener);
    }
}
