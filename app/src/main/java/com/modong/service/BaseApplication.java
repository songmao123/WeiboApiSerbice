package com.modong.service;

import android.app.Application;

import com.modong.service.model.AccountBean;

/**
 * Created by sqsong on 16-8-7.
 */
public class BaseApplication extends Application {

    private static BaseApplication instance;
    private AccountBean mAccountBean;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public AccountBean getAccountBean() {
        return mAccountBean;
    }

    public void setAccountBean(AccountBean mAccountBean) {
        this.mAccountBean = mAccountBean;
    }
}
