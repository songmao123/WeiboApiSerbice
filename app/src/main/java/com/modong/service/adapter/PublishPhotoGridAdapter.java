package com.modong.service.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.modong.service.R;
import com.modong.service.utils.DensityUtil;

import java.io.File;
import java.util.List;

/**
 * Created by 青松 on 2016/8/26.
 */
public class PublishPhotoGridAdapter extends RecyclerView.Adapter<PublishPhotoGridAdapter.GridViewHolder> {

    private Context mContext;
    private List<String> mPhotoList;
    private LayoutInflater mInflater;
    private int imageHeight;
    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View itemView, String data, int position);
    }

    public void setOnRecyclerViewItemClickListener (OnRecyclerViewItemClickListener l) {
        this.listener = l;
    }

    public PublishPhotoGridAdapter(Context context, List<String> mPhotoList) {
        this.mContext = context;
        this.mPhotoList = mPhotoList;
        this.mInflater = LayoutInflater.from(context);
        this.imageHeight = (DensityUtil.getScreenWidth() - DensityUtil.dip2px(23) * 2) / 3;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_publish_grid_image, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GridViewHolder holder, final int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = imageHeight;
        }

        final String data;
        if (position == mPhotoList.size()) {
            data = null;
            holder.delete_iv.setVisibility(View.GONE);
            holder.image_iv.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(R.drawable.compose_pic_add_highlighted).into(holder.image_iv);
        } else {
            String photo = mPhotoList.get(position);
            data = photo;
            holder.delete_iv.setVisibility(View.VISIBLE);
            holder.image_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext).load(new File(photo)).into(holder.image_iv);
            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    mPhotoList.remove(pos);
                    if (mPhotoList.size() > 0) {
                        notifyItemRemoved(pos);
                    } else {
                        notifyDataSetChanged();
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(view, data, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mPhotoList.size() > 0 && mPhotoList.size() < 9) ? (mPhotoList.size() + 1) : mPhotoList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView image_iv;
        ImageView delete_iv;

        public GridViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.image_iv = (ImageView) itemView.findViewById(R.id.image_iv);
            this.delete_iv = (ImageView) itemView.findViewById(R.id.delete_iv);
        }

    }
}
