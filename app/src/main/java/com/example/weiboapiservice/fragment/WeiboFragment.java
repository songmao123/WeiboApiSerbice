package com.example.weiboapiservice.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.weiboapiservice.BaseApplication;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.adapter.WeiboStatusAdapter;
import com.example.weiboapiservice.databinding.FragmentWeiboBinding;
import com.example.weiboapiservice.fragment.status.util.SpacesItemDecoration;
import com.example.weiboapiservice.model.WeiboStatus;
import com.example.weiboapiservice.model.WeiboStatusList;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;
import com.example.weiboapiservice.utils.Constants;
import com.example.weiboapiservice.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class WeiboFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private FragmentWeiboBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private List<WeiboStatus> mWeiboStatusLists = new ArrayList<>();

    private int startPage = 1;
    private WeiboStatusAdapter mStatusAdapter;
    private SwipeRefreshLayout mSwipLayout;

    public static WeiboFragment newInstance() {
        WeiboFragment fragment = new WeiboFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
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
        initEvents();
        getHomeStatusLists();
    }

    private void initEvents() {
        mSwipLayout = mBinding.swipLayout;
        mSwipLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        RecyclerView mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        SpacesItemDecoration decoration = new SpacesItemDecoration(DensityUtil.dip2px(10));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mStatusAdapter = new WeiboStatusAdapter(getActivity(), R.layout.item_timeline_status, mWeiboStatusLists);
        mStatusAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mStatusAdapter.isFirstOnly(true);
        mStatusAdapter.setOnLoadMoreListener(this);
        mStatusAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mStatusAdapter.setOnRecyclerViewItemClickListener(this);
        mStatusAdapter.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mStatusAdapter);

        mSwipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startPage = 1;
                getHomeStatusLists();
            }
        });
    }

    private void getHomeStatusLists() {
        String accessToken = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        Subscription subscribe = WeiboApiFactory.createWeiboApi(null, accessToken)
                .getHomeStatusLists(startPage, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeiboStatusList>() {
                    @Override
                    public void call(WeiboStatusList weiboStatusList) {
                        mSwipLayout.setRefreshing(false);
                        List<WeiboStatus> weiboStatuses = weiboStatusList.getStatuses();
                        if (startPage == 1) {
                            mWeiboStatusLists.clear();
                            mWeiboStatusLists.addAll(weiboStatuses);
                            mStatusAdapter.notifyDataSetChanged();
                        } else {
                            if (weiboStatuses == null || weiboStatuses.size() < 1) {
                                mStatusAdapter.notifyDataChangedAfterLoadMore(false);
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_no_more_data,
                                        (ViewGroup) mBinding.recyclerView.getParent(), false);
                                mStatusAdapter.addFooterView(view);
                            } else {
                                mStatusAdapter.notifyDataChangedAfterLoadMore(weiboStatuses, true);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mSwipLayout.setRefreshing(false);
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscribe);
    }

    @Override
    public void onLoadMoreRequested() {
        startPage++;
        getHomeStatusLists();
    }

    @Override
    public void onItemClick(View view, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
