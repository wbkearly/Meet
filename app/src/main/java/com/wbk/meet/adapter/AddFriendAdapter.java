package com.wbk.meet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wbk.framework.helper.GlideHelper;
import com.wbk.meet.R;
import com.wbk.meet.mdoel.AddFriendModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 标题
    public static final int TYPE_TITLE = 0;
    // 内容
    public static final int TYPE_CONTENT = 1;

    private Context mContext;
    private List<AddFriendModel> mList;
    private LayoutInflater mInflater;
    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public AddFriendAdapter(Context context, List<AddFriendModel> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(mInflater.inflate(R.layout.layout_search_title_item, null));
        } else if (viewType == TYPE_CONTENT) {
            return new ContentViewHolder(mInflater.inflate(R.layout.layout_search_user_item, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AddFriendModel model = mList.get(position);
        if (model.getType() == TYPE_TITLE) {
            ((TitleViewHolder) holder).mTvTitle.setText(model.getTitle());
        } else if (model.getType() == TYPE_CONTENT) {
            GlideHelper.loadUrl(mContext, model.getPortrait(), ((ContentViewHolder) holder).mIvPortrait);
            ((ContentViewHolder) holder).mIvGender.setImageResource(model.isGender() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
            ((ContentViewHolder) holder).mTvNickname.setText(model.getNickname());
            ((ContentViewHolder) holder).mTvAge.setText(model.getAge() + "岁");
            ((ContentViewHolder) holder).mTvDesc.setText(model.getDesc());

            if (model.isContact()) {
                ((ContentViewHolder) holder).mLlContactInfo.setVisibility(View.GONE);
                ((ContentViewHolder) holder).mTvContactName.setText(model.getContactName());
                ((ContentViewHolder) holder).mTvContactPhone.setText(model.getContactPhone());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;

        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mIvPortrait;
        private ImageView mIvGender;
        private TextView mTvNickname;
        private TextView mTvAge;
        private TextView mTvDesc;
        private LinearLayout mLlContactInfo;
        private TextView mTvContactName;
        private TextView mTvContactPhone;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvPortrait = itemView.findViewById(R.id.iv_portrait);
            mIvGender = itemView.findViewById(R.id.iv_gender);
            mTvNickname = itemView.findViewById(R.id.tv_nickname);
            mTvAge = itemView.findViewById(R.id.tv_age);
            mTvDesc = itemView.findViewById(R.id.tv_desc);
            mLlContactInfo =  itemView.findViewById(R.id.ll_contact_info);
            mTvContactName =  itemView.findViewById(R.id.tv_contact_name);
            mTvContactPhone = itemView.findViewById(R.id.tv_contact_phone);
        }
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}
