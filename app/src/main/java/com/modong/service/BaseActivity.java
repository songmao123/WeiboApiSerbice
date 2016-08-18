package com.modong.service;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeListener;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this instanceof MainActivity)) {
            SwipeBackHelper.onCreate(this);
            configSwipeBackHelper();
        }
    }

    private void configSwipeBackHelper() {
        SwipeBackHelper.getCurrentPage(this)//get current instance
                .setSwipeBackEnable(true)//on-off
                .setSwipeEdge(200)//set the touch area。200 mean only the left 200px of screen can touch to begin swipe.
                .setSwipeEdgePercent(0.1f)//0.2 mean left 20% of screen can touch to begin swipe.
                .setSwipeSensitivity(0.5f)//sensitiveness of the gesture。0:slow  1:sensitive
//                .setScrimColor(Color.BLUE)//color of Scrim below the activity
                .setClosePercent(0.5f)//close activity when swipe over this
                .setSwipeRelateEnable(false)//if should move together with the following Activity
                .setSwipeRelateOffset(500)//the Offset of following Activity when setSwipeRelateEnable(true)
                .setDisallowInterceptTouchEvent(false)//your view can hand the events first.default false;
                .addListener(new SwipeListener() {
                    @Override
                    public void onScroll(float percent, int px) {

                    }

                    @Override
                    public void onEdgeTouch() {
                    }

                    @Override
                    public void onScrollToClose() {
                    }
                });
    }

    public abstract void initEvents();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!(this instanceof MainActivity)) {
            SwipeBackHelper.onPostCreate(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!(this instanceof MainActivity)) {
            SwipeBackHelper.onDestroy(this);
        }
    }
}
