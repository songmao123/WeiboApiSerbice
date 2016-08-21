package com.modong.service.fragment.user;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.modong.service.BaseApplication;
import com.modong.service.R;
import com.modong.service.adapter.WeiboStatusAdapter;
import com.modong.service.databinding.FragmentUserStatusBinding;
import com.modong.service.fragment.AbstractLazyFragment;
import com.modong.service.fragment.status.util.SpacesItemDecoration;
import com.modong.service.model.WeiboStatus;
import com.modong.service.model.WeiboStatusList;
import com.modong.service.retrofit.WeiboApiFactory;
import com.modong.service.ui.StatusDetailActivity;
import com.modong.service.ui.UserInfoActivity;
import com.modong.service.utils.Constants;
import com.modong.service.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserStatusFragment extends AbstractLazyFragment implements BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnRecyclerViewItemClickListener {

    private FragmentUserStatusBinding mBinding;
    private RecyclerView mRecyclerView;
    protected CompositeSubscription mCompositeSubscription;
    private List<WeiboStatus> mWeiboStatusLists = new ArrayList<>();
    private WeiboStatusAdapter mStatusAdapter;
    private String mScreenName;
    private boolean isPrepared;
    private int pageIndex = 1;

    public static UserStatusFragment newInstance(String screenName) {
        UserStatusFragment fragment = new UserStatusFragment();
        Bundle args = new Bundle();
        args.putString(UserInfoActivity.USER_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mScreenName = getArguments().getString(UserInfoActivity.USER_SCREEN_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_status, container, false);

        initEvents();
        isPrepared = true;
        lazyLoad();
        return mBinding.getRoot();
    }

    private void initEvents() {
        mCompositeSubscription = new CompositeSubscription();

        mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        SpacesItemDecoration decoration = new SpacesItemDecoration(DensityUtil.dip2px(10));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mStatusAdapter = new WeiboStatusAdapter(getActivity(), R.layout.item_timeline_status, mWeiboStatusLists);
        mStatusAdapter.setIsUserInfo(true);
        mStatusAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mStatusAdapter.isFirstOnly(true);
        mStatusAdapter.setOnLoadMoreListener(this);
        mStatusAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mStatusAdapter.setOnRecyclerViewItemClickListener(this);
        mStatusAdapter.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mStatusAdapter);
    }

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        getStatusList();
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        getStatusList();
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

    private void getStatusList() {
        mCompositeSubscription.add(getObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<WeiboStatusList>() {
                @Override
                public void call(WeiboStatusList weiboStatusList) {
                    List<WeiboStatus> weiboStatuses = weiboStatusList.getStatuses();
                    if (pageIndex == 1) {
                        mWeiboStatusLists.clear();
                        if (weiboStatuses == null || weiboStatuses.size() < 1) {
                            setAdapterEmpty();
                            return;
                        }
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
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                    if (pageIndex == 1) {
                        setAdapterEmpty();
                    }
                }
            }));
    }

    private Observable<WeiboStatusList> getObservable() {
        String userScreenName = BaseApplication.getInstance().getAccountBean().getUser().getScreen_name();
        String accessToken = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        if (mScreenName.equals(userScreenName)) {
            return WeiboApiFactory.createWeiboApi(null, accessToken).getUserStatusLists(mScreenName, pageIndex, 10);
        } else {
            return WeiboApiFactory.createWeiboApi(null, accessToken).getUserAllStatusLists(mScreenName, pageIndex, 10);
        }
    }

    protected void setAdapterEmpty() {
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty_message,
                (ViewGroup)mRecyclerView.getParent(), false);
        mStatusAdapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mStatusAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
