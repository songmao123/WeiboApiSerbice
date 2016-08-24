package com.modong.service.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.adapter.PhotoGridAdapter.OnCheckBoxClickListener;
import com.modong.service.databinding.ActivityPhotoSelectPreviewBinding;
import com.modong.service.utils.CommonUtils;
import com.modong.service.utils.CommonUtils.PaletteColorType;
import com.modong.service.utils.DensityUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoSelectPreviewActivity extends BaseActivity implements OnCheckBoxClickListener, View.OnClickListener {

    public static final String ALL_IMAGES = "all_images";
    public static final String SELECTED_IMAGES = "selected_images";
    public static final String CURRENT_POSITION = "current_position";
    public static final String MAX_SELECTED_COUNT = "max_selected_count";
    public static final String PREVIEW_COMPLETE = "preview_complete";


    private ActivityPhotoSelectPreviewBinding mBinding;
    private ArrayList<String> mAllImages;
    private ArrayList<String> mSelectedImages;
    private int curPosition = 0;
    private int maxSelectedCount = 0;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_select_preview);

        getIntentParams();
        initEvents();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            this.mAllImages = intent.getStringArrayListExtra(ALL_IMAGES);
            this.mSelectedImages = intent.getStringArrayListExtra(SELECTED_IMAGES);
            this.curPosition = intent.getIntExtra(CURRENT_POSITION, 0);
            this.maxSelectedCount = intent.getIntExtra(MAX_SELECTED_COUNT, 9);
        }
    }

    @Override
    public void initEvents() {
        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);

        PhotoPreviewPagerAdapter pagerAdapter = new PhotoPreviewPagerAdapter(this, mAllImages, mSelectedImages, maxSelectedCount,this);
        mBinding.viewpager.setAdapter(pagerAdapter);
        mBinding.viewpager.addOnPageChangeListener(onPageChangeListener);
        mBinding.viewpager.setCurrentItem(curPosition);
        setToolbarText(curPosition);
        setNextStepText(mSelectedImages.size());
    }

    private void setToolbarText(int position) {
        int totalPages = mBinding.viewpager.getAdapter().getCount();
        mBinding.toolbarInclude.photoTitleTv.setText(String.format("%d/%d", (position + 1), totalPages));
    }

    @Override
    public void onCheckBoxClicked(int count) {
        setNextStepText(count);
    }

    private void setNextStepText(int count) {
        if (count > 0) {
            mBinding.toolbarInclude.nextTv.setText("下一步 (" + count + ")");
        } else {
            mBinding.toolbarInclude.nextTv.setText("下一步");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow_iv:
                backAction(false);
                break;
            case R.id.next_tv:
                backAction(true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            backAction(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backAction(boolean isComplete) {
        Intent data = new Intent();
        data.putStringArrayListExtra(SELECTED_IMAGES, mSelectedImages);
        data.putExtra(PREVIEW_COMPLETE, isComplete);
        setResult(RESULT_OK, data);
        finish();
    }

    private class PhotoPreviewPagerAdapter extends PagerAdapter {

        private Context context;
        private ArrayList<String> photos;
        private ArrayList<String> selectPhotos;
        private PaletteColorType paletteColorType;
        private int maxSelectedCount;
        private OnCheckBoxClickListener listener;

        public PhotoPreviewPagerAdapter(Context context, ArrayList<String> photos, ArrayList<String> selectPhotos,
                                        int maxSelectedCount, OnCheckBoxClickListener l) {
            this.context = context;
            this.photos = photos;
            this.selectPhotos = selectPhotos;
            this.maxSelectedCount = maxSelectedCount;
            this.listener = l;
            this.paletteColorType = PaletteColorType.VIBRANT;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_select_photo_preview, null);
            final ImageView check_iv = (ImageView) view.findViewById(R.id.check_iv);
            final String photo = photos.get(position);
            if (selectPhotos.contains(photo)) {
                check_iv.setImageResource(R.drawable.compose_photo_preview_right);
            } else {
                check_iv.setImageResource(R.drawable.compose_photo_preview_default);
            }

            check_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectPhotos.contains(photo)) {
                        selectPhotos.remove(photo);
                        check_iv.setImageResource(R.drawable.compose_photo_preview_default);
                    } else {
                        if (selectPhotos.size() >= maxSelectedCount) {
                            Toast.makeText(context, "最多选择" + maxSelectedCount + "张图片", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            selectPhotos.add(photo);
                            check_iv.setImageResource(R.drawable.compose_photo_preview_right);
                        }
                    }
                    if (listener != null) {
                        listener.onCheckBoxClicked(selectPhotos.size());
                    }
                }
            });

            final ImageView photo_view = (ImageView) view.findViewById(R.id.photo_view);
            final RelativeLayout root_rl = (RelativeLayout) view.findViewById(R.id.root_rl);
            int width = DensityUtil.getScreenWidth();
            final PhotoViewAttacher attacher = new PhotoViewAttacher(photo_view);
            Picasso.with(context).load(new File(photo))/*.resize(width, 0)*/.into(photo_view, new Callback() {
                @Override
                public void onError() {}

                @Override
                public void onSuccess() {
                    attacher.update();
                    Bitmap bitmap = ((BitmapDrawable) photo_view.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            applyPalette(palette, root_rl);
                        }
                    });
                }
            });

            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void applyPalette(Palette palette, RelativeLayout background){
            int bgColor = CommonUtils.getBackgroundColor(paletteColorType, palette);
            if (bgColor != -1)
                background.setBackgroundColor(bgColor);
        }
    }
}
