package com.modong.service.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
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
import com.modong.service.adapter.PublishPhotoGridAdapter;
import com.modong.service.databinding.ActivityPublishStatusBinding;
import com.modong.service.db.EmojiAssetDbHelper;
import com.modong.service.fragment.emotion.EmotionFragment;
import com.modong.service.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.modong.service.fragment.status.util.ImageBitmapCache;
import com.modong.service.fragment.status.util.WeiboPattern;
import com.modong.service.fragment.status.util.WeiboURLSpan;
import com.modong.service.model.Emotion;
import com.modong.service.model.EmotionItem;
import com.modong.service.utils.CommonUtils;
import com.modong.service.utils.DensityUtil;
import com.modong.service.view.GridSpacingItemDecoration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PublishStatusActivity extends BaseActivity implements View.OnClickListener,
        PublishPhotoGridAdapter.OnRecyclerViewItemClickListener, EmotionFragment.OnEmotionItemClickListener {

    public static final String SOFT_INPUT_HEIGHT = "soft_input_height";
    public static final int REQUEST_SELECT_PHOTO = 1;
    public static final int REQUEST_SELECT_MENTION = 2;

    private CompositeSubscription mCompositeSubscription;
    private ActivityPublishStatusBinding mBinding;
    private ArrayList<String> mSelectPhotos = new ArrayList<>();
    private List<EmotionItem> mEmotionItems = new ArrayList<>();
    private PublishPhotoGridAdapter mGridAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private EmojiAssetDbHelper dbHelper;
    private Vibrator vibrator;
    private InputMethodManager inputMethodManager;
    private EmotionCategoryPagerAdapter mPagerAdapter;
    public int mSoftInputHeight;

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
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mCompositeSubscription = new CompositeSubscription();

        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);
        mBinding.bottomLayout.publishImageIv.setOnClickListener(this);
        mBinding.bottomLayout.publishAltIv.setOnClickListener(this);
        mBinding.bottomLayout.publishTopicIv.setOnClickListener(this);
        mBinding.bottomLayout.publishEmojiIv.setOnClickListener(this);
        mBinding.bottomLayout.publishMoreIv.setOnClickListener(this);
        mBinding.bottomLayout.emotionDeleteIv.setOnClickListener(this);

        mPagerAdapter = new EmotionCategoryPagerAdapter(getSupportFragmentManager(), mFragments);
        mBinding.bottomLayout.emotionViewpager.setAdapter(mPagerAdapter);

        mBinding.inputEt.setOnTouchListener(touchListener);
        mBinding.inputEt.setFilters(new InputFilter[]{emotionFilter});

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

        mBinding.inputEt.post(new Runnable() {
            @Override
            public void run() {
                mSoftInputHeight = CommonUtils.getSupportSoftInputHeight(PublishStatusActivity.this);
            }
        });
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mBinding.bottomLayout.emotionLl.isShown()) {
                lockContentHeight();
                hideEmotionLayout(true);
                unlockContentHeightDelayed();
                mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_face);
            }
            return listener.onTouch(view, motionEvent);
        }
    };

    private InputFilter emotionFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 是delete直接返回
            if ("".equals(source)) {
                return null;
            }
            CharSequence result = source;

            if (result.toString().startsWith("@")) {
                SpannableString emotionSpanned = new SpannableString(result);
                Linkify.addLinks(emotionSpanned, WeiboPattern.MENTION_URL, WeiboPattern.MENTION_SCHEME);
                URLSpan[] urlSpans = emotionSpanned.getSpans(0, emotionSpanned.length(), URLSpan.class);
                WeiboURLSpan weiboSpan = null;
                for (URLSpan urlSpan : urlSpans) {
                    weiboSpan = new WeiboURLSpan(urlSpan.getURL());
                    int s = emotionSpanned.getSpanStart(urlSpan);
                    int e = emotionSpanned.getSpanEnd(urlSpan);
                    emotionSpanned.removeSpan(urlSpan);
                    emotionSpanned.setSpan(weiboSpan, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                result = emotionSpanned;
            }

            if (result.toString().startsWith("[")) {
                if (dbHelper == null) {
                    dbHelper = new EmojiAssetDbHelper(getApplicationContext());
                }
                String value = dbHelper.queryEmotionValue(EmojiAssetDbHelper.DB_EMOTION, source.toString());
                try {
                    Bitmap bitmap = ImageBitmapCache.getInstance().getBitmapFromMemCache(value);
                    if (bitmap == null) {
                        InputStream inputStream = getAssets().open(value);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        int size = DensityUtil.dip2px(20);
                        bitmap = CommonUtils.zoomBitmap(bitmap, size);
                        ImageBitmapCache.getInstance().addBitmapToMemCache(value, bitmap);
                        Log.i("sqsong", "Load New Bitmap! " + source.toString());
                    }
                    SpannableString emotionSpanned = new SpannableString(result);
                    ImageSpan span = new ImageSpan(getApplicationContext(), bitmap);
                    emotionSpanned.setSpan(span, 0, source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    result =  emotionSpanned;
                    Log.i("sqsong", "Input Result: " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

    };

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
                Intent altIntent = new Intent(this, FriendsActivity.class);
                startActivityForResult(altIntent, REQUEST_SELECT_MENTION);
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
                Toast.makeText(PublishStatusActivity.this, "更多功能,敬请期待!!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.emotion_delete_iv:
                deleteEmotionOrText();
                break;
        }
    }

    private void deleteEmotionOrText() {
        vibrator.vibrate(10);
//        CommonUtils.deleteFace(mBinding.inputEt );
        mBinding.inputEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    private void toggleEmojiVisiblity() {
        if (mBinding.bottomLayout.emotionLl.isShown()) {
            lockContentHeight();
            hideEmotionLayout(true);
            unlockContentHeightDelayed();
            mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_face);
        } else {
            if (isSoftInputShown()) {
                lockContentHeight();
                showEmotionLayout();
                unlockContentHeightDelayed();
                mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_keyboard);
            } else {
                showEmotionLayout();
            }
        }
    }

    private boolean isSoftInputShown() {
        return CommonUtils.getSupportSoftInputHeight(this) != 0;
    }


    private void showEmotionLayout() {
        mSoftInputHeight = CommonUtils.getSupportSoftInputHeight(this);
        if (mSoftInputHeight == 0) {
            mSoftInputHeight = mPreferenceHelper.getInt(SOFT_INPUT_HEIGHT, DensityUtil.dip2px(220));
        }
        showInputKeyBoard(false);
        mBinding.bottomLayout.emotionLl.getLayoutParams().height = mSoftInputHeight;
        mBinding.bottomLayout.emotionLl.setVisibility(View.VISIBLE);
    }

    private void hideEmotionLayout(boolean showSoftInput) {
        mBinding.bottomLayout.emotionLl.setVisibility(View.GONE);
        if (showSoftInput) {
            showInputKeyBoard(true);
        }
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.contentLl.getLayoutParams();
        params.height = mBinding.contentLl.getHeight();
        params.weight = 0;
    }

    private void unlockContentHeightDelayed() {
        mBinding.inputEt.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mBinding.contentLl.getLayoutParams()).weight = 1;
                mBinding.getRoot().requestLayout();
            }
        }, 200);
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

//    @Override
//    public void onEmotionClick(View view, Emotion emotion, int position) {
//        vibrator.vibrate(10);
//        String name = emotion.getName();
//        Editable editable = mBinding.inputEt.getEditableText();
//        int start = mBinding.inputEt.getSelectionStart();
//        editable.insert(start, name);
//    }

    @Override
    public void onEmotionItemClick(View view, Emotion emotion, int position) {
        vibrator.vibrate(10);
        if (emotion == null) {
            mBinding.inputEt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            String name = emotion.getName();
            Editable editable = mBinding.inputEt.getEditableText();
            int start = mBinding.inputEt.getSelectionStart();
            editable.insert(start, name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) return;
            if (requestCode == REQUEST_SELECT_PHOTO) {
                ArrayList<String> datas = data.getStringArrayListExtra(SelectPhotoActivity.DATA_SELECTED_PHOTO);
                mSelectPhotos.clear();
                mSelectPhotos.addAll(datas);
                mGridAdapter.notifyDataSetChanged();
            }
            if (requestCode == REQUEST_SELECT_MENTION) {
                String screenName = data.getStringExtra(FriendsActivity.MENTION_NAME);
                Editable editable = mBinding.inputEt.getEditableText();
                int start = mBinding.inputEt.getSelectionStart();
                editable.insert(start, "@" + screenName + " ");
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
//                    mEmotionPagerAdapter.notifyDataSetChanged();
                    setEmotionFragments(emotionItems);
                }
            }));
    }

    private void setEmotionFragments(List<EmotionItem> emotionItems) {
        if (emotionItems == null || emotionItems.size() <1) {
            return;
        }
        for (EmotionItem item : emotionItems) {
            mFragments.add(EmotionFragment.newInstance((ArrayList<Emotion>) item.getEmotionList()));
        }
        mPagerAdapter.notifyDataSetChanged();
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
//        int padding = DensityUtil.dip2px(10);
//        textView.setPadding(0, padding, 0, padding);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(80), ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        return textView;
    }

    private void showInputKeyBoard(boolean show) {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (show) {
            mBinding.inputEt.requestFocus();
            inputMethodManager.showSoftInput(mBinding.inputEt, InputMethodManager.SHOW_FORCED);
        } else {
            inputMethodManager.hideSoftInputFromWindow(mBinding.inputEt.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mBinding.bottomLayout.emotionLl.isShown()) {
                hideEmotionLayout(false);
                mBinding.bottomLayout.publishEmojiIv.setImageResource(R.drawable.selector_publish_face);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideEmotionLayout(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    public static class EmotionCategoryPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        public EmotionCategoryPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
