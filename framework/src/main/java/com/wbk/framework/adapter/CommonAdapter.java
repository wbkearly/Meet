package com.wbk.framework.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private List<T> mList;

    private OnBindDataListener<T> mOnBindDataListener;

    private OnBindMultiTypeDataListener<T> mOnBindMultiTypeDataListener;

    public interface OnBindDataListener<T> {
        void onBindViewHolder(T model, CommonViewHolder viewHolder, int type, int position);

        int getLayoutId(int type);
    }

    public interface OnBindMultiTypeDataListener<T> extends OnBindDataListener<T> {
        int getItemType(int position);
    }

    public CommonAdapter(List<T> list, OnBindDataListener<T> onBindDataListener) {
        mList = list;
        mOnBindDataListener = onBindDataListener;
    }

    public CommonAdapter(List<T> list, OnBindMultiTypeDataListener<T> onBindMultiTypeDataListener) {
        mList = list;
        mOnBindDataListener = onBindMultiTypeDataListener;
        mOnBindMultiTypeDataListener = onBindMultiTypeDataListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mOnBindMultiTypeDataListener != null) {
            return mOnBindMultiTypeDataListener.getItemType(position);
        }
        return 0;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = mOnBindDataListener.getLayoutId(viewType);
        return CommonViewHolder.getViewHolder(parent, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        mOnBindDataListener.onBindViewHolder(mList.get(position), holder, getItemViewType(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
