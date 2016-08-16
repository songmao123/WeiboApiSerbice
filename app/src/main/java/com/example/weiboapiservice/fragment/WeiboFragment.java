package com.example.weiboapiservice.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.weiboapiservice.ui.StatusDetailActivity;
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
    public void onItemClick(View view, int position) {
        List<Pair<View, String>> pairList = new ArrayList<>();
        Pair<View, String> pair1 = new Pair<>(view.findViewById(R.id.user_avatar_civ), "user_avatar_civ");
        pairList.add(pair1);
        Pair<View, String> pair2 = new Pair<>(view.findViewById(R.id.user_name_tv), "user_name_tv");
        pairList.add(pair2);
        Pair<View, String> pair3 = new Pair<>(view.findViewById(R.id.status_publish_time_tv), "status_publish_time_tv");
        pairList.add(pair3);
        Pair<View, String> pair4 = new Pair<>(view.findViewById(R.id.from_text_tv), "from_text_tv");
        pairList.add(pair4);
        Pair<View, String> pair5 = new Pair<>(view.findViewById(R.id.status_from_tv), "status_from_tv");
        pairList.add(pair5);

        if (view.findViewById(R.id.status_content_tv).getVisibility() == View.VISIBLE) {
            Pair<View, String> pair6 = new Pair<>(view.findViewById(R.id.status_content_tv), "status_content_tv");
            pairList.add(pair6);
        }
        if (view.findViewById(R.id.status_image_ll).getVisibility() == View.VISIBLE) {
            Pair<View, String> pair7 = new Pair<>(view.findViewById(R.id.status_image_ll), "status_image_ll");
            pairList.add(pair7);
        }
        if (view.findViewById(R.id.forward_status_ll).getVisibility() == View.VISIBLE) {
            Pair<View, String> pair8 = new Pair<>(view.findViewById(R.id.forward_status_ll), "forward_status_ll");
            pairList.add(pair8);
        }
        if (view.findViewById(R.id.verified_iv).getVisibility() == View.VISIBLE) {
            Pair<View, String> pair9 = new Pair<>(view.findViewById(R.id.verified_iv), "verified_iv");
            pairList.add(pair9);
        }
        Pair<View, String>[] pairs = new Pair[pairList.size()];
        for (int i = 0; i < pairList.size(); i++) {
            pairs[i] = pairList.get(i);
        }
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        WeiboStatus weiboStatus = mWeiboStatusLists.get(position);
        Intent intent = new Intent(getActivity(), StatusDetailActivity.class);
        intent.putExtra(StatusDetailActivity.STATUS_INFO, weiboStatus);
        ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
