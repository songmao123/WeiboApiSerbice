package com.modong.service.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.adapter.PhotoGridAdapter;
import com.modong.service.databinding.ActivitySelectPhotoBinding;
import com.modong.service.model.PhotoFolderItem;
import com.modong.service.model.PhotoItem;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;
import com.modong.service.view.SelectPhotoFolderPop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SelectPhotoActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, SelectPhotoFolderPop.OnFolderItemClickListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private ActivitySelectPhotoBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private SelectPhotoFolderPop mSelectPhotoPop;
    private List<PhotoItem> mPhotoItems = new ArrayList<>();
    private PhotoGridAdapter mPhotoGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_photo);

        initPopupWindow();
        initRecyclerView();
        initEvents();
        getPhotoFromPhone();
    }

    private void initPopupWindow() {
        mSelectPhotoPop = new SelectPhotoFolderPop(this, mBinding.toolbar, this);
        mSelectPhotoPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mBinding.toolbarInclude.photoTitleCb.setChecked(false);
            }
        });
    }

    private void initRecyclerView() {
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, DensityUtil.dip2px(3), true);
        mBinding.recyclerView.addItemDecoration(decoration);
        mPhotoGridAdapter = new PhotoGridAdapter(R.layout.item_photo_grid, mPhotoItems);
        mPhotoGridAdapter.setOnRecyclerViewItemClickListener(this);
        mBinding.recyclerView.setAdapter(mPhotoGridAdapter);
    }

    @Override
    public void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        mCompositeSubscription = new CompositeSubscription();

        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);
        mBinding.toolbarInclude.photoTitleCb.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow_iv:
                ActivityCompat.finishAfterTransition(this);
                break;
            case R.id.next_tv:

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            if (mSelectPhotoPop != null && !mSelectPhotoPop.isShowing()) {
                mSelectPhotoPop.showPop();
            }
        } else {
            if (mSelectPhotoPop != null && mSelectPhotoPop.isShowing()) {
                mSelectPhotoPop.dismiss();
            }
        }
    }

    private void getPhotoFromPhone() {
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<List<PhotoFolderItem>>() {
            @Override
            public void call(Subscriber<? super List<PhotoFolderItem>> subscriber) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Uri uri = intent.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                ArrayList<String> photoLists = new ArrayList<>();
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                while (cursor != null && cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    if (TextUtils.isEmpty(path)) continue;
                    photoLists.add(new File(path).getAbsolutePath());
                }
                cursor.close();

                Collections.reverse(photoLists);
                List<PhotoFolderItem> photoFolderItemList = getPhotoFolderItemLists(photoLists);
                subscriber.onNext(photoFolderItemList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<PhotoFolderItem>>() {
            @Override
            public void call(List<PhotoFolderItem> photoFolderItems) {
                if (photoFolderItems != null) {
                    mSelectPhotoPop.setPhotoDatas(photoFolderItems);

                    mPhotoItems.clear();
                    ArrayList<PhotoItem> photos = photoFolderItems.get(0).getPhotos();
                    mPhotoItems.addAll(photos);
                    mPhotoGridAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private List<PhotoFolderItem> getPhotoFolderItemLists(ArrayList<String> photoLists) {
        List<PhotoFolderItem> photoItems = new ArrayList<>();

        PhotoFolderItem allPhoto = new PhotoFolderItem();
        allPhoto.setFolderName("所有照片");
        allPhoto.setChecked(true);
        allPhoto.setPhotos(new ArrayList<PhotoItem>());
        allPhoto.getPhotos().add(new PhotoItem(R.drawable.ic_camera + ""));
        photoItems.add(allPhoto);

        Map<String, PhotoFolderItem> map = new HashMap<>();
        for (String path: photoLists) {
            allPhoto.getPhotos().add(new PhotoItem(path));

            File file = new File(path);
            PhotoFolderItem parentDirName = map.get(file.getParentFile().getName());
            if (parentDirName == null) {
                parentDirName = new PhotoFolderItem();
                parentDirName.setFolderName(file.getParentFile().getName());
                parentDirName.setPhotos(new ArrayList<PhotoItem>());
                parentDirName.getPhotos().add(new PhotoItem(R.drawable.ic_camera + ""));
                photoItems.add(parentDirName);
                map.put(file.getParentFile().getName(), parentDirName);
            }
            parentDirName.getPhotos().add(new PhotoItem(path));
        }
        return photoItems;
    }

    @Override
    public void onItemClick(View view, int i) {

    }

    @Override
    public void onPhotoFolderItemClicked(PhotoFolderItem item, int position) {
        mPhotoItems.clear();
        mPhotoItems.addAll(item.getPhotos());
        mPhotoGridAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mSelectPhotoPop != null && mSelectPhotoPop.isShowing()) {
                mSelectPhotoPop.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
