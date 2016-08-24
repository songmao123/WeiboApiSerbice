package com.modong.service.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.databinding.ActivityPublishStatusBinding;

public class PublishStatusActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPublishStatusBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish_status);

        initEvents();
    }

    @Override
    public void initEvents() {
        mBinding.toolbarInclude.backArrowIv.setOnClickListener(this);
        mBinding.toolbarInclude.nextTv.setOnClickListener(this);
        mBinding.bottomLayout.publishImageIv.setOnClickListener(this);
        mBinding.bottomLayout.publishAltIv.setOnClickListener(this);
        mBinding.bottomLayout.publishTopicIv.setOnClickListener(this);
        mBinding.bottomLayout.publishEmojiIv.setOnClickListener(this);
        mBinding.bottomLayout.publishMoreIv.setOnClickListener(this);
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

                break;
            case R.id.publish_alt_iv:

                break;
            case R.id.publish_topic_iv:

                break;
            case R.id.publish_emoji_iv:

                break;
            case R.id.publish_more_iv:
                Toast.makeText(PublishStatusActivity.this, "More Function!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
