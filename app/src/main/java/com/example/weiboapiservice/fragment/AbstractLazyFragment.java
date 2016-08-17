package com.example.weiboapiservice.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by 青松 on 2016/8/16.
 */
public abstract class AbstractLazyFragment extends Fragment {
    protected boolean isVisible;
    private boolean isFirst = true;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        if (isFirst) {
            isFirst = false;
            lazyLoad();
        }
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {}
}
