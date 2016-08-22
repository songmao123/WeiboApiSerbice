package com.modong.service.ui;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.modong.service.BaseActivity;
import com.modong.service.R;
import com.modong.service.databinding.ActivityStatusDetailBinding;
import com.modong.service.databinding.ActivityUserInfoBinding;
import com.modong.service.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.modong.service.fragment.status.util.TimeLineUtil;
import com.modong.service.model.WeiboPicture;
import com.modong.service.model.WeiboStatus;
import com.modong.service.model.WeiboUser;
import com.modong.service.utils.ImageShowUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 青松 on 2016/8/16.
 * 数据填充帮助类
 */
public class DataInflateHelper {

    private BaseActivity mContext;
    private ImageShowUtil mImageShowUtil;
    private View.OnClickListener listener;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return listener.onTouch(view, motionEvent);
        }
    };

    public DataInflateHelper(BaseActivity context, View.OnClickListener l) {
        this.mContext = context;
        this.listener = l;
        if (context instanceof StatusDetailActivity) {
            this.mImageShowUtil = new ImageShowUtil(context);
        }
    }

    public void setStatusInfo(ActivityStatusDetailBinding binding, WeiboStatus weiboStatus, TimeLineUtil.OnSpannableTextFinishListener l) {
        WeiboUser weiboUser = weiboStatus.getUser();
        Picasso.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header).into(binding.userAvatarCiv);
        binding.userNameTv.setText(weiboUser.getName());
        binding.statusPublishTimeTv.setText(TimeLineUtil.getPublishTime(weiboStatus.getCreated_at()));
        binding.statusFromTv.setText(Html.fromHtml(weiboStatus.getSource()).toString());
        TimeLineUtil.setImageVerified(binding.verifiedIv, weiboUser);
        SpannableString spannableText = weiboStatus.getSpannableText();
        if (spannableText != null) {
            binding.statusContentTv.setText(weiboStatus.getSpannableText());
        } else {
            TimeLineUtil.setSpannableText(binding.statusContentTv, weiboStatus, l);
        }
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

    public void setUserInfos(ActivityUserInfoBinding binding, WeiboUser weiboUser) {
        String backgroundImage = weiboUser.getCover_image_phone();
        Glide.with(mContext).load(backgroundImage).placeholder(R.drawable.background)
                .error(R.drawable.back).into(binding.backgroundImageIv);
        Picasso.with(mContext).load(weiboUser.getAvatar_large()).error(R.drawable.ic_account_circle_white).into(binding.userAvatarCiv);
        binding.userNameTv.setText(weiboUser.getName());
        binding.genderIv.setImageResource(TimeLineUtil.getUserGenderResource(weiboUser.getGender()));
        binding.followingLl.setOnClickListener(listener);
        binding.fansLl.setOnClickListener(listener);
        binding.followingCountTv.setText(weiboUser.getFriends_count() + "");
        binding.fansCountTv.setText(weiboUser.getFollowers_count() + "");
        String verifiedReason = weiboUser.getVerified_reason();
        if (!TextUtils.isEmpty(verifiedReason)) {
            binding.descriptionTv.setText("微博认证:" + verifiedReason);
        } else {
            binding.descriptionTv.setText("简介:" + weiboUser.getDescription());
        }
    }
}
