package com.modong.service.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.fragment.status.util.ImageBitmapCache;
import com.modong.service.model.Emotion;
import com.modong.service.model.EmotionItem;
import com.modong.service.utils.CommonUtils;
import com.modong.service.utils.DensityUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by 青松 on 2016/8/26.
 */
public class EmotionPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<EmotionItem> mEmotionItems;
    private LayoutInflater mInflater;
    private EmotionGridItemDecoration decoration;

    public EmotionPagerAdapter(Context context, List<EmotionItem> emotionItems) {
        this.mContext = context;
        this.mEmotionItems = emotionItems;
        this.mInflater = LayoutInflater.from(context);
        this.decoration = new EmotionGridItemDecoration(7, DensityUtil.dip2px(18), true);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RecyclerView rootView = (RecyclerView) mInflater.inflate(R.layout.layout_emotion_gridview, container, false);
        rootView.setLayoutManager(new GridLayoutManager(mContext, 7));
        rootView.addItemDecoration(decoration);
        EmotionItem emotionItem = mEmotionItems.get(position);
        EmotionGridAdapter adapter = new EmotionGridAdapter(R.layout.item_emotion_item, emotionItem.getEmotionList());
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        rootView.setAdapter(adapter);
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mEmotionItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private class EmotionGridAdapter extends BaseQuickAdapter<Emotion> {

        public EmotionGridAdapter(int layoutResId, List<Emotion> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Emotion emotion) {
            int size = DensityUtil.dip2px(35);
//            int height = bitmap.getHeight();
            ViewGroup.LayoutParams params = helper.getConvertView().getLayoutParams();
            params.height = size;

            ImageView emotion_iv = helper.getView(R.id.emotion_iv);
            String value = emotion.getValue();
            try {
                Bitmap resultBitmap = null;
                Bitmap bitmap = ImageBitmapCache.getInstance().getBitmapFromMemCache(value);
                if (bitmap == null) {
                    InputStream inputStream = mContext.getAssets().open(value);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    resultBitmap = CommonUtils.zoomBitmap(bitmap, size);
                    ImageBitmapCache.getInstance().addBitmapToMemCache(value, resultBitmap);
                } else {
                    resultBitmap = CommonUtils.zoomBitmap(bitmap, size);
                }
                emotion_iv.setImageBitmap(resultBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class EmotionGridItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public EmotionGridItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
