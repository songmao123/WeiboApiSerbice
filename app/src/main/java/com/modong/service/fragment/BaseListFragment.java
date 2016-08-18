package com.modong.service.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.modong.service.BaseApplication;
import com.modong.service.R;
import com.modong.service.databinding.FragmentRepostListBinding;
import com.modong.service.utils.Constants;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseListFragment extends AbstractLazyFragment implements BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnRecyclerViewItemClickListener {

    public static final String STATUS_ID = "status_id";

    private FragmentRepostListBinding mBinding;
    protected CompositeSubscription mCompositeSubscription;
    protected BaseQuickAdapter mBaseQuickAdapter;
    protected int pageIndex = 1;
    protected long mStatusId;
    private boolean isPrepared;
    protected RecyclerView mRecyclerView;
    protected String access_token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatusId = getArguments().getLong(STATUS_ID);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repost_list, container, false);

        initEvents();

        isPrepared = true;
        lazyLoad();
        return mBinding.getRoot();
    }

    private void initEvents() {
        access_token = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        mRecyclerView = mBinding.idStickynavlayoutInnerscrollview;
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        initAdapter();
        mBaseQuickAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mBaseQuickAdapter.isFirstOnly(true);
        mBaseQuickAdapter.setOnLoadMoreListener(this);
        mBaseQuickAdapter.openLoadMore(Constants.PER_PAGE_COUNT, true);
        mBaseQuickAdapter.setOnRecyclerViewItemClickListener(this);
        mBaseQuickAdapter.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.layout_loading_progress,
                (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mBaseQuickAdapter);
    }

    protected abstract void initAdapter();

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        getDataList();
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        getDataList();
    }

    @Override
    public void onItemClick(View view, int i) {

    }

    protected abstract void getDataList();

    protected void setAdapterEmpty() {
        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_empty_message,
                (ViewGroup)mRecyclerView.getParent(), false);
        mBaseQuickAdapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mBaseQuickAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
