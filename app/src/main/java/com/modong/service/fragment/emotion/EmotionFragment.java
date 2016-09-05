package com.modong.service.fragment.emotion;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.modong.service.R;
import com.modong.service.adapter.EmotionGridListAdapter;
import com.modong.service.databinding.FragmentEmotionBinding;
import com.modong.service.model.Emotion;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.CirclePagerIndicator;

import java.util.ArrayList;
import java.util.List;

public class EmotionFragment extends Fragment implements EmotionGridListAdapter.OnRecyclerViewItemClickListener {

    private static final String EMOTION_LIST = "emotion_list";

    private ArrayList<Emotion> mEmotionList;
    private FragmentEmotionBinding mBinding;
    private CirclePagerIndicator mIndicator;
    private OnEmotionItemClickListener listener;

    public interface OnEmotionItemClickListener {
        void onEmotionItemClick(View view, Emotion emotion, int position);
    }

    public static EmotionFragment newInstance(ArrayList<Emotion> emotionList) {
        EmotionFragment fragment = new EmotionFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EMOTION_LIST, emotionList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmotionList = getArguments().getParcelableArrayList(EMOTION_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_emotion, container, false);

        initEmotions();
        return mBinding.getRoot();
    }

    private void initEmotions() {
        List<RecyclerView> recyclerViewList = new ArrayList<>();
        List<Emotion> emotionList = new ArrayList<>();
        for (Emotion emotion : mEmotionList) {
            emotionList.add(emotion);
            if (emotionList.size() == 20) {
                RecyclerView emotionRecyclerView = createEmotionRecycler(emotionList);
                recyclerViewList.add(emotionRecyclerView);
                emotionList = new ArrayList<>();
            }
        }

        if (emotionList.size() > 0) {
            RecyclerView emotionRecyclerView = createEmotionRecycler(emotionList);
            recyclerViewList.add(emotionRecyclerView);
        }

        EmotionPagerAdapter emotionPagerAdapter = new EmotionPagerAdapter(recyclerViewList);
        mBinding.viewpager.setAdapter(emotionPagerAdapter);

        mIndicator = new CirclePagerIndicator(getActivity());
        mIndicator.setCircleCount(recyclerViewList.size());
        mIndicator.setNormalColor(getResources().getColor(R.color.color_E4E4E4));
        mIndicator.setFocusColor(getResources().getColor(R.color.color_999999));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mBinding.dotLl.addView(mIndicator, lp);

        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private RecyclerView createEmotionRecycler(List<Emotion> emotionList) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        int paddingTB = DensityUtil.dip2px(15);
        recyclerView.setPadding(paddingTB, paddingTB, paddingTB, paddingTB);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        EmotionGridItemDecoration decoration = new EmotionGridItemDecoration(7, DensityUtil.dip2px(23), false);
        recyclerView.addItemDecoration(decoration);
        EmotionGridListAdapter adapter = new EmotionGridListAdapter(getActivity(), emotionList, this);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public void onItemClick(View view, Emotion emotion, int position) {
        if (listener != null) {
            listener.onEmotionItemClick(view, emotion, position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmotionItemClickListener) {
            listener = (OnEmotionItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static class EmotionGridItemDecoration extends RecyclerView.ItemDecoration {
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

    public static class EmotionPagerAdapter extends PagerAdapter {

        private List<RecyclerView> emotionRecyclerView;

        public EmotionPagerAdapter(List<RecyclerView> emotionRecyclerView) {
            this.emotionRecyclerView = emotionRecyclerView;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RecyclerView recyclerView = emotionRecyclerView.get(position);
            container.addView(recyclerView);
            return recyclerView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RecyclerView)object);
        }

        @Override
        public int getCount() {
            return emotionRecyclerView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
