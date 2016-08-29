package com.modong.service.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.modong.service.R;
import com.modong.service.adapter.FavoriteStatusAdapter;
import com.modong.service.databinding.FragmentFavoriteBinding;
import com.modong.service.fragment.WeiboFragment.OnFloatButtonShowListener;
import com.modong.service.fragment.status.util.SpacesItemDecoration;
import com.modong.service.model.FavoriteStatusList;
import com.modong.service.model.FavoriteStatusList.FavoriteStatus;
import com.modong.service.model.WeiboStatus;
import com.modong.service.retrofit.WeiboApiFactory;
import com.modong.service.ui.StatusDetailActivity;
import com.modong.service.utils.Constants;
import com.modong.service.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FavoriteFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private FragmentFavoriteBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private List<FavoriteStatus> mWeiboStatusLists = new ArrayList<>();
    private FavoriteStatusAdapter mStatusAdapter;
    private OnFloatButtonShowListener listener;
    private int pageIndex = 1;

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite, container, false);
        initRecyclerView();
        initEvents();

        getFavoriteList();
        return mBinding.getRoot();
    }

    private void initEvents() {
        mCompositeSubscription = new CompositeSubscription();
    }

    private void initRecyclerView() {
        mBinding.swipLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        RecyclerView mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        SpacesItemDecoration decoration = new SpacesItemDecoration(DensityUtil.dip2px(10));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > Constants.FAB_SCROLL_OFFSET) {
                    if (dy > 0) {
                        listener.hiddenButton();
                    } else {
                        listener.showButton();
                    }
                }
            }
        });

        mStatusAdapter = new FavoriteStatusAdapter(getActivity(), R.layout.item_timeline_status, mWeiboStatusLists);
        mStatusAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mStatusAdapter.isFirstOnly(true);
        mStatusAdapter.setOnLoadMoreListener(this);
        mStatusAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mStatusAdapter.setOnRecyclerViewItemClickListener(this);
        mStatusAdapter.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mStatusAdapter);

        mBinding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getFavoriteList();
            }
        });
    }

    private void getFavoriteList() {
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi().getFavoriteStatusList(pageIndex, 10)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<FavoriteStatusList>() {
            @Override
            public void call(FavoriteStatusList favoriteStatusList) {
                mBinding.swipLayout.setRefreshing(false);
                List<FavoriteStatus> favorites = favoriteStatusList.getFavorites();
                if (pageIndex == 1) {
                    mWeiboStatusLists.clear();
                    mWeiboStatusLists.addAll(favorites);
                    mStatusAdapter.notifyDataSetChanged();
                } else {
                    if (favorites == null || favorites.size() < 1) {
                        mStatusAdapter.notifyDataChangedAfterLoadMore(false);
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_no_more_data,
                                (ViewGroup) mBinding.recyclerView.getParent(), false);
                        mStatusAdapter.addFooterView(view);
                    } else {
                        mStatusAdapter.notifyDataChangedAfterLoadMore(favorites, true);
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mBinding.swipLayout.setRefreshing(false);
                int code = ((HttpException) throwable).code();
                Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        getFavoriteList();
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
        WeiboStatus weiboStatus = mWeiboStatusLists.get(position).getStatus();
        Intent intent = new Intent(getActivity(), StatusDetailActivity.class);
        intent.putExtra(StatusDetailActivity.STATUS_INFO, weiboStatus);
        ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFloatButtonShowListener) {
            listener = (OnFloatButtonShowListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
