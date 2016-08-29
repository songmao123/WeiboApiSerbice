package com.modong.service.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.modong.service.R;
import com.modong.service.fragment.status.util.TimeLineUtil;
import com.modong.service.model.WeiboUser;
import com.modong.service.view.CircleImageView;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by 青松 on 2016/8/29.
 */
public class FriendsOrderListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyRecyclerHeadersAdapter<FriendsOrderListAdapter.FriendItemHeaderViewHolder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private Context mContext;
    private ArrayList<WeiboUser> mUsers;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onRecyclerViewHeaderClick(View view);
        void onRecyclerViewItemClick(View view, WeiboUser user, int position);
    }

    public void setOnRecyclerViewItemClickListener (OnRecyclerViewItemClickListener l) {
        this.listener = l;
    }

    public FriendsOrderListAdapter(Context context, ArrayList<WeiboUser> users) {
        this.mContext = context;
        this.mUsers = users;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.header_search, parent, false);
            return new RecyclerView.ViewHolder(headerView) { };
        } else {
            View view = inflater.inflate(R.layout.item_friend, parent, false);
            return new FriendItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRecyclerViewHeaderClick(view);
                    }
                }
            });
        } else {
            FriendItemViewHolder itemViewHolder = (FriendItemViewHolder) holder;
            final WeiboUser weiboUser = mUsers.get(position - 1);
            Picasso.with(mContext).load(weiboUser.getAvatar_large()).placeholder(R.drawable.header).into(itemViewHolder.user_avatar_civ);
            TimeLineUtil.setImageVerified(itemViewHolder.verified_iv, weiboUser);
            itemViewHolder.user_name_tv.setText(weiboUser.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRecyclerViewItemClick(view, weiboUser, position - 1);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public String getItem(int position) {
        if (position == 0) {
            return "";
        } else {
            return mUsers.get(position).getPinyin();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public FriendItemHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_friend_header, parent, false);
        return new FriendItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(FriendItemHeaderViewHolder holder, int position) {
        if (position == 0) {

        } else {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.header_text.setText(String.valueOf(mUsers.get(position - 1).getPinyin().toUpperCase().charAt(0)));
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0) {
            return -1;
        } else {
            return mUsers.get(position).getPinyin().toUpperCase().charAt(0);
        }
    }

    public class FriendItemViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_avatar_civ;
        ImageView verified_iv;
        TextView user_name_tv;

        public FriendItemViewHolder(View itemView) {
            super(itemView);
            user_avatar_civ = (CircleImageView) itemView.findViewById(R.id.user_avatar_civ);
            verified_iv = (ImageView) itemView.findViewById(R.id.verified_iv);
            user_name_tv = (TextView) itemView.findViewById(R.id.user_name_tv);
        }

    }

    public class FriendItemHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView header_text;
        View itemView;

        public FriendItemHeaderViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            header_text = (TextView) itemView.findViewById(R.id.header_text);
        }
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

}
