package com.modong.service.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by sqsong on 16-8-15.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String[] mTabTitles;
    private List<Fragment> mFragments;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, String[] tabTitles, List<Fragment> fragments) {
        super(fm);
        this.mContext = context;
        this.mFragments = fragments;
        this.mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
