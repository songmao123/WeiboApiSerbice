package com.example.weiboapiservice.ui;

import android.text.Html;
import android.view.MotionEvent;
import android.view.View;

import com.example.weiboapiservice.R;
import com.example.weiboapiservice.databinding.ActivityStatusDetailBinding;
import com.example.weiboapiservice.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.example.weiboapiservice.fragment.status.util.TimeLineUtil;
import com.example.weiboapiservice.model.WeiboPicture;
import com.example.weiboapiservice.model.WeiboStatus;
import com.example.weiboapiservice.model.WeiboUser;
import com.example.weiboapiservice.utils.ImageShowUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 青松 on 2016/8/16.
 */
public class StatusDetailHelper {

    private StatusDetailActivity mContext;
    private ImageShowUtil mImageShowUtil;
    private View.OnClickListener listener;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return listener.onTouch(view, motionEvent);
        }
    };

    public StatusDetailHelper(StatusDetailActivity context, View.OnClickListener l) {
        this.mContext = context;
        this.mImageShowUtil = new ImageShowUtil(context);
        this.listener = l;
    }

    public void setStatusInfo(ActivityStatusDetailBinding binding, WeiboStatus weiboStatus) {
        WeiboUser weiboUser = weiboStatus.getUser();
        Picasso.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header).into(binding.userAvatarCiv);
        binding.userNameTv.setText(weiboUser.getName());
        binding.statusPublishTimeTv.setText(TimeLineUtil.getPublishTime(weiboStatus.getCreated_at()));
        binding.statusFromTv.setText(Html.fromHtml(weiboStatus.getSource()).toString());
        TimeLineUtil.setImageVerified(binding.verifiedIv, weiboUser);
        binding.statusContentTv.setText(weiboStatus.getSpannableText());
        binding.statusContentTv.setOnTouchListener(touchListener);

        List<WeiboPicture> picUrls = weiboStatus.getPic_urls();
        if (picUrls != null && picUrls.size() > 0) {
            mImageShowUtil.showStatusImages(binding.statusImageLl, picUrls);
            binding.statusImageLl.setVisibility(View.VISIBLE);
        } else {
            binding.statusImageLl.setVisibility(View.GONE);
        }

        WeiboStatus retweetedStatus = weiboStatus.getRetweeted_status();
        if (retweetedStatus != null) {
            binding.forwardStatusLl.setVisibility(View.VISIBLE);
            binding.forwardStatusContentTv.setOnTouchListener(touchListener);
            String text = retweetedStatus.getText();
            String content = "@" + retweetedStatus.getUser().getName() + ": " + text;
            binding.forwardStatusContentTv.setText(TimeLineUtil.convertNormalStringToSpannableString(content));
            List<WeiboPicture> retweetedPicUrls = retweetedStatus.getPic_urls();
            if (retweetedPicUrls != null && retweetedPicUrls.size() > 0) {
                binding.forwardStatusImageLl.setVisibility(View.VISIBLE);
                mImageShowUtil.showStatusImages(binding.forwardStatusImageLl, retweetedPicUrls);
            } else {
                binding.forwardStatusImageLl.setVisibility(View.GONE);
            }

            binding.forwardRepostTv.setOnClickListener(listener);
            binding.forwardCommentTv.setOnClickListener(listener);
            binding.forwardLikeTv.setOnClickListener(listener);

            int repostsCount = retweetedStatus.getReposts_count();
            int commentsCount = retweetedStatus.getComments_count();
            int attitudesCount = retweetedStatus.getAttitudes_count();
            if (repostsCount > 0) {
                binding.forwardRepostTv.setText(TimeLineUtil.getCounter(repostsCount));
            } else {
                binding.forwardRepostTv.setText("转发");
            }

            if (commentsCount > 0) {
                binding.forwardCommentTv.setText(TimeLineUtil.getCounter(commentsCount));
            } else {
                binding.forwardCommentTv.setText("评论");
            }
            if (attitudesCount > 0) {
                binding.forwardLikeTv.setText(TimeLineUtil.getCounter(attitudesCount));
            } else {
                binding.forwardLikeTv.setText("赞");
            }
            binding.forwardStatusLl.setOnClickListener(listener);
        } else {
            binding.forwardStatusLl.setVisibility(View.GONE);
        }
    }
}
