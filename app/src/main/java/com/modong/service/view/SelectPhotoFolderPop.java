package com.modong.service.view;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.model.PhotoFolderItem;
import com.modong.service.model.PhotoItem;
import com.modong.service.ui.SelectPhotoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 青松 on 2016/8/23.
 */
public class SelectPhotoFolderPop extends PopupWindow implements BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private List<PhotoFolderItem> mPhotoItems = new ArrayList<>();
    private OnFolderItemClickListener listener;
    private PhotoFolderListAdapter mAdapter;
    private SelectPhotoActivity mContext;
    private RecyclerView mRecyclerView;
    private View mAnchor;
    private int curPosition;

    public interface OnFolderItemClickListener {
        void onPhotoFolderItemClicked(PhotoFolderItem item, int position);
    }

    public SelectPhotoFolderPop(SelectPhotoActivity context, View anchor, OnFolderItemClickListener l) {
        super(context);
        this.mContext = context;
        this.mAnchor = anchor;
        this.listener = l;

        init();
    }

    private void init() {
        Point point = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(point);
        View popView = mContext.getLayoutInflater().inflate(R.layout.pop_select_photo, null, false);

        mRecyclerView = (RecyclerView) popView.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PhotoFolderListAdapter(R.layout.item_photo_folder_list, mPhotoItems);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setContentView(popView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable());
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position != curPosition) {
            PhotoFolderItem item = mPhotoItems.get(position);
            item.setChecked(true);
            mAdapter.notifyItemChanged(position);
            PhotoFolderItem curItem = mPhotoItems.get(curPosition);
            curItem.setChecked(false);
            mAdapter.notifyItemChanged(curPosition);

            listener.onPhotoFolderItemClicked(item, position);
            curPosition = position;
        }
        dismiss();
    }

    public void setPhotoDatas(List<PhotoFolderItem> items) {
        mPhotoItems.clear();
        mPhotoItems.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showPop() {
        this.showAsDropDown(mAnchor, 0, 0, Gravity.BOTTOM);
    }

    private class PhotoFolderListAdapter extends BaseQuickAdapter<PhotoFolderItem> {

        public PhotoFolderListAdapter(int layoutResId, List<PhotoFolderItem> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PhotoFolderItem photoFolderItem) {
            ArrayList<PhotoItem> photos = photoFolderItem.getPhotos();
            PhotoItem item = photos.get(1);
            String folderName = photoFolderItem.getFolderName();

            ImageView photo_preview_iv = helper.getView(R.id.photo_preview_iv);
            TextView photo_folder_tv = helper.getView(R.id.photo_folder_tv);
            TextView photo_count_tv = helper.getView(R.id.photo_count_tv);

            Glide.with(mContext).load(new File(item.getFilePath())).centerCrop().into(photo_preview_iv);
            photo_folder_tv.setText(folderName);
            photo_count_tv.setText("(" + (photos.size() - 1) + ")");

            if (photoFolderItem.isChecked()) {
                helper.getConvertView().setBackgroundColor(mContext.getResources().getColor(R.color.color_E2E2E2));
            } else {
                helper.getConvertView().setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            }
        }
    }

}
