package com.example.weiboapiservice.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weiboapiservice.BaseApplication;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.databinding.FragmentWeiboBinding;
import com.example.weiboapiservice.model.WeiboStatusList;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeiboFragment extends Fragment {

    private FragmentWeiboBinding mBinding;

    private int startPage = 1;

    public static WeiboFragment newInstance() {
        WeiboFragment fragment = new WeiboFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weibo, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getHomeStatusLists();
    }

    private void getHomeStatusLists() {
        String accessToken = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        WeiboApiFactory.createWeiboApi(null, accessToken)
            .getHomeStatusLists(startPage, 10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<WeiboStatusList>() {
                @Override
                public void call(WeiboStatusList weiboStatusList) {
                    Toast.makeText(getActivity(), "Request Success!", Toast.LENGTH_SHORT).show();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                }
            });
    }
}
