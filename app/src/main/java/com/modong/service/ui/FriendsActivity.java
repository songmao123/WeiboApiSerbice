package com.modong.service.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.modong.service.BaseActivity;
import com.modong.service.BaseApplication;
import com.modong.service.R;
import com.modong.service.databinding.ActivityFriendsBinding;
import com.modong.service.model.WeiboUserList;
import com.modong.service.retrofit.WeiboApiFactory;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FriendsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFriendsBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private int cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_friends);

        initEvents();
        getFriendsData();
    }

    @Override
    public void initEvents() {
        mCompositeSubscription = new CompositeSubscription();
        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.photoTitleTv.setText("联系人");
        mBinding.toolbarInclude.nextTv.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow_iv:
                finish();
                break;
        }
    }

    private void getFriendsData() {
        long uid = BaseApplication.getInstance().getAccountBean().getUser().getId();
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi().getFriendsData(uid, cursor, 200)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<WeiboUserList>() {
                @Override
                public void call(WeiboUserList weiboUserList) {
                    Log.i("sqsong", "Request Success!");
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    int code = ((HttpException) throwable).code();
                    Toast.makeText(FriendsActivity.this, "Error Code: " + code, Toast.LENGTH_SHORT).show();
                }
            }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
