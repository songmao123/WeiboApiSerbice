package com.modong.service.fragment.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.modong.service.BaseApplication;
import com.modong.service.R;
import com.modong.service.databinding.FragmentUserPhotoListBinding;
import com.modong.service.fragment.AbstractLazyFragment;
import com.modong.service.model.WeiboStatusList;
import com.modong.service.retrofit.WeiboApiFactory;
import com.modong.service.ui.UserInfoActivity;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserPhotoListFragment extends AbstractLazyFragment {

    private static final String USER_UID = "user_uid";
    private FragmentUserPhotoListBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private long mUid;
    private boolean isPrepared;
    private int pageIndex = 1;

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
    }

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
//        getPhotoList();
    }

    private void getPhotoList() {
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
    }
}
