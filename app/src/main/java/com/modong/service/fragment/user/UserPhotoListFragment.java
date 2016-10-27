package com.modong.service.fragment.user;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.modong.service.R;
import com.modong.service.adapter.PhotoWallAdapter;
import com.modong.service.databinding.FragmentUserPhotoListBinding;
import com.modong.service.fragment.AbstractLazyFragment;
import com.modong.service.ui.ImagePreviewActivity;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserPhotoListFragment extends AbstractLazyFragment implements BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private static final String USER_UID = "user_uid";

    private long mUid;
    private int pageIndex = 1;
    private boolean isPrepared;
    private FragmentUserPhotoListBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private ArrayList<String> mPhotos = new ArrayList<>();
    private PhotoWallAdapter mAdapter;

    public static UserPhotoListFragment newInstance(long uid) {
        UserPhotoListFragment fragment = new UserPhotoListFragment();
        Bundle args = new Bundle();
        args.putLong(USER_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getInt(USER_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_photo_list, container, false);

        initEvents();
        isPrepared = true;
        lazyLoad();
        return mBinding.getRoot();
    }

    private void initEvents() {
        mCompositeSubscription = new CompositeSubscription();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(2, DensityUtil.dip2px(5), true, true);
        mBinding.recyclerView.addItemDecoration(decoration);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter = new PhotoWallAdapter(R.layout.item_photo_wall, mPhotos);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        getPhotoFromPhone();
    }

    private void getPhotoFromPhone() {
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<ArrayList<String>>() {
            @Override
            public void call(Subscriber<? super ArrayList<String>> subscriber) {
                ArrayList<String> photoLists = getPhotoLists();
                subscriber.onNext(photoLists);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<ArrayList<String>>() {
                @Override
                public void call(ArrayList<String> photoList) {
                    if (photoList != null && photoList.size() > 0) {
                        mPhotos.clear();
                        mPhotos.addAll(photoList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }));
    }

    private ArrayList<String> getPhotoLists() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        String[] proj = {MediaStore.Images.Media.DATA};
        ArrayList<String> photoLists = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            String path = cursor.getString(0);
            if (TextUtils.isEmpty(path)) continue;
            photoLists.add(new File(path).getAbsolutePath());
        }
        cursor.close();
        Collections.reverse(photoLists);
        return photoLists;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.IMAGE_POSITION, position);
        intent.putStringArrayListExtra(ImagePreviewActivity.IMAGE_LIST, mPhotos);
        intent.putExtra(ImagePreviewActivity.LOCAL_IMAGE, true);
        startActivity(intent);
    }

    /*private void getPhotoList() {
        confirmUid();
        String access_token = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi(null, access_token)
            .getUserPhotoList(mUid, pageIndex)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<WeiboStatusList>() {
                @Override
                public void call(WeiboStatusList weiboStatusList) {

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                    if (pageIndex == 1) {

                    }
                }
        }));
    }

    private void confirmUid() {
        if (mUid == 0) {
            FragmentActivity activity = getActivity();
            if (activity != null && activity instanceof UserInfoActivity) {
                UserInfoActivity userInfoActivity = (UserInfoActivity) activity;
                mUid = userInfoActivity.mUid;
            }
        }
    }*/
}
