package com.modong.service.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.modong.service.R;
import com.modong.service.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.modong.service.fragment.status.util.TimeLineUtil;
import com.modong.service.model.WeiboPicture;
import com.modong.service.model.WeiboStatus;
import com.modong.service.model.WeiboUser;
import com.modong.service.ui.StatusDetailActivity;
import com.modong.service.ui.UserInfoActivity;
import com.modong.service.utils.ImageShowUtil;
import com.modong.service.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sqsong on 16-8-10.
 */
public class WeiboStatusAdapter extends BaseQuickAdapter<WeiboStatus> implements View.OnClickListener {

    private ImageShowUtil mImageShowUtil;
    private Context context;
    private boolean isUserInfo;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return listener.onTouch(view, motionEvent);
        }
    };

    public WeiboStatusAdapter(Context context, int layoutResId, List<WeiboStatus> data) {
        super(layoutResId, data);
        this.mImageShowUtil = new ImageShowUtil(context);
        this.context = context;
    }

    public void setIsUserInfo(boolean isUserInfo) {
        this.isUserInfo = isUserInfo;
    }

    @Override
    protected void convert(BaseViewHolder helper, final WeiboStatus weiboStatus) {
        WeiboUser weiboUser = weiboStatus.getUser();
        if (isUserInfo) {
            helper.setVisible(R.id.avatar_rl, false);
        } else {
            helper.setVisible(R.id.avatar_rl, true);
            CircleImageView user_avatar_civ = helper.getView(R.id.user_avatar_civ);
            user_avatar_civ.setOnClickListener(this);
            user_avatar_civ.setTag(weiboUser);
            Picasso.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header).into(user_avatar_civ);
        }

        TextView user_name_tv = helper.getView(R.id.user_name_tv);
        user_name_tv.setOnClickListener(this);
        user_name_tv.setTag(weiboUser);
        helper.setText(R.id.user_name_tv, weiboUser.getName());

        helper.setText(R.id.status_publish_time_tv, TimeLineUtil.getPublishTime(weiboStatus.getCreated_at()));
        helper.setText(R.id.status_from_tv, Html.fromHtml(weiboStatus.getSource()).toString());
        ImageView verified_iv = helper.getView(R.id.verified_iv);
        TimeLineUtil.setImageVerified(verified_iv, weiboUser);

        TextView status_content_tv = helper.getView(R.id.status_content_tv);
        if (weiboStatus.getSpannableText() != null) {
            status_content_tv.setText(weiboStatus.getSpannableText());
        } else {
            TimeLineUtil.setSpannableText(status_content_tv, weiboStatus, null);
        }
        status_content_tv.setOnTouchListener(touchListener);

        LinearLayout status_image_ll = helper.getView(R.id.status_image_ll);
        List<WeiboPicture> picUrls = weiboStatus.getPic_urls();
        if (picUrls != null && picUrls.size() > 0) {
            mImageShowUtil.showStatusImages(status_image_ll, picUrls);
            status_image_ll.setVisibility(View.VISIBLE);
        } else {
            status_image_ll.setVisibility(View.GONE);
        }

        int repostsCount = weiboStatus.getReposts_count();
        int commentsCount = weiboStatus.getComments_count();
        int attitudesCount = weiboStatus.getAttitudes_count();
        if (repostsCount > 0) {
            helper.setText(R.id.forward_tv,  TimeLineUtil.getCounter(repostsCount));
        } else {
            helper.setText(R.id.forward_tv, "转发");
        }

        if (commentsCount > 0) {
            helper.setText(R.id.comment_tv, TimeLineUtil.getCounter(commentsCount));
        } else {
            helper.setText(R.id.comment_tv, "评论");
        }
        if (attitudesCount > 0) {
            helper.setText(R.id.like_tv, TimeLineUtil.getCounter(attitudesCount));
        } else {
            helper.setText(R.id.like_tv, "赞");
        }

        LinearLayout forward_ll = helper.getView(R.id.forward_ll);
        forward_ll.setOnClickListener(this);
        LinearLayout comment_ll = helper.getView(R.id.comment_ll);
        comment_ll.setOnClickListener(this);
        LinearLayout like_ll = helper.getView(R.id.like_ll);
        like_ll.setOnClickListener(this);
        comment_ll.setTag(weiboStatus);

        LinearLayout forward_status_ll = helper.getView(R.id.forward_status_ll);
        final WeiboStatus retweetedStatus = weiboStatus.getRetweeted_status();
        if (retweetedStatus != null) {
            forward_status_ll.setVisibility(View.VISIBLE);
            TextView forward_status_content_tv = helper.getView(R.id.forward_status_content_tv);
            forward_status_content_tv.setOnTouchListener(touchListener);
            String text = retweetedStatus.getText();

            WeiboUser retweetedStatusUser = retweetedStatus.getUser();
            String content;
            if (retweetedStatusUser != null) {
                content = "@" + retweetedStatusUser.getScreen_name() + ": " + text;
            } else {
                content = text;
            }
            forward_status_content_tv.setText(TimeLineUtil.convertNormalStringToSpannableString(content));
            LinearLayout forward_status_image_ll = helper.getView(R.id.forward_status_image_ll);
            List<WeiboPicture> retweetedPicUrls = retweetedStatus.getPic_urls();
            if (retweetedPicUrls != null && retweetedPicUrls.size() > 0) {
                forward_status_image_ll.setVisibility(View.VISIBLE);
                mImageShowUtil.showStatusImages(forward_status_image_ll, retweetedPicUrls);
            } else {
                forward_status_image_ll.setVisibility(View.GONE);
            }
            forward_status_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, StatusDetailActivity.class);
                    intent.putExtra(StatusDetailActivity.STATUS_INFO, retweetedStatus);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        } else {
            forward_status_ll.setVisibility(View.GONE);
            forward_status_ll.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_avatar_civ:
            case R.id.user_name_tv:
                WeiboUser weiboUser = (WeiboUser) view.getTag();
                UserInfoActivity.lunchUserInfoActivity(context, null, weiboUser);
                break;
            case R.id.forward_ll:

                break;
            case R.id.comment_ll:
                WeiboStatus weiboStatus = (WeiboStatus) view.getTag();
                Intent intent = new Intent(view.getContext(), StatusDetailActivity.class);
                intent.putExtra(StatusDetailActivity.STATUS_INFO, weiboStatus);
                intent.putExtra(StatusDetailActivity.IS_SCROLL_TO_NAV, true);
                view.getContext().startActivity(intent);
                break;
            case R.id.like_ll:

                break;
        }
    }
}
