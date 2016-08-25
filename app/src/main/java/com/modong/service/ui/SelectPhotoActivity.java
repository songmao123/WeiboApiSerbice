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
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;
import com.modong.service.view.SelectPhotoFolderPop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SelectPhotoActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, SelectPhotoFolderPop.OnFolderItemClickListener, BaseQuickAdapter.OnRecyclerViewItemClickListener,
        PhotoGridAdapter.OnCheckBoxClickListener {

    public static final String MAX_SELECT_PHOTO_COUNT = "max_select_photo_count";
    public static final String DATA_SELECTED_PHOTO = "data_selected_photo";
    public static final int DEFAULT_MAX_SELECT_COUNT = 9;
    public static final int REQUEST_CODE_IMAGE_PREVIEW = 1;

    private ActivitySelectPhotoBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private SelectPhotoFolderPop mSelectPhotoPop;
    private ArrayList<String> mPhotoItems = new ArrayList<>();
    private ArrayList<String> mSelectedPhotos = new ArrayList<>();
    private int mMaxSelectCount = DEFAULT_MAX_SELECT_COUNT;
    private PhotoGridAdapter mPhotoGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_photo);

        getIntentParams();
        initPopupWindow();
        initRecyclerView();
        initEvents();
        getPhotoFromPhone();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mMaxSelectCount = intent.getIntExtra(MAX_SELECT_PHOTO_COUNT, DEFAULT_MAX_SELECT_COUNT);
        }
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
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, DensityUtil.dip2px(3), true, false);
        mBinding.recyclerView.addItemDecoration(decoration);
        mPhotoGridAdapter = new PhotoGridAdapter(R.layout.item_photo_grid, mPhotoItems, mSelectedPhotos, mMaxSelectCount);
        mPhotoGridAdapter.setOnRecyclerViewItemClickListener(this);
        mPhotoGridAdapter.setCheckBoxClickListener(this);
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
                Intent intent = new Intent();
                intent.putStringArrayListExtra(DATA_SELECTED_PHOTO, mSelectedPhotos);
                setResult(RESULT_OK, intent);
                finish();
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
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<ArrayList<PhotoFolderItem>>() {
            @Override
            public void call(Subscriber<? super ArrayList<PhotoFolderItem>> subscriber) {
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
                ArrayList<PhotoFolderItem> photoFolderItemList = getPhotoFolderItemLists(photoLists);
                subscriber.onNext(photoFolderItemList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ArrayList<PhotoFolderItem>>() {
            @Override
            public void call(ArrayList<PhotoFolderItem> photoFolderItems) {
                if (photoFolderItems != null) {
                    mSelectPhotoPop.setPhotoDatas(photoFolderItems);

                    mPhotoItems.clear();
                    ArrayList<String> photos = photoFolderItems.get(0).getPhotos();
                    mPhotoItems.addAll(photos);
                    mPhotoGridAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private ArrayList<PhotoFolderItem> getPhotoFolderItemLists(ArrayList<String> photoLists) {
        ArrayList<PhotoFolderItem> photoItems = new ArrayList<>();

        PhotoFolderItem allPhoto = new PhotoFolderItem();
        allPhoto.setFolderName("所有照片");
        allPhoto.setChecked(true);
        allPhoto.setPhotos(new ArrayList<String>());
        allPhoto.getPhotos().add(R.drawable.ic_camera + "");
        photoItems.add(allPhoto);

        Map<String, PhotoFolderItem> map = new HashMap<>();
        for (String path: photoLists) {
            allPhoto.getPhotos().add(path);

            File file = new File(path);
            PhotoFolderItem parentDirName = map.get(file.getParentFile().getName());
            if (parentDirName == null) {
                parentDirName = new PhotoFolderItem();
                parentDirName.setFolderName(file.getParentFile().getName());
                parentDirName.setPhotos(new ArrayList<String>());
                parentDirName.getPhotos().add(R.drawable.ic_camera + "");
                photoItems.add(parentDirName);
                map.put(file.getParentFile().getName(), parentDirName);
            }
            parentDirName.getPhotos().add(path);
        }
        return photoItems;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, PhotoSelectPreviewActivity.class);
        ArrayList<String> allImages = new ArrayList<>();
        allImages.addAll(mPhotoItems.subList(1, mPhotoItems.size()));
        intent.putStringArrayListExtra(PhotoSelectPreviewActivity.ALL_IMAGES, allImages);
        intent.putStringArrayListExtra(PhotoSelectPreviewActivity.SELECTED_IMAGES, mSelectedPhotos);
        intent.putExtra(PhotoSelectPreviewActivity.CURRENT_POSITION, position - 1);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PREVIEW) {
            if (data != null) {
                boolean isComplete = data.getBooleanExtra(PhotoSelectPreviewActivity.PREVIEW_COMPLETE, false);
                ArrayList<String> selectPhotos = data.getStringArrayListExtra(PhotoSelectPreviewActivity.SELECTED_IMAGES);
                if (isComplete) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(DATA_SELECTED_PHOTO, selectPhotos);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    mSelectedPhotos.clear();
                    mSelectedPhotos.addAll(selectPhotos);
                    mPhotoGridAdapter.notifyDataSetChanged();
                    setNextStepText(mSelectedPhotos.size());
                }
            }
        }
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
