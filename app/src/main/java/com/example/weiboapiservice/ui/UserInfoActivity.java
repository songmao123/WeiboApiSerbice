package com.example.weiboapiservice.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.weiboapiservice.BaseActivity;
import com.example.weiboapiservice.BaseApplication;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.adapter.SimpleFragmentPagerAdapter;
import com.example.weiboapiservice.databinding.ActivityUserInfoBinding;
import com.example.weiboapiservice.fragment.BlankFragment;
import com.example.weiboapiservice.fragment.user.UserStatusFragment;
import com.example.weiboapiservice.model.WeiboUser;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserInfoActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    public static final String USER_SCREEN_NAME = "user_screen_name";
    public static final String USER_INFO = "user_info";

    private static final int AVATAR_HIDDEN_PERCENT = 30;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;

    private ActivityUserInfoBinding mBinding;
    private DataInflateHelper mDataInflateHelper;
    private CompositeSubscription mCompositeSubscription;
    private List<Fragment> mFragments = new ArrayList<>();
    private WeiboUser mWeiboUser;
    private String mScreenName;

    public static void lunchUserInfoActivity(Context context, String screenName, WeiboUser weiboUser) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        if (!TextUtils.isEmpty(screenName)) {
            intent.putExtra(USER_SCREEN_NAME, screenName);
        }
        if (weiboUser != null) {
            intent.putExtra(USER_INFO, weiboUser);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);

        mDataInflateHelper = new DataInflateHelper(this, this);
        getIntentParams();
        initFragments();
        initEvents();
    }

    private void getIntentParams() {
        mCompositeSubscription = new CompositeSubscription();
        Intent intent = getIntent();
        if (intent != null) {
            mWeiboUser = intent.getParcelableExtra(USER_INFO);
            if (mWeiboUser != null) {
                mScreenName = mWeiboUser.getScreen_name();
                mDataInflateHelper.setUserInfos(mBinding, mWeiboUser);
            } else {
                Uri data = intent.getData();
                String dataStr = data.toString();
                mScreenName = dataStr.substring(dataStr.lastIndexOf("@") + 1);
                getUserInfoFromServer();
            }
        }
    }

    private void initFragments() {
        mFragments.add(BlankFragment.newInstance());
        mFragments.add(UserStatusFragment.newInstance(mScreenName));
        mFragments.add(BlankFragment.newInstance());
    }

    @Override
    public void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBinding.toolbar.post(new Runnable() {
            @Override
            public void run() {
                mBinding.toolbar.setTitle("");
            }
        });

        mBinding.appbarLayout.addOnOffsetChangedListener(this);

        String[] tabTitles = {"主页", "微博", "相册"};
        SimpleFragmentPagerAdapter mFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, tabTitles, mFragments);
        mBinding.viewpager.setAdapter(mFragmentPagerAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
        mBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        mBinding.viewpager.setCurrentItem(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        }

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;
        Log.i("sqsong", "Percentage: " + percentage + ", IsShown: " + mIsAvatarShown);
        if (percentage >= AVATAR_HIDDEN_PERCENT && mIsAvatarShown) {
            mIsAvatarShown = false;
            mBinding.userAvatarCiv.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= AVATAR_HIDDEN_PERCENT && !mIsAvatarShown) {
            mIsAvatarShown = true;
            mBinding.userAvatarCiv.animate().scaleY(1).scaleX(1).start();
        }
    }

    private void getUserInfoFromServer() {
        String access_token = BaseApplication.getInstance().getAccountBean().getAccessToken().getAccess_token();
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi(null, access_token)
        .getUserInfoByScreenName(mScreenName).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<WeiboUser>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable throwable) {
                int code = ((HttpException) throwable).code();
                Toast.makeText(UserInfoActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(WeiboUser weiboUser) {
                if (weiboUser != null) {
                    mDataInflateHelper.setUserInfos(mBinding, weiboUser);
                }
            }
        }));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
