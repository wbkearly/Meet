package com.wbk.meet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxun.tagcloudlib.view.TagsAdapter;
import com.wbk.meet.R;

import java.util.List;

public class CloudTagAdapter extends TagsAdapter {

    private List<String> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public CloudTagAdapter(List<String> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_star_view_item, null);
        ImageView ivStar = view.findViewById(R.id.iv_star_icon);
        TextView tvStar = view.findViewById(R.id.tv_star_name);
        tvStar.setText(mList.get(position));
        ivStar.setImageResource(R.drawable.img_star_icon);
        return view;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }

}
