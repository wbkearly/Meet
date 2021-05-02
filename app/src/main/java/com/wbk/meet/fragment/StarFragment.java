package com.wbk.meet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moxun.tagcloudlib.view.TagCloudView;
import com.wbk.framework.base.BaseFragment;
import com.wbk.meet.R;
import com.wbk.meet.adapter.CloudTagAdapter;
import com.wbk.meet.ui.AddFriendActivity;

import java.util.ArrayList;
import java.util.List;

public class StarFragment extends BaseFragment implements View.OnClickListener {

    private List<String> myList = new ArrayList<>();

    private CloudTagAdapter mCloudTagAdapter;
    private TagCloudView mCloudView;
    private ImageView mIvCamera;
    private ImageView mIvAdd;
    private LinearLayout mLlRandom;
    private LinearLayout mLlSoul;
    private LinearLayout mLlFate;
    private LinearLayout mLlLove;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mIvCamera = view.findViewById(R.id.iv_camera);
        mIvAdd = view.findViewById(R.id.iv_add);
        mLlRandom = view.findViewById(R.id.ll_random);
        mLlSoul = view.findViewById(R.id.ll_soul);
        mLlFate = view.findViewById(R.id.ll_fate);
        mLlLove = view.findViewById(R.id.ll_love);
        mCloudView = view.findViewById(R.id.cloud_view);
        // TO REMOVE
        for (int i = 0; i < 100; i++) {
            myList.add("star" + i);
        }
        mCloudTagAdapter = new CloudTagAdapter(myList, getActivity());
        mCloudView.setAdapter(mCloudTagAdapter);

        mCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                // TODO
            }
        });

        mIvAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.iv_camera:
                break;
            case R.id.ll_random:
                break;
            case R.id.ll_soul:
                break;
            case R.id.ll_fate:
                break;
            case R.id.ll_love:
                break;
            default:
                break;
        }
    }
}
