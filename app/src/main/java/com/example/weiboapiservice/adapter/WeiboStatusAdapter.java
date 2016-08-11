package com.example.weiboapiservice.adapter;

import android.content.Context;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.model.WeiboPicture;
import com.example.weiboapiservice.model.WeiboStatus;
import com.example.weiboapiservice.model.WeiboUser;
import com.example.weiboapiservice.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.example.weiboapiservice.utils.ImageShowUtil;
import com.example.weiboapiservice.view.CircleImageView;

import java.util.List;

/**
 * Created by sqsong on 16-8-10.
 */
public class WeiboStatusAdapter extends BaseQuickAdapter<WeiboStatus> implements View.OnClickListener {

    private ImageShowUtil mImageShowUtil;
    private Context context;

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

    @Override
    protected void convert(BaseViewHolder helper, WeiboStatus weiboStatus) {
        WeiboUser weiboUser = weiboStatus.getUser();
        CircleImageView user_avatar_civ = helper.getView(R.id.user_avatar_civ);
        Glide.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header)
                .centerCrop().into(user_avatar_civ);
        helper.setText(R.id.user_name_tv, weiboUser.getName());
        helper.setText(R.id.status_publish_time_tv, weiboStatus.getCreated_at());
        helper.setText(R.id.status_publish_time_tv, Html.fromHtml(weiboStatus.getSource()));

        TextView status_content_tv = helper.getView(R.id.status_content_tv);
        status_content_tv.setText(weiboStatus.getSpannableText());
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
            helper.setText(R.id.forward_tv, repostsCount + "");
        } else {
            helper.setText(R.id.forward_tv, "转发");
        }

        if (commentsCount > 0) {
            helper.setText(R.id.comment_tv, commentsCount + "");
        } else {
            helper.setText(R.id.comment_tv, "评论");
        }
        if (attitudesCount > 0) {
            helper.setText(R.id.like_tv, attitudesCount + "");
        } else {
            helper.setText(R.id.like_tv, "赞");
        }

        LinearLayout forward_ll = helper.getView(R.id.forward_ll);
        forward_ll.setOnClickListener(this);
        LinearLayout comment_ll = helper.getView(R.id.comment_ll);
        forward_ll.setOnClickListener(this);
        LinearLayout like_ll = helper.getView(R.id.like_ll);
        forward_ll.setOnClickListener(this);

        LinearLayout forward_status_ll = helper.getView(R.id.forward_status_ll);
        WeiboStatus retweetedStatus = weiboStatus.getRetweeted_status();
        if (retweetedStatus != null) {
            forward_status_ll.setVisibility(View.VISIBLE);
            TextView forward_status_content_tv = helper.getView(R.id.forward_status_content_tv);
            forward_status_content_tv.setOnTouchListener(touchListener);
            forward_status_content_tv.setText(retweetedStatus.getSpannableText());
            LinearLayout forward_status_image_ll = helper.getView(R.id.forward_status_image_ll);
            List<WeiboPicture> retweetedPicUrls = retweetedStatus.getPic_urls();
            if (retweetedPicUrls != null && retweetedPicUrls.size() > 0) {
                forward_status_image_ll.setVisibility(View.VISIBLE);
                mImageShowUtil.showStatusImages(forward_status_image_ll, retweetedPicUrls);
            } else {
                forward_status_image_ll.setVisibility(View.GONE);
            }
        } else {
            forward_status_ll.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {

    }
}
