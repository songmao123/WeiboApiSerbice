package com.example.weiboapiservice.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.example.weiboapiservice.BaseActivity;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.adapter.SimpleFragmentPagerAdapter;
import com.example.weiboapiservice.databinding.ActivityStatusDetailBinding;
import com.example.weiboapiservice.fragment.CommentListFragment;
import com.example.weiboapiservice.fragment.RepostListFragment;

import java.util.ArrayList;
import java.util.List;

public class StatusDetailActivity extends BaseActivity {

    private ActivityStatusDetailBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status_detail);

        initFragments();
        initEvents();
    }

    private void initFragments() {
        mFragments.add(RepostListFragment.newInstance());
        mFragments.add(CommentListFragment.newInstance());
    }

    @Override
    public void initEvents() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        SimpleFragmentPagerAdapter mFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, mFragments);
        mBinding.idStickynavlayoutViewpager.setAdapter(mFragmentPagerAdapter);
        mBinding.idStickynavlayoutIndicator.setupWithViewPager(mBinding.idStickynavlayoutViewpager);
        mBinding.idStickynavlayoutIndicator.setTabMode(TabLayout.MODE_SCROLLABLE);
        mBinding.idStick.post(new Runnable() {
            @Override
            public void run() {
                mBinding.idStick.updateTopViews();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
