package com.wbk.framework.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.helper.GlideHelper;

public class CommonViewHolder extends RecyclerView.ViewHolder {

    //子View集合
    private SparseArray<View> mViews;
    private View mContentView;

    public CommonViewHolder(@NonNull View itemView) {
        super(itemView);
        mViews =new SparseArray<>();
        mContentView = itemView;
    }

    public static CommonViewHolder getViewHolder(ViewGroup parent, int layoutId) {
        return new CommonViewHolder(View.inflate(parent.getContext(), layoutId, null));
    }

    /**
     * 提供给外部访问view
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public CommonViewHolder setVisibility(int viewId, int visibility) {
        TextView textView = getView(viewId);
        textView.setVisibility(visibility);
        return this;
    }

    public CommonViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public CommonViewHolder setImageUrl(Context context, int viewId, String url) {
        ImageView imageView = getView(viewId);
        GlideHelper.loadUrl(context, url,imageView);
        return this;
    }

    public CommonViewHolder setImageResource(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    public CommonViewHolder setBgColor(int viewId, int color) {
        TextView textView = getView(viewId);
        textView.setBackgroundColor(color);
        return this;
    }
}
