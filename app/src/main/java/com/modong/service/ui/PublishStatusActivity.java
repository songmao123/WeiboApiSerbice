package com.modong.service.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.adapter.EmotionPagerAdapter;
import com.modong.service.adapter.PublishPhotoGridAdapter;
import com.modong.service.databinding.ActivityPublishStatusBinding;
import com.modong.service.db.EmojiAssetDbHelper;
import com.modong.service.model.Emotion;
import com.modong.service.model.EmotionItem;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PublishStatusActivity extends BaseActivity implements View.OnClickListener, PublishPhotoGridAdapter.OnRecyclerViewItemClickListener {

    public static final int REQUEST_SELECT_PHOTO = 1;
    private CompositeSubscription mCompositeSubscription;
    private ActivityPublishStatusBinding mBinding;
    private ArrayList<String> mSelectPhotos = new ArrayList<>();
    private List<EmotionItem> mEmotionItems = new ArrayList<>();
    private PublishPhotoGridAdapter mGridAdapter;
    private EmotionPagerAdapter mEmotionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_status);

        getIntentParams();
        initRecyclerView();
        initEvents();

        initEmotionDatas();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> photos = intent.getStringArrayListExtra(SelectPhotoActivity.DATA_SELECTED_PHOTO);
            if (photos != null && photos.size() > 0) {
                mSelectPhotos.clear();
                mSelectPhotos.addAll(photos);
            }
        }
    }

    private void initRecyclerView() {
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, DensityUtil.dip2px(3), true, true);
        mBinding.recyclerView.addItemDecoration(decoration);
        mGridAdapter = new PublishPhotoGridAdapter(this, mSelectPhotos);
        mGridAdapter.setOnRecyclerViewItemClickListener(this);
        mBinding.recyclerView.setAdapter(mGridAdapter);
    }

    @Override
    public void initEvents() {
        mCompositeSubscription = new CompositeSubscription();

        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);
        mBinding.bottomLayout.publishImageIv.setOnClickListener(this);
        mBinding.bottomLayout.publishAltIv.setOnClickListener(this);
        mBinding.bottomLayout.publishTopicIv.setOnClickListener(this);
        mBinding.bottomLayout.publishEmojiIv.setOnClickListener(this);
        mBinding.bottomLayout.publishMoreIv.setOnClickListener(this);
        mBinding.bottomLayout.emotionDeleteIv.setOnClickListener(this);

        mEmotionPagerAdapter = new EmotionPagerAdapter(this, mEmotionItems);
        mBinding.bottomLayout.emotionViewpager.setAdapter(mEmotionPagerAdapter);

        mBinding.inputEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mBinding.bottomLayout.emotionLl.getVisibility() == View.VISIBLE) {
                    mBinding.bottomLayout.emotionLl.setVisibility(View.GONE);
                    mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_face);
                }
                return false;
            }
        });

        mBinding.bottomLayout.emotionViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                int count = mBinding.bottomLayout.emotionTabLl.getChildCount();
                if (count < 1) return;
                for (int i=0; i<count; i++) {
                    TextView textView = (TextView) mBinding.bottomLayout.emotionTabLl.getChildAt(i);
                    if (i == position) {
                        textView.setBackgroundColor(getResources().getColor(R.color.color_DFDFDF));
                        textView.setTextColor(getResources().getColor(R.color.color_646464));
                    } else {
                        textView.setBackgroundColor(getResources().getColor(R.color.color_F1F1F1));
                        textView.setTextColor(getResources().getColor(R.color.color_BDBDBD));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow_iv:
                finish();
                break;
            case R.id.next_tv:

                break;
            case R.id.publish_image_iv:
                Intent intent = new Intent(this, SelectPhotoActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_PHOTO);
                break;
            case R.id.publish_alt_iv:

                break;
            case R.id.publish_topic_iv:
                String text = mBinding.inputEt.getText().toString();
                mBinding.inputEt.setText(text + "##");
                mBinding.inputEt.setSelection(mBinding.inputEt.getText().length() - 1);
                break;
            case R.id.publish_emoji_iv:
                toggleEmojiVisiblity();
                break;
            case R.id.publish_more_iv:
                Toast.makeText(PublishStatusActivity.this, "More Function!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.emotion_delete_iv:

                break;
        }
    }

    private void toggleEmojiVisiblity() {
        final LinearLayout emotionLl = mBinding.bottomLayout.emotionLl;
        if (emotionLl.getVisibility() == View.GONE) {
            showInputKeyBoard(false);
            mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_keyboard);
            emotionLl.postDelayed(new Runnable() {
                @Override
                public void run() {
                    emotionLl.setVisibility(View.VISIBLE);
                }
            }, 200);
        } else {
            emotionLl.setVisibility(View.GONE);
            showInputKeyBoard(true);
            mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_face);
        }
    }

    @Override
    public void onItemClick(View itemView, String data, int position) {
        Intent intent;
        if (data == null || position == mSelectPhotos.size()) {
            intent = new Intent(this, SelectPhotoActivity.class);
            intent.putStringArrayListExtra(SelectPhotoActivity.DATA_SELECTED_PHOTO, mSelectPhotos);
        } else {
            intent = new Intent(this, PhotoSelectPreviewActivity.class);
            intent.putStringArrayListExtra(PhotoSelectPreviewActivity.ALL_IMAGES, mSelectPhotos);
            intent.putStringArrayListExtra(PhotoSelectPreviewActivity.SELECTED_IMAGES, mSelectPhotos);
            intent.putExtra(PhotoSelectPreviewActivity.CURRENT_POSITION, position);
            intent.putExtra(PhotoSelectPreviewActivity.FROM_PUBLISH_DIRECT, true);
        }
        startActivityForResult(intent, REQUEST_SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PHOTO) {
                if (data == null) return;
                ArrayList<String> datas = data.getStringArrayListExtra(SelectPhotoActivity.DATA_SELECTED_PHOTO);
                mSelectPhotos.clear();
                mSelectPhotos.addAll(datas);
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initEmotionDatas() {
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<List<EmotionItem>>() {
            @Override
            public void call(Subscriber<? super List<EmotionItem>> subscriber) {
                EmojiAssetDbHelper dbHelper = new EmojiAssetDbHelper(getApplicationContext());
                List<EmotionItem> emotionItemList = dbHelper.queryEmotionList(EmojiAssetDbHelper.DB_EMOTION);
                subscriber.onNext(emotionItemList);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<EmotionItem>>() {
                @Override
                public void call(List<EmotionItem> emotionItems) {
                    mEmotionItems.clear();
                    mEmotionItems.addAll(emotionItems);
                    addBottomTabNav(emotionItems);
                    mEmotionPagerAdapter.notifyDataSetChanged();
                }
            }));
    }

    private void addBottomTabNav(List<EmotionItem> emotionItems) {
        for (int i=0; i<emotionItems.size(); i++) {
            EmotionItem item = emotionItems.get(i);
            TextView textView = getBottomNavText(i);
            Emotion emotion = item.getEmotionList().get(0);
            textView.setText(emotion.getDescription());
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBinding.bottomLayout.emotionViewpager.setCurrentItem(finalI);
                }
            });
            mBinding.bottomLayout.emotionTabLl.addView(textView);
        }
    }

    private TextView getBottomNavText(int index) {
        TextView textView = new TextView(this);
        if (index == 0) {
            textView.setBackgroundColor(getResources().getColor(R.color.color_DFDFDF));
            textView.setTextColor(getResources().getColor(R.color.color_646464));
        } else {
            textView.setBackgroundColor(getResources().getColor(R.color.color_F1F1F1));
            textView.setTextColor(getResources().getColor(R.color.color_BDBDBD));
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setGravity(Gravity.CENTER);
        int padding = DensityUtil.dip2px(10);
        textView.setPadding(0, padding, 0, padding);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(80), ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        return textView;
    }

    private void showInputKeyBoard(boolean show) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            inputMethodManager.showSoftInput(mBinding.inputEt, InputMethodManager.SHOW_FORCED);
        } else {
            inputMethodManager.hideSoftInputFromWindow(mBinding.inputEt.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mBinding.bottomLayout.emotionLl.getVisibility() == View.VISIBLE) {
                mBinding.bottomLayout.emotionLl.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
