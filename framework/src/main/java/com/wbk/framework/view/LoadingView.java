package com.wbk.framework.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.wbk.framework.R;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.utils.AnimUtil;

public class LoadingView {

    private DialogView mLoadingView;
    private ImageView mIvLoading;
    private TextView mTvLoadingText;
    private ObjectAnimator mAnim;


    public LoadingView(Context context) {
        mLoadingView = DialogManager.getInstance().initView(context, R.layout.dialog_loading);
        mIvLoading = mLoadingView.findViewById(R.id.iv_loading);
        mTvLoadingText = mLoadingView.findViewById(R.id.tv_loading_text);
        mAnim = AnimUtil.rotation(mIvLoading);
    }

    public void setTvLoadingText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mTvLoadingText.setText(text);
        }
    }

    public void show() {
        mAnim.start();
        DialogManager.getInstance().show(mLoadingView);
    }

    public void show(String text) {
        mAnim.start();
        setTvLoadingText(text);
        DialogManager.getInstance().show(mLoadingView);
    }

    public void hide() {
        mAnim.pause();
        DialogManager.getInstance().hide(mLoadingView);
    }
}
