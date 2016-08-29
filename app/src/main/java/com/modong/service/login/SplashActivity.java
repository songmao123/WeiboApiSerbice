package com.modong.service.login;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.modong.service.BaseApplication;
import com.modong.service.MainActivity;
import com.modong.service.R;
import com.modong.service.databinding.ActivitySplashBinding;
import com.modong.service.db.WeiboDbExecutor;
import com.modong.service.model.AccessToken;
import com.modong.service.model.AccountBean;
import com.modong.service.model.SplashData;
import com.modong.service.model.WeiboUser;
import com.modong.service.retrofit.WeiboApiFactory;
import com.modong.service.utils.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySplashBinding mBinding;
    private Subscription mSubscribe;
    private int startTime = 3;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startTime--;
            mBinding.countDownText.setText("Close: " + startTime);
            if (startTime == 1) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofFloat(mBinding.countDownText, "alpha", 1.0f, 0.0f).setDuration(500).start();
                    }
                }, 500);
            }
            if (startTime == 0) {
                startMainActivity();
                return;
            }
            mHandler.postDelayed(this, 1000);
        }
    };
    private AccountBean mAccountBean;
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_splash);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        initEvents();
        fetchSplashImage();
    }

    private void initEvents() {
        mBinding.countDownText.setOnClickListener(this);
        mBinding.bottomLl.setAlpha(1.0f);
        mBinding.rootRl.setBackgroundResource(R.color.colorTransparentBlackBg);
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.anim_splash_bottom);
        mBinding.bottomLl.startAnimation(animation);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hiddenNavigationBar();
            }
        }, 800);
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void hiddenNavigationBar() {
        final View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    private void fetchSplashImage() {
        mSubscribe = WeiboApiFactory.createSplashApi().getSplashData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SplashData>() {
                    @Override
                    public void call(SplashData splashData) {
                        if (splashData != null) {
                            String image = splashData.getImg();
                            Glide.with(getApplicationContext()).load(image).crossFade().into(mBinding.splashImage);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.count_down_text:
                startMainActivity();
                break;
        }
    }

    private void startMainActivity() {
        stopTimer();
        Observable.create(new Observable.OnSubscribe<AccountBean>() {
            @Override
            public void call(Subscriber<? super AccountBean> subscriber) {
                AccountBean accountBean = null;
                AccessToken token = WeiboDbExecutor.getInstance().getTokenInfoFromDB();
                if (token != null) {
                    WeiboUser weiboUser = WeiboDbExecutor.getInstance().getWeiboUserFromDB(token.getUid());
                    if (weiboUser != null) {
                        accountBean = new AccountBean();
                        accountBean.setAccessToken(token);
                        accountBean.setUser(weiboUser);
                    }
                }
                subscriber.onNext(accountBean);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<AccountBean>() {
        @Override
        public void call(AccountBean accountBean) {
                Intent intent = null;
                if (accountBean != null) {
                    BaseApplication.getInstance().setAccountBean(accountBean);
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    doAuthorizedAction();
                }
            }
        });
    }

    private void stopTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void doAuthorizedAction() {
        AuthInfo mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.APP_SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);
        mSsoHandler.authorize(mAuthListener);
    }

    private WeiboAuthListener mAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (accessToken != null && accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime()));
                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
                Log.e("sqsong", "Auth Info: " + String.format(format, accessToken.getToken(), date));
                AccessToken token = generateAccessToken(accessToken);
                WeiboDbExecutor.getInstance().insertTokenInfo(token, false);
                mAccountBean = new AccountBean();
                mAccountBean.setAccessToken(token);
                getUserInfos(token);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(SplashActivity.this, "Authorize Cancel!", Toast.LENGTH_SHORT).show();
        }
    };

    private AccessToken generateAccessToken(Oauth2AccessToken accessToken) {
        AccessToken token = new AccessToken();
        token.setUid(Long.parseLong(accessToken.getUid()));
        token.setAccess_token(accessToken.getToken());
        token.setExpires_in(accessToken.getExpiresTime());
        token.setValidDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime())));
        token.setRefresh_token(accessToken.getRefreshToken());
        return token;
    }

    private void getUserInfos(AccessToken token) {
        generateUserObservable(token).flatMap(new Func1<WeiboUser, Observable<WeiboUser>>() {
            @Override
            public Observable<WeiboUser> call(final WeiboUser weiboUser) {
                return Observable.create(new Observable.OnSubscribe<WeiboUser>() {
                    @Override
                    public void call(Subscriber<? super WeiboUser> subscriber) {
                        WeiboDbExecutor.getInstance().insertLoginUser(weiboUser);
                        subscriber.onNext(weiboUser);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeiboUser>() {
                    @Override
                    public void call(WeiboUser weiboUser) {
                        mAccountBean.setUser(weiboUser);
                        BaseApplication.getInstance().setAccountBean(mAccountBean);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(SplashActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Observable<WeiboUser> generateUserObservable(AccessToken token) {
        return WeiboApiFactory.createWeiboApi(null, token.getAccess_token()).getUserInfoByUid(token.getUid());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
    }

}
