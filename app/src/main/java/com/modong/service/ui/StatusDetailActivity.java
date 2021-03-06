package com.modong.service.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gxz.library.StickyNavLayout;
import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.adapter.SimpleFragmentPagerAdapter;
import com.modong.service.databinding.ActivityStatusDetailBinding;
import com.modong.service.fragment.CommentListFragment;
import com.modong.service.fragment.RepostListFragment;
import com.modong.service.fragment.status.util.TimeLineUtil;
import com.modong.service.model.CreateOrDestoryStatus;
import com.modong.service.model.WeiboStatus;
import com.modong.service.retrofit.WeiboApi;
import com.modong.service.retrofit.WeiboApiFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class StatusDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String STATUS_INFO = "status_info";
    public static final String STATUS_ID = "status_id";
    public static final String IS_SCROLL_TO_NAV = "is_scroll_to_nav";

    private ActivityStatusDetailBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private List<Fragment> mFragments = new ArrayList<>();
    private WeiboStatus mWeiboStatus;
    private DataInflateHelper mStatusHelper;
    private long mStatusId;
    private boolean isScrollToNav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status_detail);

        mStatusHelper = new DataInflateHelper(this, this);
        getIntentParams();
        initFragments();
        initEvents();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mWeiboStatus = intent.getParcelableExtra(STATUS_INFO);
            isScrollToNav = intent.getBooleanExtra(IS_SCROLL_TO_NAV, false);
            if (mWeiboStatus != null) {
                mStatusId = mWeiboStatus.getId();
                mStatusHelper.setStatusInfo(mBinding, mWeiboStatus, spannableTextFinishListener);
                statusRequestComplete();
            } else {
                mStatusId = intent.getLongExtra(STATUS_ID, -1);
                getStatusInfo();
                mBinding.swipLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.swipLayout.setRefreshing(true);
                    }
                }, 200);
            }
        }
    }

    private TimeLineUtil.OnSpannableTextFinishListener spannableTextFinishListener = new TimeLineUtil.OnSpannableTextFinishListener() {
        @Override
        public void onSpannableTextFinish() {
            if (mBinding.idStick != null) {
                mBinding.idStick.updateTopViews();
            }
        }
    };

    private void initFragments() {
        mFragments.add(RepostListFragment.newInstance(mStatusId));
        mFragments.add(CommentListFragment.newInstance(mStatusId));
    }

    @Override
    public void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mCompositeSubscription = new CompositeSubscription();
        mBinding.collectIv.setOnClickListener(this);

        String[] tabTitles = {"转发", "评论"};
        SimpleFragmentPagerAdapter mFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, tabTitles, mFragments);
        mBinding.idStickynavlayoutViewpager.setAdapter(mFragmentPagerAdapter);
        mBinding.idStickynavlayoutIndicator.setupWithViewPager(mBinding.idStickynavlayoutViewpager);
        mBinding.idStickynavlayoutIndicator.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.idStick.post(new Runnable() {
            @Override
            public void run() {
                mBinding.idStick.updateTopViews();
            }
        });
        mBinding.idStickynavlayoutViewpager.setCurrentItem(1);

        mBinding.idStick.setOnStickStateChangeListener(stickStateChangeListener);
        mBinding.swipLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        if (isScrollToNav) {
            mBinding.idStick.setStickNavAndScrollToNav();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getStatusInfo();
        }
    };

    private StickyNavLayout.onStickStateChangeListener stickStateChangeListener = new StickyNavLayout.onStickStateChangeListener() {
        @Override
        public void isStick(boolean isStick) {

        }

        @Override
        public void scrollPercent(float percent) {
            if (percent == 0) {
                mBinding.swipLayout.setEnabled(true);
                mBinding.swipLayout.setOnRefreshListener(mOnRefreshListener);
            } else {
                mBinding.swipLayout.setEnabled(false);
                mBinding.swipLayout.setOnRefreshListener(null);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forward_status_ll:
            case R.id.forward_repost_tv:
            case R.id.forward_comment_tv:
            case R.id.forward_like_tv:
                Intent intent = new Intent(this, StatusDetailActivity.class);
                intent.putExtra(StatusDetailActivity.STATUS_INFO, mWeiboStatus.getRetweeted_status());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.collect_iv:
                setFavorite();
                break;
        }
    }

    private void setFavorite() {
        final boolean favorited = mWeiboStatus.isFavorited();
        mCompositeSubscription.add(getFavoriteObservable(favorited)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<CreateOrDestoryStatus>() {
                @Override
                public void call(CreateOrDestoryStatus createOrDestoryStatus) {
                    if (favorited) {
                        Toast.makeText(StatusDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        mWeiboStatus.setFavorited(false);
                        mBinding.collectIv.setImageResource(R.drawable.radar_card_around_collect);
                    } else {
                        Toast.makeText(StatusDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        mWeiboStatus.setFavorited(true);
                        mBinding.collectIv.setImageResource(R.drawable.radar_card_around_collect_lighted);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(StatusDetailActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
                }
            }));
    }

    private Observable<CreateOrDestoryStatus> getFavoriteObservable(boolean favorite) {
        WeiboApi weiboApi = WeiboApiFactory.createWeiboApi();
        if (favorite) {
            return weiboApi.destoryFavorite(mWeiboStatus.getId());
        } else {
            return weiboApi.crateFavorite(mWeiboStatus.getId());
        }
    }

    private void getStatusInfo() {
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi().getSingleWeiboStatus(mStatusId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<WeiboStatus>() {
                @Override
                public void call(WeiboStatus weiboStatus) {
                    statusRequestComplete();
                    if (weiboStatus != null) {
                        mStatusHelper.setStatusInfo(mBinding, weiboStatus, spannableTextFinishListener);
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    statusRequestComplete();
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(StatusDetailActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
                }
            }));
    }

    private void statusRequestComplete() {
        mBinding.swipLayout.setRefreshing(false);
        mBinding.idStick.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
