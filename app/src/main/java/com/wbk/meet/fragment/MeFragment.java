package com.wbk.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wbk.framework.base.BaseFragment;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.bmob.IMUser;
import com.wbk.framework.helper.GlideHelper;
import com.wbk.meet.R;
import com.wbk.meet.ui.NewFriendActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private CircleImageView mIvMePortrait;
    private TextView mTvNickname;
    private LinearLayout mLlMeInfo;
    private LinearLayout mLlNewFriend;
    private LinearLayout mLlPrivateSet;
    private LinearLayout mLlShare;
    private LinearLayout mLlSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mIvMePortrait = view.findViewById(R.id.iv_me_portrait);
        mTvNickname = view.findViewById(R.id.tv_nickname);
        mLlMeInfo = view.findViewById(R.id.ll_me_info);
        mLlNewFriend = view.findViewById(R.id.ll_new_friend);
        mLlPrivateSet = view.findViewById(R.id.ll_private_set);
        mLlShare = view.findViewById(R.id.ll_share);
        mLlSetting = view.findViewById(R.id.ll_setting);

        mLlMeInfo.setOnClickListener(this);
        mLlNewFriend.setOnClickListener(this);
        mLlPrivateSet.setOnClickListener(this);
        mLlShare.setOnClickListener(this);
        mLlSetting.setOnClickListener(this);

        loadMeInfo();
    }

    private void loadMeInfo() {
        IMUser imUser = BmobManager.getInstance().getUser();
        GlideHelper.loadUrl(getActivity(), imUser.getPortrait(), mIvMePortrait);
        mTvNickname.setText(imUser.getNickname());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_friend:
                startActivity(new Intent(getActivity(), NewFriendActivity.class));
                break;
            default:
                break;
        }
    }
}
