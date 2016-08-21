package com.modong.service.fragment.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.modong.service.R;
import com.modong.service.databinding.FragmentUserInfoBinding;
import com.modong.service.fragment.AbstractLazyFragment;
import com.modong.service.model.WeiboUser;
import com.modong.service.ui.UserInfoActivity;


public class UserInfoFragment extends AbstractLazyFragment {

    private FragmentUserInfoBinding mBinding;
    private boolean isSetUserInfo;

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_info, container, false);
        setUserInfo();
        return mBinding.getRoot();
    }

    private void setUserInfo() {
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof UserInfoActivity) {
            UserInfoActivity userInfoActivity = (UserInfoActivity) activity;
            WeiboUser weiboUser = userInfoActivity.mWeiboUser;
            if (weiboUser != null && !isSetUserInfo) {
                isSetUserInfo = true;
                mBinding.setUser(weiboUser);
            }
        }
    }

    @Override
    protected void lazyLoad() {

    }

    public void setUserInfo(WeiboUser weiboUser) {
        if (weiboUser != null && !isSetUserInfo) {
            isSetUserInfo = true;
            mBinding.setUser(weiboUser);
        }
    }
}
