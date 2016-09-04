package com.modong.service.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.modong.service.R;
import com.modong.service.fragment.status.util.ImageBitmapCache;
import com.modong.service.model.Emotion;
import com.modong.service.utils.CommonUtils;
import com.modong.service.utils.DensityUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by sqsong on 16-9-3.
 */
public class EmotionGridListAdapter extends RecyclerView.Adapter<EmotionGridListAdapter.EmotionItemViewHolder> {

    private Context context;
    private List<Emotion> emotionList;

    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Emotion emotion, int position);
    }

    public EmotionGridListAdapter(Context context, List<Emotion> emotionList, OnRecyclerViewItemClickListener l) {
        this.context = context;
        this.emotionList = emotionList;
        this.listener = l;
    }

    @Override
    public EmotionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emotion_item, parent, false);
        return new EmotionItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EmotionItemViewHolder holder, int position) {
        int size = DensityUtil.dip2px(35);
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = size;

        Emotion emotion = null;
        if (position == emotionList.size()) {
            holder.emotion_iv.setImageResource(R.drawable.selector_emotion_delete);
        } else {
            emotion = emotionList.get(position);
            String value = emotion.getValue();
            try {
                Bitmap resultBitmap = null;
                Bitmap bitmap = ImageBitmapCache.getInstance().getBitmapFromMemCache(value);
                if (bitmap == null) {
                    InputStream inputStream = context.getAssets().open(value);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    resultBitmap = CommonUtils.zoomBitmap(bitmap, size);
                    ImageBitmapCache.getInstance().addBitmapToMemCache(value, resultBitmap);
                } else {
                    resultBitmap = CommonUtils.zoomBitmap(bitmap, size);
                }
                holder.emotion_iv.setImageBitmap(resultBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final Emotion e = emotion;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(view, e, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return emotionList.size() + 1;
    }

    public class EmotionItemViewHolder extends RecyclerView.ViewHolder {

        ImageView emotion_iv;
        View itemView;

        public EmotionItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.emotion_iv = (ImageView) itemView.findViewById(R.id.emotion_iv);
        }
    }
}
