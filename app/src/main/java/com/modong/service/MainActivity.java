package com.modong.service;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.modong.service.databinding.ActivityMainBinding;
import com.modong.service.databinding.NavHeaderMainBinding;
import com.modong.service.fragment.FavoriteFragment;
import com.modong.service.fragment.LikeFragment;
import com.modong.service.fragment.WeiboFragment;
import com.modong.service.model.AccountBean;
import com.modong.service.model.WeiboUser;
import com.modong.service.ui.PublishStatusActivity;
import com.modong.service.ui.SelectPhotoActivity;
import com.modong.service.ui.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        WeiboFragment.OnFloatButtonShowListener, View.OnClickListener {

    private ActivityMainBinding mBinding;
    private NavHeaderMainBinding mNavHeaderBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initEvents();
        initFragments();
    }

    @Override
    public void initEvents() {
        setSupportActionBar(mBinding.appBarMain.toolbar);

        mBinding.appBarMain.fabText.setOnClickListener(this);
        mBinding.appBarMain.fabImage.setOnClickListener(this);

        DrawerLayout drawer = mBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mBinding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mBinding.navView.setNavigationItemSelectedListener(this);

        mNavHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.nav_header_main, mBinding.navView, false);
        mBinding.navView.addHeaderView(mNavHeaderBinding.navContainer);
        AccountBean accountBean = BaseApplication.getInstance().getAccountBean();
        WeiboUser weiboUser = accountBean.getUser();
        mNavHeaderBinding.setUser(weiboUser);
        mNavHeaderBinding.setClickHandler(new ClickListenerHandler());

        mBinding.appBarMain.toolbar.post(new Runnable() {
            @Override
            public void run() {
                mBinding.appBarMain.toolbar.setTitle("全部微博");
            }
        });

        mBinding.appBarMain.fam.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
        mBinding.appBarMain.fam.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom));
    }

    private void initFragments() {
        mFragments.add(WeiboFragment.newInstance());
        mFragments.add(FavoriteFragment.newInstance());
        mFragments.add(LikeFragment.newInstance());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, mFragments.get(0)).commit();
        mBinding.navView.setCheckedItem(R.id.nav_weibo);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_weibo) {
            showSelectedFragment(0);
            mBinding.appBarMain.toolbar.setTitle("全部微博");
        } else if (id == R.id.nav_favorite) {
            showSelectedFragment(1);
            mBinding.appBarMain.toolbar.setTitle("收藏");
        } /*else if (id == R.id.nav_comment) {
            showSelectedFragment(2);
            mBinding.appBarMain.toolbar.setTitle("评论我");
        } */ else if (id == R.id.nav_like) {
            showSelectedFragment(3);
            mBinding.appBarMain.toolbar.setTitle("赞我");
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this, SelectPhotoActivity.class));
        } else if (id == R.id.nav_send) {

        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSelectedFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment fragment = mFragments.get(i);
            if (i != position) {
                transaction.hide(fragment);
            } else {
                if (!fragment.isAdded()) {
                    transaction.add(R.id.content, fragment);
                }
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出魔动", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showButton() {
        mBinding.appBarMain.fam.showMenu(true);
    }

    @Override
    public void hiddenButton() {
        mBinding.appBarMain.fam.hideMenu(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_text:
                Intent publishIntent = new Intent(this, PublishStatusActivity.class);
                startActivity(publishIntent);
                mBinding.appBarMain.fam.close(true);
                break;
            case R.id.fab_image:
                Intent intent = new Intent(this, SelectPhotoActivity.class);
                intent.putExtra(SelectPhotoActivity.DIRECT_SELECT_PHOTO, true);
                startActivity(intent);
                mBinding.appBarMain.fam.close(true);
                break;
        }
    }

    public class ClickListenerHandler {
        public void onViewClick(View view) {
            WeiboUser weiboUser = BaseApplication.getInstance().getAccountBean().getUser();
            UserInfoActivity.lunchUserInfoActivity(MainActivity.this, null, weiboUser);
        }
    }
}
