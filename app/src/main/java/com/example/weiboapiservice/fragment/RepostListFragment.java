package com.example.weiboapiservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weiboapiservice.R;
import com.example.weiboapiservice.adapter.RepostListAdapter;
import com.example.weiboapiservice.model.RepostStatusList;
import com.example.weiboapiservice.model.WeiboStatus;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;
import com.example.weiboapiservice.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RepostListFragment extends BaseListFragment {

    private List<WeiboStatus> mRepostStatusList = new ArrayList<>();
    private int pageIndex = 1;

    public static RepostListFragment newInstance(long statusId) {
        RepostListFragment fragment = new RepostListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(STATUS_ID, statusId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        mBaseQuickAdapter = new RepostListAdapter(getActivity(), R.layout.item_repost_list, mRepostStatusList);
    }

    @Override
    public void onItemClick(View view, int i) {

    }

    @Override
    public void getRepostStatusList() {
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi(null, access_token).getRepostList(mStatusId, Constants.APP_KEY, access_token, pageIndex)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<RepostStatusList>() {
            @Override
            public void call(RepostStatusList repostStatusList) {
                List<WeiboStatus> weiboStatuses = repostStatusList.getReposts();
                if (pageIndex == 1) {
                    mRepostStatusList.clear();
                    mRepostStatusList.addAll(weiboStatuses);
                    mBaseQuickAdapter.notifyDataSetChanged();
                } else {
                    if (weiboStatuses == null || weiboStatuses.size() < 1) {
                        mBaseQuickAdapter.notifyDataChangedAfterLoadMore(false);
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_no_more_data,
                                (ViewGroup) mRecyclerView.getParent(), false);
                        mBaseQuickAdapter.addFooterView(view);
                    } else {
                        mBaseQuickAdapter.notifyDataChangedAfterLoadMore(weiboStatuses, true);
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                int code = ((HttpException) throwable).code();
                Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
