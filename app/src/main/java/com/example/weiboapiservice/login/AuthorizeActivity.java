package com.example.weiboapiservice.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.weiboapiservice.BaseActivity;
import com.example.weiboapiservice.BaseApplication;
import com.example.weiboapiservice.MainActivity;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.model.AccessToken;
import com.example.weiboapiservice.model.AccountBean;
import com.example.weiboapiservice.model.WeiboGroups;
import com.example.weiboapiservice.model.WeiboUser;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;
import com.example.weiboapiservice.utils.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class AuthorizeActivity extends BaseActivity {

    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    private AccountBean mAccountBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);

        initEvents();
    }

    @Override
    public void initEvents() {
        mAuthInfo = new AuthInfo(this,
                Constants.APP_KEY, Constants.REDIRECT_URL, Constants.APP_SCOPE);
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
                mAccountBean = new AccountBean();
                mAccountBean.setAccessToken(token);
                getUserInfos(token);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(AuthorizeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(AuthorizeActivity.this, "Authorize Cancel!", Toast.LENGTH_SHORT).show();
        }
    };

    private void getUserInfos(AccessToken token) {
        /*Observable.zip(generateUserObservable(token), generateGroupsObservable(token), new Func2<WeiboUser, WeiboGroups, AccountBean>() {
            @Override
            public AccountBean call(WeiboUser weiboUser, WeiboGroups weiboGroups) {
                mAccountBean.setUser(weiboUser);
                mAccountBean.setGroups(weiboGroups);
                return mAccountBean;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<AccountBean>() {
            @Override
            public void call(AccountBean accountBean) {
                BaseApplication.getInstance().setAccountBean(accountBean);
                Intent intent = new Intent(AuthorizeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                int code = ((HttpException) throwable).code();
                Toast.makeText(AuthorizeActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
            }
        });*/

        generateUserObservable(token)
                .flatMap(new Func1<WeiboUser, Observable<WeiboUser>>() {
                    @Override
                    public Observable<WeiboUser> call(final WeiboUser weiboUser) {
                        return Observable.create(new Observable.OnSubscribe<WeiboUser>() {
                            @Override
                            public void call(Subscriber<? super WeiboUser> subscriber) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                WeiboUser realmWeiboUser = realm.copyToRealm(weiboUser);
                                realm.commitTransaction();
                                subscriber.onNext(realmWeiboUser);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeiboUser>() {
                    @Override
                    public void call(WeiboUser weiboUser) {
                        mAccountBean.setUser(weiboUser);
                        BaseApplication.getInstance().setAccountBean(mAccountBean);
                        Intent intent = new Intent(AuthorizeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(AuthorizeActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Observable<WeiboUser> generateUserObservable(AccessToken token) {
        return WeiboApiFactory.createWeiboApi(null, token.getAccess_token()).getUserInfo(token.getUid());
    }

    private Observable<WeiboGroups> generateGroupsObservable(AccessToken token) {
        return WeiboApiFactory.createWeiboApi(null, token.getAccess_token()).getFriendGroups();
    }

    private AccessToken generateAccessToken(Oauth2AccessToken accessToken) {
        AccessToken token = new AccessToken();
        token.setUid(Long.parseLong(accessToken.getUid()));
        token.setAccess_token(accessToken.getToken());
        token.setExpires_in(accessToken.getExpiresTime());
        token.setValidDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime())));
        token.setRefresh_token(accessToken.getRefreshToken());
        return token;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
