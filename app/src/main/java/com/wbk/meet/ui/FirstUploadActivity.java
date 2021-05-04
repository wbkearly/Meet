package com.wbk.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.wbk.framework.base.BaseBackActivity;
import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.helper.FileHelper;
import com.wbk.framework.helper.UploadHelper;
import com.wbk.framework.manager.DialogManager;
import com.wbk.framework.utils.LogUtils;
import com.wbk.framework.view.DialogView;
import com.wbk.framework.view.LoadingView;
import com.wbk.meet.R;

import java.io.File;

import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;

public class FirstUploadActivity extends BaseBackActivity implements View.OnClickListener {

    private TextView mTvCamera;
    private TextView mTvAlbum;
    private TextView mTvCancel;

    /**
     * 跳转
     */
    public static void startActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, FirstUploadActivity.class);
        activity.startActivityForResult(intent, requestCode);

    }

    private CircleImageView mIvPortrait;
    private EditText mEtNickname;
    private Button mBtnUpload;

    private LoadingView mLoadingView;
    private DialogView mPortraitSelectView;

    private File mUploadFile = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_upload);

        initViews();
    }

    private void initViews() {

        initPortraitView();

        mIvPortrait = findViewById(R.id.iv_portrait);
        mEtNickname = findViewById(R.id.et_nickname);
        mBtnUpload = findViewById(R.id.btn_upload);

        mIvPortrait.setOnClickListener(this);
        mBtnUpload.setOnClickListener(this);
        mBtnUpload.setEnabled(false);

        mEtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mBtnUpload.setEnabled(mUploadFile != null);
                } else {
                    mBtnUpload.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initPortraitView() {

        mLoadingView = new LoadingView(this);
        mLoadingView.setTvLoadingText("正在上传头像...");

        mPortraitSelectView = DialogManager.getInstance().initView(this, R.layout.dialog_select_portrait, Gravity.BOTTOM);
        mTvCamera = mPortraitSelectView.findViewById(R.id.tv_camera);
        mTvAlbum = mPortraitSelectView.findViewById(R.id.tv_album);
        mTvCancel = mPortraitSelectView.findViewById(R.id.tv_cancel);

        mTvCamera.setOnClickListener(this);
        mTvAlbum.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                DialogManager.getInstance().hide(mPortraitSelectView);
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.tv_album:
                DialogManager.getInstance().hide(mPortraitSelectView);
                FileHelper.getInstance().toAlbum(this);
                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mPortraitSelectView);
                break;
            case R.id.iv_portrait:
                // 显示选择提示框
                DialogManager.getInstance().show(mPortraitSelectView);
                break;
            case R.id.btn_upload:
                uploadPhoto();
                break;
            default:
                break;
        }
    }

    private void uploadPhoto() {
        String nickname = mEtNickname.getText().toString().trim();
        mLoadingView.show();
        String portraitUrl = UploadHelper.getInstance().uploadPortrait(mUploadFile.getPath());
        if (portraitUrl == null) {
            mLoadingView.hide();
            Toast.makeText(FirstUploadActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtils.i("portraitUrl:" + portraitUrl);
        BmobManager.getInstance().updateUserInfo(nickname, portraitUrl, new BmobManager.OnUploadListener() {
            @Override
            public void onUploadSuccess() {
                mLoadingView.hide();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onUpdateFail(BmobException e) {
                mLoadingView.hide();
                Toast.makeText(FirstUploadActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FileHelper.CAMERA_REQUEST_CODE) {
                mUploadFile = FileHelper.getInstance().getTempFile();
            } else if (requestCode == FileHelper.ALBUM_REQUEST_CODE) {
                assert data != null;
                Uri uri = data.getData();
                if (uri != null) {
                    // 获取真实地址
                    String path = FileHelper.getInstance().getRealPathFromUri(this, uri);
                    LogUtils.i("path:" + path);
                    if (!TextUtils.isEmpty(path)) {
                        mUploadFile = new File(path);
                    }
                }
            }
        }
        if (mUploadFile != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(mUploadFile.getPath());
            mIvPortrait.setImageBitmap(bitmap);

            // 判断当前输入框
            String nickname = mEtNickname.getText().toString().trim();
            mBtnUpload.setEnabled(!TextUtils.isEmpty(nickname));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
