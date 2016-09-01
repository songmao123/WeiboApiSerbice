package com.modong.service.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.modong.service.BaseActivity;
import com.modong.service.BaseApplication;
import com.modong.service.R;
import com.modong.service.adapter.FriendsOrderListAdapter;
import com.modong.service.databinding.ActivityFriendsBinding;
import com.modong.service.model.WeiboUser;
import com.modong.service.model.WeiboUserList;
import com.modong.service.retrofit.WeiboApiFactory;
import com.modong.service.utils.Pinyin4jUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FriendsActivity extends BaseActivity implements View.OnClickListener, FriendsOrderListAdapter.OnRecyclerViewItemClickListener {

    public static final String MENTION_NAME = "mention_name";

    private ActivityFriendsBinding mBinding;
    private CompositeSubscription mCompositeSubscription;
    private ArrayList<WeiboUser> mUsers = new ArrayList<>();
    private FriendsOrderListAdapter mAdapter;
    private int cursor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_friends);

        initRecyclerView();
        initEvents();
        getFriendsData();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(layoutManager);

        mAdapter = new FriendsOrderListAdapter(this, mUsers);
        mAdapter.setOnRecyclerViewItemClickListener(this);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.fastscroll.setRecyclerView(mBinding.recyclerView);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mBinding.recyclerView.addItemDecoration(headersDecor);

        mBinding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        }));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
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

    @Override
    public void onRecyclerViewHeaderClick(View view) {
        Toast.makeText(FriendsActivity.this, "Header Clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecyclerViewItemClick(View view, WeiboUser user, int position) {
        String screenName = user.getScreen_name();
        Intent intent = new Intent();
        intent.putExtra(MENTION_NAME, screenName);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void getFriendsData() {
        long uid = BaseApplication.getInstance().getAccountBean().getUser().getId();
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi().getFriendsData(uid, cursor, 200)
                .flatMap(new Func1<WeiboUserList, Observable<List<WeiboUser>>>() {
                    @Override
                    public Observable<List<WeiboUser>> call(final WeiboUserList weiboUserList) {
                        return Observable.create(new Observable.OnSubscribe<List<WeiboUser>>() {
                            @Override
                            public void call(Subscriber<? super List<WeiboUser>> subscriber) {
                                if (weiboUserList != null) {
                                    List<WeiboUser> users = weiboUserList.getUsers();
                                    for (WeiboUser user : users) {
                                        String screenName = user.getScreen_name();
                                        Set<String> stringSet = Pinyin4jUtil.str2Pinyin(screenName, Pinyin4jUtil.RET_PINYIN_TYPE_FULL);
                                        user.setPinyin((String) stringSet.toArray()[0]);
                                    }
                                    Collections.sort(users, new Comparator<WeiboUser>() {
                                        @Override
                                        public int compare(WeiboUser user1, WeiboUser user2) {
                                            String pinyin1 = user1.getPinyin().toLowerCase();
                                            String pinyin2 = user2.getPinyin().toLowerCase();
                                            return pinyin1.compareTo(pinyin2);
                                        }
                                    });
                                    subscriber.onNext(users);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<WeiboUser>>() {
                    @Override
                    public void call(List<WeiboUser> weiboUsers) {
                        mUsers.clear();
                        mUsers.addAll(weiboUsers);
                        mAdapter.notifyDataSetChanged();
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

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
