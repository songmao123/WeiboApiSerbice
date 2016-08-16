package com.example.weiboapiservice.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.weiboapiservice.R;
import com.example.weiboapiservice.fragment.status.util.ClickableTextViewMentionLinkOnTouchListener;
import com.example.weiboapiservice.fragment.status.util.TimeLineUtil;
import com.example.weiboapiservice.model.WeiboComment;
import com.example.weiboapiservice.model.WeiboUser;
import com.example.weiboapiservice.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 青松 on 2016/8/16.
 */
public class CommentListAdapter extends BaseQuickAdapter<WeiboComment> {

    private Context context;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return listener.onTouch(view, motionEvent);
        }
    };

    public CommentListAdapter(Context context, int layoutResId, List<WeiboComment> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, WeiboComment weiboComment) {
        WeiboUser weiboUser = weiboComment.getUser();
        CircleImageView user_avatar_civ = helper.getView(R.id.user_avatar_civ);
        Picasso.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header).into(user_avatar_civ);
        helper.setText(R.id.user_name_tv, weiboUser.getName());
        helper.setText(R.id.comment_time_tv, TimeLineUtil.getPublishTime(weiboComment.getCreated_at()));
        ImageView verified_iv = helper.getView(R.id.verified_iv);
        TimeLineUtil.setImageVerified(verified_iv, weiboUser);

        TextView status_content_tv = helper.getView(R.id.comment_content_tv);
        status_content_tv.setText(weiboComment.getSpannableText());
        status_content_tv.setOnTouchListener(touchListener);
    }
}
