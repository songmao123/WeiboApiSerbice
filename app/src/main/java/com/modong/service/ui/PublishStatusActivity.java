package com.modong.service.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.databinding.ActivityPublishStatusBinding;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishStatusActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_SELECT_PHOTO = 1;
    private ActivityPublishStatusBinding mBinding;
    private List<String> mSelectPhotos = new ArrayList<>();
    private ImageGridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_status);

        initRecyclerView();
        initEvents();
    }

    private void initRecyclerView() {
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, DensityUtil.dip2px(3), true, true);
        mBinding.recyclerView.addItemDecoration(decoration);
        mGridAdapter = new ImageGridAdapter(R.layout.item_publish_grid_image, mSelectPhotos);
        mGridAdapter.setOnRecyclerViewItemClickListener(this);
        mBinding.recyclerView.setAdapter(mGridAdapter);
    }

    @Override
    public void initEvents() {
        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);
        mBinding.bottomLayout.publishImageIv.setOnClickListener(this);
        mBinding.bottomLayout.publishAltIv.setOnClickListener(this);
        mBinding.bottomLayout.publishTopicIv.setOnClickListener(this);
        mBinding.bottomLayout.publishEmojiIv.setOnClickListener(this);
        mBinding.bottomLayout.publishMoreIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow_iv:
                finish();
                break;
            case R.id.next_tv:

                break;
            case R.id.publish_image_iv:
                Intent intent = new Intent(this, SelectPhotoActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
                break;
            case R.id.publish_alt_iv:

                break;
            case R.id.publish_topic_iv:
                String text = mBinding.inputEt.getText().toString();
                mBinding.inputEt.setText(text + "##");
                mBinding.inputEt.setSelection(mBinding.inputEt.getText().length() - 1);
                break;
            case R.id.publish_emoji_iv:

                break;
            case R.id.publish_more_iv:
                Toast.makeText(PublishStatusActivity.this, "More Function!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PHOTO) {
                if (data == null) return;
                List<String> datas = data.getStringArrayListExtra(SelectPhotoActivity.DATA_SELECTED_PHOTO);
                mSelectPhotos.clear();
                mSelectPhotos.addAll(datas);
                mSelectPhotos.add("");
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ImageGridAdapter extends BaseQuickAdapter<String> {

        private int imageHeight;

        public ImageGridAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
            this.imageHeight = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(23) * 2) / 3;
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        protected void convert(final BaseViewHolder helper, String s) {
            ImageView image_iv = helper.getView(R.id.image_iv);
            ImageView delete_iv = helper.getView(R.id.delete_iv);

            View rootView = helper.getConvertView();
            ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
            layoutParams.height = imageHeight;

            final int position = helper.getAdapterPosition();
            if (position == mData.size() - 1) {
                delete_iv.setVisibility(View.GONE);
                image_iv.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(mContext).load(R.drawable.compose_pic_add_highlighted).into(image_iv);
            } else {
                delete_iv.setVisibility(View.VISIBLE);
                image_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(mContext).load(new File(s)).into(image_iv);
                delete_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mData.remove(position);
                        notifyItemRemoved(position);
                    }
                });
            }
        }
    }
}
